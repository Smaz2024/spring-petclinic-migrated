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
package org.springframework.samples.petclinic.model;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Audit log entity for compliance tracking.
 *
 * <p>
 * Stores historical data about changes to business entities, including
 * who made the change, when it occurred, and the associated trace ID for
 * distributed tracing correlation.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Entity
@Table(name = "audit_log")
public class AuditLog extends BaseEntity {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(AuditLog.class.getName());

  @Column(name = "entity_type")
  @NotNull
  private String entityType;

  @Column(name = "entity_id")
  private Long entityId;

  @Column(name = "action")
  @NotNull
  private String action;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "timestamp")
  @NotNull
  private LocalDateTime timestamp;

  @Column(name = "old_value", columnDefinition = "TEXT")
  private String oldValue;

  @Column(name = "new_value", columnDefinition = "TEXT")
  private String newValue;

  @Column(name = "trace_id")
  private String traceId;

  @Column(name = "ip_address")
  private String ipAddress;

  public AuditLog() {
    this.timestamp = LocalDateTime.now();
  }

  // Getters and Setters
  public String getEntityType() {
    logger.info("Getting entity type: " + entityType);
    return entityType;
  }

  public void setEntityType(String entityType) {
    logger.info("Setting entity type to: " + entityType);
    this.entityType = entityType;
  }

  public Long getEntityId() {
    logger.info("Getting entity ID: " + entityId);
    return entityId;
  }

  public void setEntityId(Long entityId) {
    logger.info("Setting entity ID to: " + entityId);
    this.entityId = entityId;
  }

  public String getAction() {
    logger.info("Getting action: " + action);
    return action;
  }

  public void setAction(String action) {
    logger.info("Setting action to: " + action);
    this.action = action;
  }

  public String getUserId() {
    logger.info("Getting user ID: " + userId);
    return userId;
  }

  public void setUserId(String userId) {
    logger.info("Setting user ID to: " + userId);
    this.userId = userId;
  }

  public LocalDateTime getTimestamp() {
    logger.info("Getting timestamp: " + timestamp);
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    logger.info("Setting timestamp to: " + timestamp);
    this.timestamp = timestamp;
  }

  public String getOldValue() {
    logger.info("Getting old value: " + oldValue);
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    logger.info("Setting old value to: " + oldValue);
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    logger.info("Getting new value: " + newValue);
    return newValue;
  }

  public void setNewValue(String newValue) {
    logger.info("Setting new value to: " + newValue);
    this.newValue = newValue;
  }

  public String getTraceId() {
    logger.info("Getting trace ID: " + traceId);
    return traceId;
  }

  public void setTraceId(String traceId) {
    logger.info("Setting trace ID to: " + traceId);
    this.traceId = traceId;
  }

  public String getIpAddress() {
    logger.info("Getting IP address: " + ipAddress);
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    logger.info("Setting IP address to: " + ipAddress);
    this.ipAddress = ipAddress;
  }
}
