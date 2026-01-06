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
package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application configuration class.
 *
 * <p>
 * This class replaces the legacy XML configurations (business-config.xml, etc.)
 * with a Java-based configuration approach. It serves as the bridge for all
 * specialized configurations including data source, JPA, caching, security,
 * and observability.
 *
 * <p>
 * Optimizations for WildFly:
 * <ul>
 * <li>Explicit component scanning to reduce startup scanning overhead.</li>
 * <li>AspectJ auto-proxying for AOP-driven features like auditing and
 * logging.</li>
 * <li>Centralized import of modular configuration classes.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
// Specifies the base packages to scan for Spring components.
@ComponentScan(basePackages = {
    "org.springframework.samples.petclinic.config",
    "org.springframework.samples.petclinic.web",
    "org.springframework.samples.petclinic.service",
    "org.springframework.samples.petclinic.repository",
    "org.springframework.samples.petclinic.aspect"
})
// Enables support for AspectJ-based aspects.
@EnableAspectJAutoProxy
// Enables Spring's annotation-driven transaction management.
@EnableTransactionManagement
// Specifies the location of the application properties file.
@PropertySource("classpath:application.properties")
// Imports other configuration classes to create a modular configuration.
@Import({
    DataSourceConfig.class,
    JpaConfig.class,
    CacheConfig.class,
    ObservabilityConfig.class,
    ResilienceConfig.class
})
public class AppConfig {
}
