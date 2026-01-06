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

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@ExtendWith(MockitoExtension.class)
class DataSourceConfigTests {

  @Mock
  private Environment env;

  @InjectMocks
  private DataSourceConfig dataSourceConfig;

  @Test
  void dataSourceShouldBeCreated() {
    when(env.getProperty("jdbc.jndi-name")).thenReturn(null);
    when(env.getRequiredProperty("jdbc.driverClassName")).thenReturn("org.h2.Driver");
    when(env.getRequiredProperty("jdbc.url")).thenReturn("jdbc:h2:mem:test");
    when(env.getRequiredProperty("jdbc.username")).thenReturn("sa");
    when(env.getRequiredProperty("jdbc.password")).thenReturn("");
    when(env.getProperty("hikari.maximum-pool-size", "20")).thenReturn("20");
    when(env.getProperty("hikari.minimum-idle", "5")).thenReturn("5");
    when(env.getProperty("hikari.idle-timeout", "600000")).thenReturn("600000");
    when(env.getProperty("hikari.connection-timeout", "30000")).thenReturn("30000");
    when(env.getProperty("hikari.max-lifetime", "1800000")).thenReturn("1800000");
    when(env.getProperty("hikari.auto-commit", "false")).thenReturn("false");
    when(env.getProperty("hikari.cache-prep-stmts", "true")).thenReturn("true");
    when(env.getProperty("hikari.prep-stmt-cache-size", "250")).thenReturn("250");
    when(env.getProperty("hikari.prep-stmt-cache-sql-limit", "2048")).thenReturn("2048");

    DataSource dataSource = dataSourceConfig.dataSource();
    assertNotNull(dataSource);
    if (dataSource instanceof HikariDataSource) {
      ((HikariDataSource) dataSource).close();
    }
  }

  @Test
  void jdbcTemplateShouldBeCreated() {
    DataSource dataSource = mock(DataSource.class);
    JdbcTemplate jdbcTemplate = dataSourceConfig.jdbcTemplate(dataSource);
    assertNotNull(jdbcTemplate);
  }
}
