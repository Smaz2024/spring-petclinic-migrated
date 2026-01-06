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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class CacheConfigTests {

  @Test
  void cacheManagerShouldBeConfigured() throws Exception {
    CacheConfig config = new CacheConfig();
    // Manually set required fields since @Value is not processed in plain unit tests
    java.lang.reflect.Field cacheNamesField = CacheConfig.class.getDeclaredField("cacheNames");
    cacheNamesField.setAccessible(true);
    cacheNamesField.set(config, "vets,specialties,petTypes");
    java.lang.reflect.Field initialCapacityField = CacheConfig.class.getDeclaredField("initialCapacity");
    initialCapacityField.setAccessible(true);
    initialCapacityField.set(config, 100);
    java.lang.reflect.Field maximumSizeField = CacheConfig.class.getDeclaredField("maximumSize");
    maximumSizeField.setAccessible(true);
    maximumSizeField.set(config, 500);
    java.lang.reflect.Field expireAfterWriteMinutesField = CacheConfig.class.getDeclaredField("expireAfterWriteMinutes");
    expireAfterWriteMinutesField.setAccessible(true);
    expireAfterWriteMinutesField.set(config, 10);

    CacheManager cacheManager = config.cacheManager();

    assertNotNull(cacheManager);
    assertTrue(cacheManager.getCacheNames().contains("vets"));
    assertTrue(cacheManager.getCacheNames().contains("specialties"));
    assertTrue(cacheManager.getCacheNames().contains("petTypes"));
  }
}


