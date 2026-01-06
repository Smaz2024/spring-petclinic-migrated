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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;

/**
 * Aspect for recording custom business metrics using Micrometer.
 *
 * <p>
 * This aspect captures high-level business events that are not easily
 * tracked by standard performance monitoring. It provides custom counters
 * for critical business actions.
 *
 * <p>
 * Metrics tracked:
 * <ul>
 * <li>{@code petclinic.owners.created}: Incremented on successful
 * {@code saveOwner} calls.</li>
 * <li>{@code petclinic.visits.created}: Incremented on successful
 * {@code saveVisit} calls.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Aspect
@Component
public class MetricsAspect {

  // Counter for tracking the number of created owners.
  private final Counter ownersCreated;
  // Counter for tracking the number of created visits.
  private final Counter visitsCreated;

  /**
   * Constructor to initialize the metrics counters.
   *
   * @param registry The meter registry to register the counters with.
   */
  @Autowired
  public MetricsAspect(io.micrometer.core.instrument.composite.CompositeMeterRegistry registry) {
    // Initialize the counter for created owners.
    this.ownersCreated = Counter.builder("petclinic.owners.created")
        .description("Number of owners created")
        .register(registry);

    // Initialize the counter for created visits.
    this.visitsCreated = Counter.builder("petclinic.visits.created")
        .description("Number of visits created")
        .register(registry);
  }

  /**
   * Advice that increments the owner creation counter after an owner is saved.
   *
   * @param joinPoint The join point for the advised method.
   */
  @AfterReturning("execution(* org.springframework.samples.petclinic.service.ClinicService.saveOwner(..))")
  public void countOwnerCreation(JoinPoint joinPoint) {
    ownersCreated.increment();
  }

  /**
   * Advice that increments the visit creation counter after a visit is saved.
   *
   * @param joinPoint The join point for the advised method.
   */
  @AfterReturning("execution(* org.springframework.samples.petclinic.service.ClinicService.saveVisit(..))")
  public void countVisitCreation(JoinPoint joinPoint) {
    visitsCreated.increment();
  }
}
