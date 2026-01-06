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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoggingAspectTests {

  @Test
  void logAroundShouldProceedAndReturnResult() throws Throwable {
    LoggingAspect aspect = new LoggingAspect();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    Signature signature = mock(Signature.class);

    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getDeclaringTypeName()).thenReturn("com.example.Service");
    when(signature.getName()).thenReturn("doSomething");
    when(joinPoint.getArgs()).thenReturn(new Object[] {});
    when(joinPoint.proceed()).thenReturn("Success");

    Object result = aspect.logAround(joinPoint);

    Assertions.assertEquals("Success", result);
    verify(joinPoint).proceed();
  }

  @Test
  void logAroundShouldThrowException() throws Throwable {
    LoggingAspect aspect = new LoggingAspect();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    Signature signature = mock(Signature.class);

    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getDeclaringTypeName()).thenReturn("com.example.Service");
    when(signature.getName()).thenReturn("doError");
    when(joinPoint.getArgs()).thenReturn(new Object[] {});
    when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("Error"));

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          aspect.logAround(joinPoint);
        });
  }
}


