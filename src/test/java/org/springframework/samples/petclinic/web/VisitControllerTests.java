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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Test class for {@link VisitController} */
@ExtendWith(MockitoExtension.class)
class VisitControllerTests {

  private static final Long TEST_OWNER_ID = 1L;
  private static final Long TEST_PET_ID = 1L;

  @Mock private ClinicService clinicService;

  @InjectMocks private VisitController visitController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    Pet pet = new Pet();
    pet.setId(TEST_PET_ID);
    given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(pet);

    mockMvc = MockMvcBuilders.standaloneSetup(visitController).build();
  }

  @Test
  void testInitNewVisitForm() throws Exception {
    mockMvc
        .perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/createOrUpdateVisitForm"));
  }

  @Test
  void testProcessNewVisitFormSuccess() throws Exception {
    mockMvc
        .perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George")
                .param("description", "Visit Description"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/owners/{ownerId}"));
  }

  @Test
  void testProcessNewVisitFormHasErrors() throws Exception {
    mockMvc
        .perform(
            post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "George"))
        .andExpect(model().attributeHasErrors("visit"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/createOrUpdateVisitForm"));
  }
}


