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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

class WebMvcConfigTests {

  @Test
  void shouldConfigureDefaultServletHandling() {
    WebMvcConfig config = new WebMvcConfig();
    DefaultServletHandlerConfigurer configurer = mock(DefaultServletHandlerConfigurer.class);
    config.configureDefaultServletHandling(configurer);
    verify(configurer).enable();
  }

  @Test
  void shouldAddResourceHandlers() {
    WebMvcConfig config = new WebMvcConfig();
    ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
    ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

    when(registry.addResourceHandler(anyString())).thenReturn(registration);
    when(registration.addResourceLocations(any(String[].class))).thenReturn(registration);
    when(registration.addResourceLocations(anyString())).thenReturn(registration);

    config.addResourceHandlers(registry);

    verify(registry).addResourceHandler("/resources/**");
    verify(registration).addResourceLocations("/resources/");
    verify(registry).addResourceHandler("/webjars/**");
  }

  @Test
  void shouldAddViewControllers() {
    WebMvcConfig config = new WebMvcConfig();
    ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
    ViewControllerRegistration registration = mock(ViewControllerRegistration.class);

    when(registry.addViewController("/")).thenReturn(registration);

    config.addViewControllers(registry);

    verify(registry).addViewController("/");
    verify(registration).setViewName("welcome");
  }

  @Test
  void shouldConfigureViewResolvers() {
    WebMvcConfig config = new WebMvcConfig();
    ViewResolverRegistry registry = mock(ViewResolverRegistry.class);

    config.configureViewResolvers(registry);

    verify(registry).viewResolver(any(InternalResourceViewResolver.class));
  }
}


