package org.springframework.samples.petclinic.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Observability configuration for metrics and tracing.
 *
 * <p>
 * This class configures Micrometer for metrics collection and OpenTelemetry for distributed tracing.
 * It sets up meter registries, a tracer, and observation aspects to provide comprehensive observability.
 * </p>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
public class ObservabilityConfig {

  /**
   * Flag to enable Prometheus registry via application.properties:
   * metrics.prometheus.enabled=true
   */
  @Value("${metrics.prometheus.enabled:false}")
  private boolean prometheusEnabled;

  // --- Meter Registries ---

  /**
   * Creates a simple meter registry for basic in-memory metrics.
   *
   * @return The simple meter registry.
   */
  @Bean
  public MeterRegistry simpleMeterRegistry() {
    return new SimpleMeterRegistry();
  }

  /**
   * Creates a Prometheus meter registry if enabled in the application properties.
   *
   * @return The Prometheus meter registry, or null if disabled.
   */
  @Bean
  public PrometheusMeterRegistry prometheusMeterRegistry() {
    if (prometheusEnabled) {
      return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
    return null;
  }

  /**
   * Creates a composite meter registry that combines all available meter registries.
   *
   * @param registries A list of meter registries to combine.
   * @return The composite meter registry.
   */
  @Bean
  public CompositeMeterRegistry compositeMeterRegistry(@Autowired(required = false) List<MeterRegistry> registries) {
    CompositeMeterRegistry composite = new CompositeMeterRegistry();
    if (registries != null) {
      registries.stream()
          .filter(r -> r != null)
          .forEach(composite::add);
    }
    return composite;
  }

  // --- OpenTelemetry Tracing ---

  /**
   * Creates an SDK tracer provider for OpenTelemetry.
   *
   * @return The SDK tracer provider.
   */
  @Bean
  @SuppressWarnings("deprecation")
  public SdkTracerProvider sdkTracerProvider() {
    // Uses a logging span exporter for demonstration purposes.
    SpanExporter spanExporter = io.opentelemetry.exporter.logging.LoggingSpanExporter.create();

    // Sets the service name for the resource.
    Resource resource = Resource.getDefault()
        .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "spring-petclinic")));

    // Builds the SDK tracer provider with a batch span processor and a sampler that always samples.
    return SdkTracerProvider.builder()
        .setSampler(Sampler.alwaysOn())
        .setResource(resource)
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .build();
  }

  /**
   * Creates an OpenTelemetry instance with the configured tracer provider and propagators.
   *
   * @param sdkTracerProvider The SDK tracer provider.
   * @return The OpenTelemetry instance.
   */
  @Bean
  public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider) {
    // Configures B3 propagation for distributed tracing.
    ContextPropagators contextPropagators = ContextPropagators.create(
        TextMapPropagator.composite(
            B3Propagator.injectingMultiHeaders(),
            B3Propagator.injectingSingleHeader()));

    return OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(contextPropagators)
        .build();
  }

  /**
   * Creates a Micrometer tracer that bridges to the OpenTelemetry tracer.
   *
   * @param openTelemetry The OpenTelemetry instance.
   * @return The Micrometer tracer.
   */
  @Bean
  public Tracer tracer(OpenTelemetry openTelemetry) {
    return new OtelTracer(openTelemetry.getTracer("spring-petclinic"), new OtelCurrentTraceContext(), event -> {
    });
  }

  // --- Micrometer Observation ---

  /**
   * Creates an observation registry for Micrometer Observation.
   *
   * @param tracer The tracer.
   * @param meterRegistry The composite meter registry.
   * @return The observation registry.
   */
  @Bean
  public ObservationRegistry observationRegistry(Tracer tracer, CompositeMeterRegistry meterRegistry) {
    ObservationRegistry registry = ObservationRegistry.create();
    registry.observationConfig()
        .observationHandler(new DefaultMeterObservationHandler(meterRegistry));
    return registry;
  }

  // --- Aspects ---

  /**
   * Creates a TimedAspect for the @Timed annotation.
   *
   * @param registry The composite meter registry.
   * @return The TimedAspect.
   */
  @Bean
  public TimedAspect timedAspect(CompositeMeterRegistry registry) {
    return new TimedAspect(registry);
  }

  /**
   * Creates an ObservedAspect for the @Observed annotation.
   *
   * @param observationRegistry The observation registry.
   * @return The ObservedAspect.
   */
  @Bean
  public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }
}
