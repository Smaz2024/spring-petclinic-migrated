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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Simple domain object representing a list of veterinarians. Mostly here to be used for the 'vets'
 * {@link org.springframework.web.servlet.view.xml.MarshallingView}.
 */
@XmlRootElement
public class Vets {

  private List<Vet> vets;
  private static final Logger logger = Logger.getLogger(Vets.class.getName());

  @XmlElement(name = "vetList")
  public List<Vet> getVetList() {
    logger.info("Getting vet list");
    if (vets == null) {
      vets = new ArrayList<>();
    }
    return vets;
  }
  
  public void setVetList(List<Vet> vets) {
      logger.info("Setting vet list");
      this.vets = vets;
  }
}
