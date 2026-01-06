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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Caching configuration using Caffeine.
 *
 * <p>
 * Caffeine is a high-performance, near-optimal caching library providing a
 * near-optimal hit rate. This configuration enables Spring's cache abstraction
 * and defines a {@link CaffeineCacheManager} for the application.
 *
 * <p>
 * Monitored caches:
 * <ul>
 * <li><b>vets</b>: Stores veterinarian data.</li>
 * <li><b>specialties</b>: Stores veterinarian specialties.</li>
 * <li><b>petTypes</b>: Stores pet types.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
// Enables Spring's caching support.
@EnableCaching
public class CacheConfig {
  @Value("${cache.names:vets,specialties,petTypes}")
  private String cacheNames;

  @Value("${cache.initial-capacity:100}")
  private int initialCapacity;

  @Value("${cache.maximum-size:500}")
  private int maximumSize;

  @Value("${cache.expire-after-write-minutes:10}")
  private int expireAfterWriteMinutes;

  @Bean
  @NonNull
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    List<String> names = Arrays.asList(cacheNames.split(","));
    cacheManager.setCacheNames(names);
    cacheManager.setCaffeine(
        java.util.Objects.requireNonNull(
            Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
                .recordStats()));
    return cacheManager;
  }
}
