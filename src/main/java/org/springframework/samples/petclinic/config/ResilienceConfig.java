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

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

/**
 * Resilience configuration using Resilience4J.
 *
 * <p>
 * This class configures fault tolerance patterns including circuit breakers
 * and rate limiters to ensure the application remains stable under heavy load
 * or during partial system failures.
 *
 * <p>
 * Patterns implemented:
 * <ul>
 * <li><b>Circuit Breaker</b>: Prevents cascading failures by tripping when
 * error rates
 * exceed 50% in a sliding window of 20 calls.</li>
 * <li><b>Rate Limiter</b>: Protects resources by limiting requests to 100 per
 * second.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
public class ResilienceConfig {

  @Value("${resilience4j.circuitbreaker.failureRateThreshold:50}")
  private float failureRateThreshold;
  @Value("${resilience4j.circuitbreaker.waitDurationInOpenState:1000}")
  private long waitDurationInOpenState;
  @Value("${resilience4j.circuitbreaker.slidingWindowSize:20}")
  private int slidingWindowSize;

  @Value("${resilience4j.ratelimiter.limitRefreshPeriod:1}")
  private int limitRefreshPeriod;
  @Value("${resilience4j.ratelimiter.limitForPeriod:100}")
  private int limitForPeriod;
  @Value("${resilience4j.ratelimiter.timeoutDuration:25}")
  private int timeoutDuration;

  @Value("${resilience4j.retry.maxAttempts:3}")
  private int retryMaxAttempts;
  @Value("${resilience4j.retry.waitDuration:500}")
  private long retryWaitDuration;

  /**
   * Configures the {@link CircuitBreakerRegistry} with custom default settings.
   *
   * @return the configured circuit breaker registry
   */
  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .failureRateThreshold(failureRateThreshold)
        .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
        .slidingWindowSize(slidingWindowSize)
        .build();
    return CircuitBreakerRegistry.of(config);
  }

  /**
   * Configures the {@link RateLimiterRegistry} with custom default settings.
   *
   * @return the configured rate limiter registry
   */
  @Bean
  public RateLimiterRegistry rateLimiterRegistry() {
    RateLimiterConfig config = RateLimiterConfig.custom()
        .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriod))
        .limitForPeriod(limitForPeriod)
        .timeoutDuration(Duration.ofMillis(timeoutDuration))
        .build();
    return RateLimiterRegistry.of(config);
  }

  @Bean
  public RetryRegistry retryRegistry() {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(retryMaxAttempts)
        .waitDuration(Duration.ofMillis(retryWaitDuration))
        .build();
    return RetryRegistry.of(config);
  }
}
