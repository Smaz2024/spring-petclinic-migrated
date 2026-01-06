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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Vet} domain objects.
 *
 * <p>
 * This interface provides CRUD (Create, Read, Update, Delete) operations for {@link Vet}
 * entities, leveraging Spring Data JPA's {@link JpaRepository}. The default {@code findAll}
 * method is sufficient for loading all veterinarians.
 * </p>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Long> {

  // Default findAll is sufficient to load vets
}
