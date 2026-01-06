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

import org.junit.jupiter.api.Test;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

class ResilienceConfigTests {

  @Test
  void registriesShouldBeCreated() throws Exception {
    ResilienceConfig config = new ResilienceConfig();
    // Set required fields to valid values
    java.lang.reflect.Field failureRateThresholdField = ResilienceConfig.class.getDeclaredField("failureRateThreshold");
    failureRateThresholdField.setAccessible(true);
    failureRateThresholdField.set(config, 50f);
    java.lang.reflect.Field waitDurationInOpenStateField = ResilienceConfig.class.getDeclaredField("waitDurationInOpenState");
    waitDurationInOpenStateField.setAccessible(true);
    waitDurationInOpenStateField.set(config, 1000L);
    java.lang.reflect.Field slidingWindowSizeField = ResilienceConfig.class.getDeclaredField("slidingWindowSize");
    slidingWindowSizeField.setAccessible(true);
    slidingWindowSizeField.set(config, 20);
    java.lang.reflect.Field limitRefreshPeriodField = ResilienceConfig.class.getDeclaredField("limitRefreshPeriod");
    limitRefreshPeriodField.setAccessible(true);
    limitRefreshPeriodField.set(config, 1);
    java.lang.reflect.Field limitForPeriodField = ResilienceConfig.class.getDeclaredField("limitForPeriod");
    limitForPeriodField.setAccessible(true);
    limitForPeriodField.set(config, 100);
    java.lang.reflect.Field timeoutDurationField = ResilienceConfig.class.getDeclaredField("timeoutDuration");
    timeoutDurationField.setAccessible(true);
    timeoutDurationField.set(config, 25);
    java.lang.reflect.Field retryMaxAttemptsField = ResilienceConfig.class.getDeclaredField("retryMaxAttempts");
    retryMaxAttemptsField.setAccessible(true);
    retryMaxAttemptsField.set(config, 3);
    java.lang.reflect.Field retryWaitDurationField = ResilienceConfig.class.getDeclaredField("retryWaitDuration");
    retryWaitDurationField.setAccessible(true);
    retryWaitDurationField.set(config, 500L);

    CircuitBreakerRegistry cbRegistry = config.circuitBreakerRegistry();
    RateLimiterRegistry rlRegistry = config.rateLimiterRegistry();

    assertNotNull(cbRegistry);
    assertNotNull(rlRegistry);
  }
}


