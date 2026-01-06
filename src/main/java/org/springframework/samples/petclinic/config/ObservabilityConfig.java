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
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(ObservabilityConfig.class);

  /**
   * Flag to enable Prometheus registry via application.properties:
   * metrics.prometheus.enabled=true
   */
  @Value("${metrics.prometheus.enabled:false}")
  private boolean prometheusEnabled;

  /**
   * Sampler type: 'traceidratio' (default) or 'always_on'
   * otel.traces.sampler.type=traceidratio
   */
  @Value("${otel.traces.sampler.type:traceidratio}")
  private String samplerType;

  /**
   * Sampling ratio (0.0 to 1.0) when using traceidratio sampler
   * otel.traces.sampler.arg=0.1 (10% sampling)
   */
  @Value("${otel.traces.sampler.arg:0.1}")
  private double samplingRatio;

  /**
   * OTLP Exporter endpoint
   * otel.exporter.otlp.endpoint=http://localhost:4317
   */
  @Value("${otel.exporter.otlp.endpoint:http://localhost:4317}")
  private String otlpEndpoint;

  /**
   * OTLP Exporter protocol: 'grpc' or 'http/protobuf'
   * otel.exporter.otlp.protocol=grpc
   */
  @Value("${otel.exporter.otlp.protocol:grpc}")
  private String otlpProtocol;

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
   * Creates an SDK tracer provider for OpenTelemetry with configurable sampling.
   * Uses OTLP gRPC exporter for distributed tracing infrastructure.
   * Falls back to logging exporter if OTLP is unavailable.
   *
   * @return The SDK tracer provider.
   */
  @Bean
  public SdkTracerProvider sdkTracerProvider() {
    // Create OTLP gRPC span exporter
    SpanExporter spanExporter;
    try {
      spanExporter = OtlpGrpcSpanExporter.builder()
          .setEndpoint(otlpEndpoint)
          .build();
      logger.info("OTLP Exporter configured: {}", otlpEndpoint);
    } catch (Exception e) {
      // Fallback to logging exporter if OTLP is unavailable
      logger.warn("OTLP Exporter unavailable ({}), falling back to logging exporter", e.getMessage());
      spanExporter = LoggingSpanExporter.create();
    }

    // Configure sampler based on property
    Sampler sampler = createSampler();
    logger.info("OTEL Sampler configured: {} with ratio {}", samplerType, samplingRatio);

    // Sets the service name for the resource.
    Resource resource = Resource.getDefault()
        .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "spring-petclinic")));

    // Builds the SDK tracer provider with a batch span processor and configurable sampler.
    return SdkTracerProvider.builder()
        .setSampler(sampler)
        .setResource(resource)
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .build();
  }

  /**
   * Creates a sampler based on configuration.
   *
   * @return The configured Sampler.
   */
  private Sampler createSampler() {
    Sampler baseSampler;

    if ("always_on".equalsIgnoreCase(samplerType)) {
      baseSampler = Sampler.alwaysOn();
      logger.info("Using AlwaysOn sampler: 100% sampling");
    } else if ("traceidratio".equalsIgnoreCase(samplerType)) {
      baseSampler = Sampler.traceIdRatioBased(samplingRatio);
      logger.info("Using TraceIdRatioBased sampler: {}% sampling", samplingRatio * 100);
    } else {
      // Default to 10% sampling
      baseSampler = Sampler.traceIdRatioBased(0.1);
      logger.warn("Unknown sampler type: {}, defaulting to 10% TraceIdRatioBased", samplerType);
    }

    // Wrap with ParentBasedSampler to respect parent span decisions
    return Sampler.parentBased(baseSampler);
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
