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

import javax.sql.DataSource;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Data Source Configuration supporting both JNDI (WildFly) and HikariCP.
 *
 * <p>
 * This configuration allows for:
 * 1. JNDI lookup (java:jboss/datasources/PetclinicDS) for container-managed
 * pooling.
 * 2. Performance-tuned HikariCP fallback for local/standalone environments.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
public class DataSourceConfig {

  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

  // Injects the Environment to access application properties.
  @Autowired
  private Environment env;

  /**
   * Configures the main data source for the application.
   * 
   * <p>
   * Priorities:
   * 1. JNDI name from 'jdbc.jndi-name' property.
   * 2. Manual HikariCP configuration using 'jdbc.*' properties.
   *
   * @return the configured data source
   */
  @Bean
  // Specifies that this bean is only active for the "postgres" profile.
  @Profile("postgres") 
  public DataSource dataSource() {
    // 1. Try JNDI lookup first (Recommended for WildFly)
    String jndiName = env.getProperty("jdbc.jndi-name");
    if (jndiName != null && !jndiName.isEmpty()) {
      try {
        logger.info("Initializing DataSource via JNDI: {}", jndiName);
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource(jndiName);
      } catch (Exception e) {
        logger.warn("JNDI lookup failed for {}, falling back to HikariCP", jndiName);
      }
    }

    // 2. Fallback to HikariCP (Standalone / Local / Non-tinkered WildFly)
    logger.info("Initializing HikariCP DataSource");
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(env.getRequiredProperty("jdbc.driverClassName"));
    config.setJdbcUrl(env.getRequiredProperty("jdbc.url"));
    config.setUsername(env.getRequiredProperty("jdbc.username"));
    config.setPassword(env.getRequiredProperty("jdbc.password"));

    // Pool Sizing & Timeouts
    config.setMaximumPoolSize(Integer.parseInt(env.getProperty("hikari.maximum-pool-size", "20")));
    config.setMinimumIdle(Integer.parseInt(env.getProperty("hikari.minimum-idle", "5")));
    config.setIdleTimeout(Long.parseLong(env.getProperty("hikari.idle-timeout", "600000")));
    config.setConnectionTimeout(Long.parseLong(env.getProperty("hikari.connection-timeout", "30000")));
    config.setMaxLifetime(Long.parseLong(env.getProperty("hikari.max-lifetime", "1800000")));
    config.setPoolName("PetClinicHikariPool");

    // Performance Tuning (from WILDFLY_DEPLOYMENT.md)
    config.setAutoCommit(Boolean.parseBoolean(env.getProperty("hikari.auto-commit", "false")));
    config.addDataSourceProperty("cachePrepStmts", env.getProperty("hikari.cache-prep-stmts", "true"));
    config.addDataSourceProperty("prepStmtCacheSize", env.getProperty("hikari.prep-stmt-cache-size", "250"));
    config.addDataSourceProperty("prepStmtCacheSqlLimit", env.getProperty("hikari.prep-stmt-cache-sql-limit", "2048"));

    return new HikariDataSource(config);
  }

  /**
   * Configures the JdbcTemplate.
   *
   * @param dataSource The data source to use for the JdbcTemplate.
   * @return The configured JdbcTemplate.
   */
  @Bean
  public JdbcTemplate jdbcTemplate(@NotNull DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
