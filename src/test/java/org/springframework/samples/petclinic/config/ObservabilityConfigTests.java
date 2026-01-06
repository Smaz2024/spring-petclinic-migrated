/*
 * Copyright 2002-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.trace.SdkTracerProvider;

class ObservabilityConfigTests {

  @Test
  void beansShouldBeCreated() {
    ObservabilityConfig config = new ObservabilityConfig();
    ReflectionTestUtils.setField(config, "prometheusEnabled", true);

    assertNotNull(config.prometheusMeterRegistry());

    SdkTracerProvider sdkTracerProvider = config.sdkTracerProvider();
    assertNotNull(sdkTracerProvider);

    OpenTelemetry openTelemetry = config.openTelemetry(sdkTracerProvider);
    assertNotNull(openTelemetry);

    Tracer tracer = config.tracer(openTelemetry);
    assertNotNull(tracer);

    CompositeMeterRegistry meterRegistry = mock(CompositeMeterRegistry.class);
    ObservationRegistry observationRegistry = config.observationRegistry(tracer, meterRegistry);
    assertNotNull(observationRegistry);

    assertNotNull(config.timedAspect(meterRegistry));
    assertNotNull(config.observedAspect(observationRegistry));
  }
}
