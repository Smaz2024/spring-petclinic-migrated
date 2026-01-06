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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.util.PetclinicConstants;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.util.InputSanitizer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for Owner-related actions.
 *
 * <p>
 * Handles creating, updating, and finding owners. Includes input
 * sanitization using {@link InputSanitizer} for all user-provided strings
 * to prevent XSS.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Controller
public class OwnerController {

  private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = PetclinicConstants.OWNER_CREATE_OR_UPDATE_FORM;
  private final ClinicService clinicService;

  /**
   * Constructor to inject the ClinicService.
   *
   * @param clinicService The clinic service.
   */
  @Autowired
  public OwnerController(ClinicService clinicService) {
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
   * Initializes the owner creation form.
   *
   * @param model The model.
   * @return The view for the owner creation form.
   */
  @GetMapping("/owners/new")
  public String initCreationForm(Map<String, Object> model) {
    Owner owner = new Owner();
    model.put("owner", owner);
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }

  /**
   * Processes the owner creation form.
   *
   * @param owner  The owner to create.
   * @param result The binding result.
   * @return The view to redirect to after creation.
   */
  @PostMapping("/owners/new")
  public String processCreationForm(@Valid Owner owner, BindingResult result) {
    if (result.hasErrors()) {
      return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    } else {
      // Sanitize input before saving.
      owner.setFirstName(InputSanitizer.sanitize(owner.getFirstName()));
      owner.setLastName(InputSanitizer.sanitize(owner.getLastName()));
      owner.setAddress(InputSanitizer.sanitize(owner.getAddress()));
      owner.setCity(InputSanitizer.sanitize(owner.getCity()));

      this.clinicService.saveOwner(owner);
      return "redirect:/owners/" + owner.getId();
    }
  }

  /**
   * Initializes the owner find form.
   *
   * @param model The model.
   * @return The view for the owner find form.
   */
  @GetMapping("/owners/find")
  public String initFindForm(Map<String, Object> model) {
    model.put("owner", new Owner());
    return "owners/findOwners";
  }

  /**
   * Processes the owner find form.
   *
   * @param page   The page number for pagination.
   * @param owner  The owner to find.
   * @param result The binding result.
   * @param model  The model.
   * @return The view to display the find results.
   */
  @GetMapping("/owners")
  public String processFindForm(
      @RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result, Model model) {
    // allow parameterless GET request for /owners to return all records
    if (owner.getLastName() == null) {
      owner.setLastName(""); // empty string signifies broadest possible search
    }

    // find owners by last name
    Pageable pageable = PageRequest.of(page - 1, 5);
    Page<Owner> owners = clinicService.findOwnerByLastName(owner.getLastName(), pageable);

    if (owners.getTotalElements() == 0) {
      // no owners found
      result.rejectValue("lastName", "notFound", "not found");
      return "owners/findOwners";
    } else if (owners.getTotalElements() == 1) {
      // 1 owner found
      owner = owners.iterator().next();
      return "redirect:/owners/" + owner.getId();
    } else {
      // multiple owners found
      model.addAttribute("listOwners", owners.getContent());
      model.addAttribute("currentPage", page);
      model.addAttribute("totalPages", owners.getTotalPages());
      model.addAttribute("totalItems", owners.getTotalElements());
      return "owners/ownersList";
    }
  }

  /**
   * Initializes the owner update form.
   *
   * @param ownerId The ID of the owner to update.
   * @param model   The model.
   * @return The view for the owner update form.
   */
  @GetMapping("/owners/{ownerId}/edit")
  public String initUpdateOwnerForm(@PathVariable("ownerId") Long ownerId, Model model) {
    Owner owner = this.clinicService.findOwnerById(ownerId);
    if (owner != null) {
      model.addAttribute(owner);
    }
    return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
  }

  /**
   * Processes the owner update form.
   *
   * @param owner   The owner to update.
   * @param result  The binding result.
   * @param ownerId The ID of the owner to update.
   * @return The view to redirect to after updating.
   */
  @PostMapping("/owners/{ownerId}/edit")
  public String processUpdateOwnerForm(
      @Valid Owner owner, BindingResult result, @PathVariable("ownerId") Long ownerId) {
    if (result.hasErrors()) {
      return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    } else {
      if (ownerId != null) {
        owner.setId(ownerId);
      }
      // Sanitize input before saving.
      owner.setFirstName(InputSanitizer.sanitize(owner.getFirstName()));
      owner.setLastName(InputSanitizer.sanitize(owner.getLastName()));
      owner.setAddress(InputSanitizer.sanitize(owner.getAddress()));
      owner.setCity(InputSanitizer.sanitize(owner.getCity()));

      this.clinicService.saveOwner(owner);
      return "redirect:/owners/{ownerId}";
    }
  }

  /**
   * Shows the details of an owner.
   *
   * @param ownerId The ID of the owner to show.
   * @return The view for the owner details.
   */
  @GetMapping("/owners/{ownerId}")
  public ModelAndView showOwner(@PathVariable("ownerId") Long ownerId) {
    ModelAndView mav = new ModelAndView("owners/ownerDetails");
    Owner owner = this.clinicService.findOwnerById(ownerId);
    if (owner != null) {
      mav.addObject(owner);
    }
    return mav;
  }
}
