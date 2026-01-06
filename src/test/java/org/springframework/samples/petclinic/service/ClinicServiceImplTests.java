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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

/** Test class for {@link ClinicServiceImpl} */
@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTests {

  @Mock private PetRepository petRepository;
  @Mock private VetRepository vetRepository;
  @Mock private OwnerRepository ownerRepository;
  @Mock private VisitRepository visitRepository;

  @InjectMocks private ClinicServiceImpl clinicService;

  @Test
  void shouldFindOwnerById() {
    Owner owner = new Owner();
    owner.setId(1L);
    when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

    Owner foundOwner = clinicService.findOwnerById(1L);
    assertEquals(1L, foundOwner.getId());
    verify(ownerRepository).findById(1L);
  }

  @Test
  void shouldFindVets() {
    clinicService.findVets();
    verify(vetRepository).findAll();
  }
}


