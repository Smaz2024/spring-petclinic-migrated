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
package org.springframework.samples.petclinic.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Entity
@Table(name = "vets")
@XmlRootElement
public class Vet extends Person {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(Vet.class.getName());

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"), inverseJoinColumns = @JoinColumn(name = "specialty_id"))
  private Set<Specialty> specialties;

  protected Set<Specialty> getSpecialtiesInternal() {
    if (this.specialties == null) {
      this.specialties = new HashSet<>();
    }
    return this.specialties;
  }

  protected void setSpecialtiesInternal(Set<Specialty> specialties) {
    this.specialties = specialties;
  }

  @XmlElement
  public List<Specialty> getSpecialties() {
    logger.info("Getting specialties");
    List<Specialty> sortedSpecs = new ArrayList<>(getSpecialtiesInternal());
    PropertyComparator.sort(sortedSpecs, new MutableSortDefinition("name", true, true));
    return Collections.unmodifiableList(sortedSpecs);
  }

  public int getNrOfSpecialties() {
    logger.info("Getting number of specialties");
    return getSpecialtiesInternal().size();
  }

  public void addSpecialty(Specialty specialty) {
    logger.info("Adding specialty: " + specialty);
    getSpecialtiesInternal().add(specialty);
  }
}
