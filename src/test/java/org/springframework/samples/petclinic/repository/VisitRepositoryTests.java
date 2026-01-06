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

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepoTestConfig.class})
@Transactional
class VisitRepositoryTests {

  @Autowired private VisitRepository visitRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void shouldFindByPetId() {
    PetType dog = new PetType();
    dog.setName("Dog");
    entityManager.persist(dog);

    Pet pet = new Pet();
    pet.setName("Rosy");
    pet.setBirthDate(LocalDate.now());
    pet.setType(dog);
    entityManager.persist(pet);

    Visit visit = new Visit();
    visit.setPet(pet);
    visit.setDate(LocalDate.now());
    visit.setDescription("Rabies verify");
    visitRepository.save(visit);

    List<Visit> visits = visitRepository.findByPetId(pet.getId());
    assertThat(visits).hasSize(1);
    assertThat(visits.get(0).getDescription()).isEqualTo("Rabies verify");
  }
}


