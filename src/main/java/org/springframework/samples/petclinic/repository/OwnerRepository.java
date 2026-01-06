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
package org.springframework.samples.petclinic.repository;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Owner} domain objects.
 *
 * <p>
 * Uses Spring Data JPA to provide automated implementations.
 * Includes custom queries with JOIN FETCH to optimize performance
 * by reducing N+1 selection problems.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

  /**
   * Retrieves a paginated list of owners whose last name starts with the given string.
   *
   * @param lastName The last name to search for.
   * @param pageable The pagination information.
   * @return A page of matching owners.
   */
  @Query(value = "SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE concat(:lastName, '%')", countQuery = "SELECT count(owner) FROM Owner owner WHERE owner.lastName LIKE concat(:lastName, '%')")
  Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

  /**
   * Retrieves an owner by their ID, fetching their pets in the same query.
   *
   * @param id The ID of the owner to retrieve.
   * @return An optional containing the owner if found, or empty otherwise.
   */
  @Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
  @NonNull
  Optional<Owner> findById(@Param("id") @NotNull Long id);
}
