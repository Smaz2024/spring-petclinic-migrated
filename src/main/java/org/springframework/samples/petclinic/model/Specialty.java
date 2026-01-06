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

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.logging.Logger;

/**
 * Models a {@link Vet Vet's} specialization (e.g. radiology, dentistry,
 * surgery).
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Entity
@Table(name = "specialties")
@XmlRootElement
public class Specialty extends NamedEntity {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(Specialty.class.getName());

    @Override
    public String getName() {
        logger.info("Getting specialty name: " + super.getName());
        return super.getName();
    }

    @Override
    public void setName(String name) {
        logger.info("Setting specialty name to: " + name);
        super.setName(name);
    }
}
