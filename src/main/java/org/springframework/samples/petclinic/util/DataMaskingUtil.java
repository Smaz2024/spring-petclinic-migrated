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
package org.springframework.samples.petclinic.util;

import org.springframework.util.StringUtils;

/**
 * Utility for masking sensitive data in logs.
 *
 * <p>
 * Provides methods to mask PII (Personally Identifiable Information)
 * like telephone numbers and physical addresses before they are logged.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
public class DataMaskingUtil {

  // Private constructor to prevent instantiation of this utility class.
  private DataMaskingUtil() {
    // utility class
  }

  /**
   * Masks a telephone number, revealing only the last four digits.
   *
   * @param telephone The telephone number to mask.
   * @return The masked telephone number.
   */
  public static String maskTelephone(String telephone) {
    // Return an empty string if the input is null or empty.
    if (!StringUtils.hasText(telephone)) {
      return "";
    }
    // If the telephone number is four digits or less, mask the entire number.
    if (telephone.length() <= 4) {
      return "****";
    }
    // Otherwise, mask all but the last four digits.
    return "XXX-XXX-" + telephone.substring(telephone.length() - 4);
  }

  /**
   * Masks a physical address completely.
   *
   * @param address The address to mask.
   * @return A masked representation of the address.
   */
  public static String maskAddress(String address) {
    // Return an empty string if the input is null or empty.
    if (!StringUtils.hasText(address)) {
      return "";
    }
    // Return a fixed-length masked string for the address.
    return "******"; // Fully mask address in logs
  }
}
