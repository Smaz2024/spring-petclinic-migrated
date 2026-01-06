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

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.util.InputSanitizer;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for Visit-related actions.
 *
 * <p>
 * Handles creating new visits for a specific pet. Ensures input
 * sanitization using {@link InputSanitizer}.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Controller
public class VisitController {

  private final ClinicService clinicService;

  /**
   * Constructor to inject the ClinicService.
   *
   * @param clinicService The clinic service.
   */
  @Autowired
  public VisitController(ClinicService clinicService) {
    this.clinicService = clinicService;
  }

  /**
   * Initializes the data binder to disallow binding of the 'id' field.
   *
   * @param dataBinder The data binder.
   */
  @InitBinder
  public void setAllowedFields(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  /**
   * Loads the pet and creates a new visit for it.
   *
   * @param petId The ID of the pet.
   * @param model The model.
   * @return The new visit.
   * @throws IllegalArgumentException if the pet is not found or the petId is null.
   */
  @ModelAttribute("visit")
  public Visit loadPetWithVisit(@PathVariable("petId") Long petId, Map<String, Object> model) {
    if (petId != null) {
      Pet pet = this.clinicService.findPetById(petId);
      if (pet == null) {
        throw new IllegalArgumentException("Pet not found with id: " + petId);
      }
      model.put("pet", pet);
      Visit visit = new Visit();
      pet.addVisit(visit);
      return visit;
    }
    throw new IllegalArgumentException("petId must not be null");
  }

  /**
   * Initializes the new visit form.
   *
   * @param petId The ID of the pet.
   * @param model The model.
   * @return The view for the new visit form.
   */
  @GetMapping("/owners/*/pets/{petId}/visits/new")
  public String initNewVisitForm(@PathVariable("petId") Long petId, Map<String, Object> model) {
    return "pets/createOrUpdateVisitForm";
  }

  /**
   * Processes the new visit form.
   *
   * @param visit  The visit to create.
   * @param result The binding result.
   * @return The view to redirect to after creation.
   */
  @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
  public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
    if (result.hasErrors()) {
      return "pets/createOrUpdateVisitForm";
    } else {
      // Sanitize input before saving.
      visit.setDescription(InputSanitizer.sanitize(visit.getDescription()));
      this.clinicService.saveVisit(visit);
      return "redirect:/owners/{ownerId}";
    }
  }
}
