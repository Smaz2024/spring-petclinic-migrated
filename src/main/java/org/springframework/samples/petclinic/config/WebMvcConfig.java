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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.samples.petclinic.web.SecurityHeadersInterceptor;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Spring MVC Configuration.
 *
 * <p>
 * This class replaces legacy XML configurations (mvc-view-config.xml and
 * mvc-core-config.xml). it configures:
 * <ul>
 * <li>Static resource handling (CSS, JS, WebJars).</li>
 * <li>View resolution (JSP based).</li>
 * <li>Formatters and Interceptors.</li>
 * <li>Default view controllers.</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
// Enables Spring MVC.
@EnableWebMvc
// Scans for web components.
@ComponentScan(basePackages = "org.springframework.samples.petclinic.web")
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${view.prefix:/WEB-INF/jsp/}")
  private String viewPrefix;

  @Value("${view.suffix:.jsp}")
  private String viewSuffix;

  @Value("${static.cache-period-seconds:31556926}")
  private int staticCachePeriod;

  // Enables the default servlet handler.
  @Override
  public void configureDefaultServletHandling(@NonNull DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  // Configures resource handlers for static resources.
  @Override
  public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/resources/**")
        .addResourceLocations("/resources/")
        .setCachePeriod(staticCachePeriod);

    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  // Configures a view controller for the root URL.
  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("welcome");
  }

  // Configures the view resolver for JSP views.
  @Override
  public void configureViewResolvers(@NonNull ViewResolverRegistry registry) {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix(viewPrefix);
    resolver.setSuffix(viewSuffix);
    registry.viewResolver(resolver);
  }

  // Injects the PetTypeFormatter.
  @Autowired
  private org.springframework.samples.petclinic.web.formatters.PetTypeFormatter petTypeFormatter;

  // Adds the PetTypeFormatter to the formatter registry.
  @Override
  public void addFormatters(@NonNull org.springframework.format.FormatterRegistry registry) {
    if (petTypeFormatter != null) {
      registry.addFormatter(petTypeFormatter);
    }
  }

  // Adds the SecurityHeadersInterceptor to the interceptor registry.
  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(new SecurityHeadersInterceptor());
  }
}
