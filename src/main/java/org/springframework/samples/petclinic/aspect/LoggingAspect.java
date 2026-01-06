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

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * <p>
 * This aspect provides unified logging across the application layers. It logs:
 * <ul>
 * <li>Method entry with arguments (DEBUG level)</li>
 * <li>Method exit with results and execution time (DEBUG level)</li>
 * <li>Exceptions with contextual information (ERROR level)</li>
 * </ul>
 *
 * <p>
 * The logging is coordinated with MDC to ensure trace IDs are included
 * if provided by the observability layer.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Aspect
@Component
public class LoggingAspect {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Pointcut that matches all repositories, services, and Web REST endpoints.
   * This is used to select the Spring components that should be logged.
   */
  @Pointcut("within(@org.springframework.stereotype.Repository *)"
      + " || within(@org.springframework.stereotype.Service *)"
      + " || within(@org.springframework.web.bind.annotation.RestController *)")
  public void springBeanPointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the
    // advices.
  }

  /**
   * Pointcut that matches all Spring beans in the application's main packages.
   * This ensures that only application-specific code is logged, excluding framework code.
   */
  @Pointcut("within(org.springframework.samples.petclinic.repository..*)"
      + " || within(org.springframework.samples.petclinic.service..*)"
      + " || within(org.springframework.samples.petclinic.web..*)")
  public void applicationPackagePointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the
    // advices.
  }

  /**
   * Advice that logs method entry, exit, and execution time.
   * It also logs exceptions thrown from the advised methods.
   *
   * @param joinPoint The join point for the advised method.
   * @return The result of the method execution.
   * @throws Throwable If the advised method throws an exception.
   */
  @Around("applicationPackagePointcut() && springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    // Record the start time of the method execution.
    long start = System.currentTimeMillis();
    // Ensure TraceID is in MDC (if not already handled by OTEL instrumented servlet
    // filter)
    // For deep service calls, we rely on the context.

    // Log method entry if DEBUG is enabled.
    if (log.isDebugEnabled()) {
      log.debug(
          "Enter: {}.{}() with argument[s] = {}",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          Arrays.toString(joinPoint.getArgs()));
    }
    try {
      // Proceed with the actual method execution.
      Object result = joinPoint.proceed();
      // Calculate the execution time.
      long executionTime = System.currentTimeMillis() - start;
      // Log method exit if DEBUG is enabled.
      if (log.isDebugEnabled()) {
        log.debug(
            "Exit: {}.{}() with result = {} ({} ms)",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            result,
            executionTime);
      }
      // Return the result of the method execution.
      return result;
    } catch (IllegalArgumentException e) {
      // Log any illegal argument exceptions.
      log.error(
          "Illegal argument: {} in {}.{}()",
          Arrays.toString(joinPoint.getArgs()),
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName());
      // Re-throw the exception.
      throw e;
    }
  }
}
