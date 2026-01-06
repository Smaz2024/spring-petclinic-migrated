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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Visit} domain objects.
 *
 * <p>
 * This interface provides CRUD (Create, Read, Update, Delete) operations for {@link Visit}
 * entities, leveraging Spring Data JPA's {@link JpaRepository}. It also includes a custom
 * query method to find visits by pet ID.
 * </p>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

  /**
   * Retrieves all visits for a given pet ID.
   *
   * @param petId The ID of the pet to retrieve visits for.
   * @return A list of visits for the given pet.
   */
  List<Visit> findByPetId(Long petId);
}
