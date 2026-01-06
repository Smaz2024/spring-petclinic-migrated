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

import java.util.Collection;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.util.InputSanitizer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Pet-related actions.
 *
 * <p>
 * Handles creating and updating pets for a specific owner. Ensures
 * that input is sanitized and duplicate pet names are rejected.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Controller
@RequestMapping("/owners/{ownerId}")
public class PetController {

  private static final String VIEWS_PET_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
  private final ClinicService clinicService;

  /**
   * Constructor to inject the ClinicService.
   *
   * @param clinicService The clinic service.
   */
  @Autowired
  public PetController(ClinicService clinicService) {
    this.clinicService = clinicService;
  }

  /**
   * Populates the pet types for the form.
   *
   * @return A collection of pet types.
   */
  @ModelAttribute("types")
  public Collection<PetType> populatePetTypes() {
    return this.clinicService.findPetTypes();
  }

  /**
   * Finds the owner for the given ID.
   *
   * @param ownerId The ID of the owner.
   * @return The owner.
   * @throws IllegalArgumentException if the owner is not found.
   */
  @ModelAttribute("owner")
  public Owner findOwner(@PathVariable("ownerId") Long ownerId) {
    Owner owner = this.clinicService.findOwnerById(ownerId);
    if (owner == null) {
      throw new IllegalArgumentException("Owner not found with id: " + ownerId);
    }
    return owner;
  }

  /**
   * Initializes the binder for the owner.
   *
   * @param dataBinder The data binder.
   */
  @InitBinder("owner")
  public void initOwnerBinder(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  /**
   * Initializes the binder for the pet.
   *
   * @param dataBinder The data binder.
   */
  @InitBinder("pet")
  public void initPetBinder(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields("id");
  }

  /**
   * Initializes the pet creation form.
   *
   * @param owner The owner of the pet.
   * @param model The model.
   * @return The view for the pet creation form.
   */
  @GetMapping("/pets/new")
  public String initCreationForm(Owner owner, ModelMap model) {
    Pet pet = new Pet();
    owner.addPet(pet);
    model.put("pet", pet);
    return VIEWS_PET_CREATE_OR_UPDATE_FORM;
  }

  /**
   * Processes the pet creation form.
   *
   * @param owner  The owner of the pet.
   * @param pet    The pet to create.
   * @param result The binding result.
   * @param model  The model.
   * @return The view to redirect to after creation.
   */
  @PostMapping("/pets/new")
  public String processCreationForm(
      Owner owner, @Valid Pet pet, BindingResult result, ModelMap model) {
    // Check for duplicate pet names.
    if (StringUtils.hasLength(pet.getName())
        && pet.isNew()
        && owner.getPet(pet.getName(), true) != null) {
      result.rejectValue("name", "duplicate", "already exists");
    }
    owner.addPet(pet);
    if (result.hasErrors()) {
      model.put("pet", pet);
      return VIEWS_PET_CREATE_OR_UPDATE_FORM;
    } else {
      // Sanitize input before saving.
      pet.setName(InputSanitizer.sanitize(pet.getName()));
      this.clinicService.savePet(pet);
      return "redirect:/owners/{ownerId}";
    }
  }

  /**
   * Initializes the pet update form.
   *
   * @param petId The ID of the pet to update.
   * @param model The model.
   * @return The view for the pet update form.
   * @throws IllegalArgumentException if the pet is not found.
   */
  @GetMapping("/pets/{petId}/edit")
  public String initUpdateForm(@PathVariable("petId") Long petId, ModelMap model) {
    Pet pet = this.clinicService.findPetById(petId);
    if (pet == null) {
      throw new IllegalArgumentException("Pet not found with id: " + petId);
    }
    model.put("pet", pet);
    return VIEWS_PET_CREATE_OR_UPDATE_FORM;
  }

  /**
   * Processes the pet update form.
   *
   * @param pet    The pet to update.
   * @param result The binding result.
   * @param owner  The owner of the pet.
   * @param model  The model.
   * @return The view to redirect to after updating.
   */
  @PostMapping("/pets/{petId}/edit")
  public String processUpdateForm(
      @Valid Pet pet, BindingResult result, Owner owner, ModelMap model) {
    if (result.hasErrors()) {
      pet.setOwner(owner);
      model.put("pet", pet);
      return VIEWS_PET_CREATE_OR_UPDATE_FORM;
    } else {
      owner.addPet(pet);
      // Sanitize input before saving.
      pet.setName(InputSanitizer.sanitize(pet.getName()));
      this.clinicService.savePet(pet);
      return "redirect:/owners/{ownerId}";
    }
  }
}
