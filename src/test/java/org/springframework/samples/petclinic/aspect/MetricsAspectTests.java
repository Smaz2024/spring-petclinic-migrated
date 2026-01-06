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
package org.springframework.samples.petclinic.aspect;

import static org.mockito.Mockito.mock;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

class MetricsAspectTests {

  private MetricsAspect aspect;

  @BeforeEach
  void setup() {
    mock(MeterRegistry.class);
    mock(Counter.class);
    Counter.Builder builder = mock(Counter.Builder.class);

    // Mocking the builder chain for Counter
    // Note: Use Mockito.mockStatic for static methods if verifying parameters,
    // but here we just need to ensure the aspect initializes without error and
    // increments
    // internal counters?
    // Wait, the aspect creates counters in constructor.
    // Counter.builder(...) return a builder, calling register(registry) returns a
    // Counter.
    // I need to mock the static method Counter.builder or passing a registry that
    // returns mocked
    // counters is hard because it's static.
    // Actually, simple unit testing of this Aspect is hard because of
    // Counter.builder() static call
    // unless I use Mockito-inline (which supports static mocks).
    // Alternatively, I can use a SimpleMeterRegistry.

  }

  @Test
  void shouldIncrementCounters() {
    io.micrometer.core.instrument.simple.SimpleMeterRegistry simpleRegistry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    io.micrometer.core.instrument.composite.CompositeMeterRegistry composite = new io.micrometer.core.instrument.composite.CompositeMeterRegistry();
    composite.add(simpleRegistry);
    aspect = new MetricsAspect(composite);

    JoinPoint joinPoint = mock(JoinPoint.class);
    aspect.countOwnerCreation(joinPoint);
    aspect.countVisitCreation(joinPoint);

    Assertions.assertEquals(1.0, simpleRegistry.get("petclinic.owners.created").counter().count());
    Assertions.assertEquals(1.0, simpleRegistry.get("petclinic.visits.created").counter().count());
  }
}
