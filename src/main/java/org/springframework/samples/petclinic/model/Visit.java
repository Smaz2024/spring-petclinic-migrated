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

import java.time.LocalDate;
import java.util.logging.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(Visit.class.getName());

  @Column(name = "visit_date")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull
  private LocalDate date;

  @NotEmpty
  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "pet_id")
  @NotNull
  private Pet pet;

  public Visit() {
    this.date = LocalDate.now();
  }

  public LocalDate getDate() {
    logger.info("Getting date: " + date);
    return this.date;
  }

  public void setDate(LocalDate date) {
    logger.info("Setting date to: " + date);
    this.date = date;
  }

  public String getDescription() {
    logger.info("Getting description: " + description);
    return this.description;
  }

  public void setDescription(String description) {
    logger.info("Setting description to: " + description);
    this.description = description;
  }

  public Pet getPet() {
    logger.info("Getting pet: " + pet);
    return this.pet;
  }

  public void setPet(Pet pet) {
    logger.info("Setting pet to: " + pet);
    this.pet = pet;
  }
}
