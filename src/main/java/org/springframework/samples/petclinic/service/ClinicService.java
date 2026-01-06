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
package org.springframework.samples.petclinic.service;

import java.util.Collection;

import jakarta.validation.constraints.NotNull;

import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;

/**
 * Primarily used as a facade so all controllers have a single point of entry.
 *
 * <p>
 * This interface defines the business logic for the Petclinic application.
 * It coordinates actions between repositories and provides transactional
 * boundaries for business operations.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
public interface ClinicService {

  /**
   * Retrieves all pet types from the data store.
   *
   * @return a collection of pet types
   */
  @NonNull
  Collection<PetType> findPetTypes();

  /**
   * Retrieves an owner by their ID.
   *
   * @param id the ID of the owner to retrieve
   * @return the owner if found, or null otherwise
   */
  Owner findOwnerById(@NonNull Long id);

  /**
   * Retrieves a paginated list of owners whose last name starts with the given string.
   *
   * @param lastName the last name to search for
   * @param pageable the pagination information
   * @return a page of matching owners
   */
  @NonNull
  Page<Owner> findOwnerByLastName(String lastName, @NonNull Pageable pageable);

  /**
   * Saves a new or existing owner.
   *
   * @param owner the owner to save
   */
  void saveOwner(@NotNull Owner owner);

  /**
   * Saves a new or existing visit.
   *
   * @param visit the visit to save
   */
  void saveVisit(@NotNull Visit visit);

  /**
   * Retrieves a pet by their ID.
   *
   * @param id the ID of the pet to retrieve
   * @return the pet if found, or null otherwise
   */
  Pet findPetById(@NonNull Long id);

  /**
   * Saves a new or existing pet.
   *
   * @param pet the pet to save
   */
  void savePet(@NotNull Pet pet);

  /**
   * Retrieves all veterinarians from the data store.
   *
   * @return a collection of veterinarians
   */
  @NonNull
  Collection<Vet> findVets();
}
