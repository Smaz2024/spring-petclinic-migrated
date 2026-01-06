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
package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Test class for {@link VetController} */
@ExtendWith(MockitoExtension.class)
class VetControllerTests {

  @Mock
  private ClinicService clinicService;

  @InjectMocks
  private VetController vetController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    Vet vet = new Vet();
    vet.setFirstName("James");
    vet.setLastName("Carter");
    vet.setId(1L);
    Specialty radiology = new Specialty();
    radiology.setId(1L);
    radiology.setName("radiology");
    vet.addSpecialty(radiology);
    Collection<Vet> vets = new ArrayList<>();
    vets.add(vet);

    given(this.clinicService.findVets()).willReturn(vets);

    mockMvc = MockMvcBuilders.standaloneSetup(vetController).build();
  }

  @Test
  void testShowVetListHtml() throws Exception {
    mockMvc
        .perform(get("/vets.html"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("vets"))
        .andExpect(view().name("vets/vetList"));
  }
}


