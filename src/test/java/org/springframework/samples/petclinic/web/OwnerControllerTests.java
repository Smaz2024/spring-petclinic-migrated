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
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/** Test class for {@link OwnerController} */
@ExtendWith(MockitoExtension.class)
class OwnerControllerTests {

  private static final Long TEST_OWNER_ID = 1L;

  @Mock private ClinicService clinicService;

  @InjectMocks private OwnerController ownerController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc = MockMvcBuilders.standaloneSetup(ownerController).setValidator(validator).build();
  }

  @Test
  void testInitCreationForm() throws Exception {
    mockMvc
        .perform(get("/owners/new"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("owner"))
        .andExpect(view().name("owners/createOrUpdateOwnerForm"));
  }

  // @Test
  // void testProcessCreationFormSuccess() throws Exception {
  //   // Mock saveOwner to set an ID on the owner
  //   org.mockito.BDDMockito.willAnswer(invocation -> {
  //     Owner owner = invocation.getArgument(0);
  //     owner.setId(TEST_OWNER_ID);
  //     return null;
  //   }).given(clinicService).saveOwner(org.mockito.ArgumentMatchers.any(Owner.class));
  //
  //   mockMvc
  //       .perform(
  //           post("/owners/new")
  //               .param("firstName", "Joe")
  //               .param("lastName", "Bloggs")
  //               .param("address", "123 Sesame Street")
  //               .param("city", "London")
  //               .param("telephone", "01316761638"))
  //       .andExpect(status().is3xxRedirection())
  //       .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
  // }

  @Test
  void testProcessCreationFormHasErrors() throws Exception {
    mockMvc
        .perform(
            post("/owners/new")
                .param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("city", "London"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("owner"))
        .andExpect(view().name("owners/createOrUpdateOwnerForm"));
  }

  @Test
  void testShowOwner() throws Exception {
    Owner owner = new Owner();
    owner.setId(TEST_OWNER_ID);
    given(this.clinicService.findOwnerById(TEST_OWNER_ID)).willReturn(owner);

    mockMvc
        .perform(get("/owners/{ownerId}", TEST_OWNER_ID))
        .andExpect(status().isOk())
        .andExpect(model().attribute("owner", owner))
        .andExpect(view().name("owners/ownerDetails"));
  }
}


