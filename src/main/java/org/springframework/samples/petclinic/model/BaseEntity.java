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

import java.io.Serializable;
import java.util.logging.Logger;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Base class for all domain objects.
 *
 * <p>
 * Provides a consistent {@link Id} property and an {@code isNew()} check.
 * Uses {@link GenerationType#IDENTITY} which is optimal for PostgreSQL.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BaseEntity.class.getName());


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @XmlElement
  public Long getId() {
    logger.info("Getting ID: " + id);
    return id;
  }

  public void setId(Long id) {
    logger.info("Setting ID to: " + id);
    this.id = id;
  }

  public boolean isNew() {
    return this.id == null;
  }
}
