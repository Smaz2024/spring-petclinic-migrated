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
package org.springframework.samples.petclinic.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller used to showcase what happens when an exception is thrown.
 *
 * <p>
 * Triggered by navigating to the "/oups" endpoint. This demonstrates the
 * exception handling configuration and how error pages are rendered.
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Controller
@RequestMapping("/oups")
public class CrashController {

  /**
   * Triggers a runtime exception to showcase error page handling.
   *
   * @throws RuntimeException always, to demonstrate error handling
   */
  @GetMapping
  public void triggerException() {
    throw new RuntimeException(
        "Expected: controller used to showcase what happens when an exception is thrown");
  }

}
