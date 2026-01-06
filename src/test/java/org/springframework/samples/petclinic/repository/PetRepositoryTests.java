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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepoTestConfig.class})
@Transactional
class PetRepositoryTests {

  @Autowired private PetRepository petRepository;
  @Autowired private jakarta.persistence.EntityManager entityManager;

  @Test
  void shouldFindPetTypes() {
    PetType cat = new PetType();
    cat.setName("Cat");
    entityManager.persist(cat);

    PetType dog = new PetType();
    dog.setName("Dog");
    entityManager.persist(dog);

    entityManager.flush();

    List<PetType> petTypes = petRepository.findPetTypes();
    assertThat(petTypes).hasSize(2);
    assertThat(petTypes).extracting("name").contains("Cat", "Dog");
  }
}


