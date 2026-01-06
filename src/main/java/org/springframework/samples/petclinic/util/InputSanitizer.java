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

import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Input sanitization utility for security.
 *
 * <p>
 * Provides methods to escape HTML entities to prevent XSS attacks and
 * perform basic detection for common SQL injection patterns.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
public class InputSanitizer {

  // A pattern to detect common SQL injection keywords and characters.
  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(['\";]+|(--)+)");

  // Private constructor to prevent instantiation of this utility class.
  private InputSanitizer() {
    // utility class
  }

  /**
   * Sanitizes a string by escaping HTML and checking for potential SQL injection.
   *
   * @param input The string to sanitize.
   * @return The sanitized string.
   * @throws IllegalArgumentException if potential SQL injection is detected.
   */
  @NonNull
  public static String sanitize(String input) {
    // Return an empty string if the input is null or empty.
    if (!StringUtils.hasText(input)) {
      return "";
    }
    // Escape HTML characters to prevent Cross-Site Scripting (XSS) attacks.
    String safe = HtmlUtils.htmlEscape(input);

    // Check for patterns that might indicate a SQL injection attempt.
    if (SQL_INJECTION_PATTERN.matcher(safe).find()) {
      throw new IllegalArgumentException("Potential SQL injection detected in input");
    }

    // Trim whitespace from the sanitized string.
    String result = safe.trim();
    return result;
  }
}
