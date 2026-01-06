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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepoTestConfig.class})
@Transactional
class OwnerRepositoryTests {

  @Autowired private OwnerRepository ownerRepository;

  private Owner owner;

  @BeforeEach
  void setup() {
    owner = new Owner();
    owner.setFirstName("George");
    owner.setLastName("Franklin");
    owner.setAddress("110 W. Liberty St.");
    owner.setCity("Madison");
    owner.setTelephone("6085551023");
    ownerRepository.save(owner);
  }

  @Test
  void shouldFindOwnersByLastName() {
    Page<Owner> owners = ownerRepository.findByLastName("Frank", PageRequest.of(0, 5));
    assertThat(owners.getContent()).hasSize(1);
    assertThat(owners.getContent().get(0).getLastName()).isEqualTo("Franklin");
  }

  @Test
  void shouldFindOwnerById() {
    Optional<Owner> foundOwner = ownerRepository.findById(owner.getId());
    assertThat(foundOwner).isPresent();
    assertThat(foundOwner.get().getLastName()).isEqualTo("Franklin");
  }
}


