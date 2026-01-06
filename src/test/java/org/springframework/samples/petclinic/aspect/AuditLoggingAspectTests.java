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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.AuditLogRepository;

class AuditLoggingAspectTests {

  private AuditLogRepository auditLogRepository;
  private AuditLoggingAspect aspect;

  @BeforeEach
  void setup() {
    auditLogRepository = mock(AuditLogRepository.class);
    aspect = new AuditLoggingAspect(auditLogRepository);
  }

  @Test
  void logSaveShouldSaveAuditLogForBaseEntity() {
    JoinPoint joinPoint = mock(JoinPoint.class);
    Owner owner = new Owner();
    owner.setId(1L);

    when(joinPoint.getArgs()).thenReturn(new Object[] { owner });

    aspect.logSave(joinPoint, owner);

    verify(auditLogRepository, times(1)).save(any());
  }

  @Test
  void logSaveShouldDoNothingForNonBaseEntity() {
    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getArgs()).thenReturn(new Object[] { "NotAnEntity" });

    aspect.logSave(joinPoint, "NotAnEntity");

    verify(auditLogRepository, times(0)).save(any());
  }
}


