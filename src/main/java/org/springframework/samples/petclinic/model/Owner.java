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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.util.DataMaskingUtil;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * <p>
 * Integrates with {@link DataMaskingUtil} to ensure PII (Personally
 * Identifiable Information)
 * such as address and telephone is masked in logs.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Entity
@Table(name = "owners")
public class Owner extends Person {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(Owner.class.getName());

  @Column(name = "address")
  @NotEmpty(message = "Address is required.")
  @jakarta.validation.constraints.Size(min = 5, max = 255, message = "Address must be at least 5 characters.")
  private String address;

  @Column(name = "city")
  @NotEmpty(message = "City is required.")
  private String city;

  @Column(name = "telephone")
  @NotEmpty(message = "Telephone is required.")
  @jakarta.validation.constraints.Pattern(regexp = "\\d{10}", message = "Telephone must be exactly 10 digits.")
  private String telephone;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
  private Set<Pet> pets;

  public String getAddress() {
    logger.info("Getting address");
    return this.address;
  }

  public void setAddress(String address) {
    logger.info("Setting address");
    this.address = address;
  }

  public String getCity() {
    logger.info("Getting city");
    return this.city;
  }

  public void setCity(String city) {
    logger.info("Setting city");
    this.city = city;
  }

  public String getTelephone() {
    logger.info("Getting telephone");
    return this.telephone;
  }

  public void setTelephone(String telephone) {
    logger.info("Setting telephone");
    this.telephone = telephone;
  }

  protected Set<Pet> getPetsInternal() {
    if (this.pets == null) {
      this.pets = new HashSet<>();
    }
    return this.pets;
  }

  protected void setPetsInternal(Set<Pet> pets) {
    this.pets = pets;
  }

  public List<Pet> getPets() {
    List<Pet> sortedPets = new ArrayList<>(getPetsInternal());
    PropertyComparator.sort(sortedPets, new MutableSortDefinition("name", true, true));
    return Collections.unmodifiableList(sortedPets);
  }

  public void addPet(Pet pet) {
    logger.info("Adding pet: " + pet);
    if (pet.isNew()) {
      getPetsInternal().add(pet);
    }
    pet.setOwner(this);
  }

  /**
   * Return the Pet with the given name, or null if none found for this Owner.
   *
   * @param name to test
   * @return true if pet name is already in use
   */
  public Pet getPet(String name) {
    logger.info("Getting pet by name: " + name);
    return getPet(name, false);
  }

  /**
   * Return the Pet with the given name, or null if none found for this Owner.
   *
   * @param name to test
   * @return true if pet name is already in use
   */
  public Pet getPet(String name, boolean ignoreNew) {
    logger.info("Getting pet by name: " + name + ", ignoreNew: " + ignoreNew);
    name = name.toLowerCase();
    for (Pet pet : getPetsInternal()) {
      if (!ignoreNew || !pet.isNew()) {
        String compName = pet.getName();
        compName = compName.toLowerCase();
        if (compName.equals(name)) {
          return pet;
        }
      }
    }
    return null;
  }

  @Override
  public String toString() {
    // Use DataMaskingUtil for PII in logs
    return new ToStringCreator(this)
        .append("id", this.getId())
        .append("new", this.isNew())
        .append("lastName", this.getLastName())
        .append("firstName", this.getFirstName())
        .append("address", DataMaskingUtil.maskAddress(this.address))
        .append("city", this.city)
        .append("telephone", DataMaskingUtil.maskTelephone(this.telephone))
        .toString();
  }
}
