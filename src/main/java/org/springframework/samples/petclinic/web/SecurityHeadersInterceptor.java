/*
 * Copyright 2002-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law of or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that adds security-related HTTP headers to every response.
 *
 * <p>
 * Headers included:
 * <ul>
 * <li>X-Frame-Options: DENY</li>
 * <li>X-XSS-Protection: 1; mode=block</li>
 * <li>X-Content-Type-Options: nosniff</li>
 * <li>Cache-Control: no-cache, no-store, max-age=0, must-revalidate</li>
 * <li>Pragma: no-cache</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
public class SecurityHeadersInterceptor implements HandlerInterceptor {

  /**
   * Adds security headers to the response before the handler is executed.
   *
   * @param request  The HTTP request.
   * @param response The HTTP response.
   * @param handler  The handler to be executed.
   * @return True to continue processing the request, false otherwise.
   */
  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
    // Prevents the page from being displayed in a frame.
    response.addHeader("X-Frame-Options", "DENY");
    // Enables the XSS filter in the browser.
    response.addHeader("X-XSS-Protection", "1; mode=block");
    // Prevents the browser from MIME-sniffing the content type.
    response.addHeader("X-Content-Type-Options", "nosniff");
    // Disables caching.
    response.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    response.addHeader("Pragma", "no-cache");
    return true;
  }
}
