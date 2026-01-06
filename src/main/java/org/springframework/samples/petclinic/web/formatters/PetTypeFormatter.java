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
package org.springframework.samples.petclinic.web.formatters;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.lang.NonNull;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Component;

/**
 * {@link Formatter} implementation for {@link PetType}.
 *
 * <p>
 * Used by Spring MVC to convert between PetType objects and their
 * string representation (the name) in forms and views.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Component
public class PetTypeFormatter implements Formatter<PetType> {

  private final ClinicService clinicService;

  /**
   * Constructor to inject the ClinicService.
   *
   * @param clinicService The clinic service.
   */
  @Autowired
  public PetTypeFormatter(ClinicService clinicService) {
    this.clinicService = clinicService;
  }

  /**
   * Prints the PetType's name.
   *
   * @param petType The PetType to print.
   * @param locale  The current locale.
   * @return The name of the PetType.
   */
  @Override
  @NonNull
  public String print(@NonNull PetType petType, @NonNull Locale locale) {
    String name = petType.getName();
    return name != null ? name : "";
  }

  /**
   * Parses a string to find a matching PetType.
   *
   * @param text   The string to parse.
   * @param locale The current locale.
   * @return The matching PetType.
   * @throws ParseException if no matching PetType is found.
   */
  @Override
  @NonNull
  public PetType parse(@NonNull String text, @NonNull Locale locale) throws ParseException {
    Collection<PetType> findPetTypes = this.clinicService.findPetTypes();
    for (PetType type : findPetTypes) {
      if (type.getName().equals(text)) {
        return type;
      }
    }
    throw new ParseException("type not found: " + text, 0);
  }
}
