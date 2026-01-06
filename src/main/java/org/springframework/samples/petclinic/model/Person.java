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
package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.logging.Logger;

/**
 * Simple JavaBean domain object representing a person.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@MappedSuperclass
public class Person extends BaseEntity {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(Person.class.getName());

  @Column(name = "first_name")
  @NotEmpty
  private String firstName;

  @Column(name = "last_name")
  @NotEmpty
  private String lastName;

  @XmlElement
  public String getFirstName() {
    logger.info("Getting first name: " + firstName);
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    logger.info("Setting first name to: " + firstName);
    this.firstName = firstName;
  }

  @XmlElement
  public String getLastName() {
    logger.info("Getting last name: " + lastName);
    return this.lastName;
  }

  public void setLastName(String lastName) {
    logger.info("Setting last name to: " + lastName);
    this.lastName = lastName;
  }
}
