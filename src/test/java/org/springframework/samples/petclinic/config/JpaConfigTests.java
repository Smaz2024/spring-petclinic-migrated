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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class JpaConfigTests {

  @Mock
  private Environment env;

  @InjectMocks
  private JpaConfig jpaConfig;

  @Test
  void entityManagerFactoryShouldBeCreated() {
    DataSource dataSource = mock(DataSource.class);
    when(env.getRequiredProperty("jpa.database-platform"))
        .thenReturn("org.hibernate.dialect.H2Dialect");
    when(env.getProperty("jpa.show-sql", "false")).thenReturn("false");
    when(env.getProperty("jpa.format-sql", "false")).thenReturn("false");
    when(env.getProperty("jpa.hibernate.ddl-auto", "validate")).thenReturn("none");
    when(env.getProperty("hibernate.cache.jcache.provider",
        "com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider"))
        .thenReturn("com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider");
    when(env.getProperty("hibernate.cache.jcache.uri", "classpath:caffeine-jcache.properties"))
        .thenReturn("classpath:caffeine-jcache.properties");

    LocalContainerEntityManagerFactoryBean em = jpaConfig.entityManagerFactory(dataSource);
    assertNotNull(em);
  }

  @Test
  void transactionManagerShouldBeCreated() {
    EntityManagerFactory emf = mock(EntityManagerFactory.class);
    PlatformTransactionManager tm = jpaConfig.transactionManager(emf);
    assertNotNull(tm);
  }
}
