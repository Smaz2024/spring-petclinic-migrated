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
 * distributed under the License is an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.aspect;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.AuditLog;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aspect for auditing persistence actions.
 *
 * <p>
 * This aspect captures successful save operations in the {@code ClinicService}
 * and records them in the audit log table. It captures entity type, ID,
 * action (CREATE/UPDATE), timestamp, user ID, and trace ID.
 *
 * <p>
 * Integration:
 * <ul>
 * <li>Pointcut: {@code execution(* ClinicService.save*(..))}</li>
 * <li>Metadata: Captures {@code traceId} from MDC.</li>
 * <li>Security: Retrieves current username from
 * {@link SecurityContextHolder}.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Aspect
@Component
public class AuditLoggingAspect {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final AuditLogRepository auditLogRepository;

  /**
   * Constructor to inject the AuditLogRepository.
   *
   * @param auditLogRepository The repository for saving audit logs.
   */
  @Autowired
  public AuditLoggingAspect(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  /**
   * Advice that runs after a successful save operation.
   *
   * <p>
   * Constructs an {@link AuditLog} entry based on the saved entity and
   * persists it to the database.
   *
   * @param joinPoint the join point containing method arguments
   * @param result    the returned object from the saved method
   */
  @AfterReturning(pointcut = "execution(* org.springframework.samples.petclinic.service.ClinicService.save*(..))", returning = "result")
  @Transactional
  public void logSave(JoinPoint joinPoint, Object result) {
    try {
      // Get the arguments from the join point.
      Object[] args = joinPoint.getArgs();
      // Check if the first argument is a BaseEntity.
      if (args.length > 0 && args[0] instanceof BaseEntity) {
        BaseEntity entity = (BaseEntity) args[0];

        // Create a new AuditLog entry.
        AuditLog audit = new AuditLog();
        audit.setEntityType(entity.getClass().getSimpleName());
        audit.setEntityId(entity.getId());
        audit.setAction(entity.isNew() ? "CREATE" : "UPDATE");
        audit.setTimestamp(LocalDateTime.now());

        // Retrieve real username from SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : "anonymous";
        audit.setUserId(username);

        // Get the trace ID from MDC.
        audit.setTraceId(MDC.get("traceId"));

        // For simplified auditing, we just log providing the entity string (which masks
        // PII)
        audit.setNewValue(entity.toString());

        // Save the audit log.
        auditLogRepository.save(audit);
        log.info(
            "AUDIT: Action={} Entity={} ID={}",
            audit.getAction(),
            audit.getEntityType(),
            audit.getEntityId());
      }
    } catch (Exception e) {
      // Log any errors that occur during the audit process.
      log.error("Failed to save audit log", e);
    }
  }
}
