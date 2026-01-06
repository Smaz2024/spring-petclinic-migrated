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
package org.springframework.samples.petclinic.service;

import java.util.Collection;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.samples.petclinic.util.PetclinicConstants;

/**
 * Primarily used as a facade for all Petclinic controllers.
 *
 * <p>
 * This implementation handles:
 * <ul>
 * <li>Transactional boundaries (mapped to {@code @Transactional}).</li>
 * <li>Caching (mapped to {@code @Cacheable}).</li>
 * <li>Fault tolerance (Resilience4J circuit breakers and rate limiters).</li>
 * </ul>
 *
 * @author Spring Petclinic Team
 * @version 2.0.0
 * @since 2.0.0
 */
@Service
public class ClinicServiceImpl implements ClinicService {

  private static final Logger logger = LoggerFactory.getLogger(ClinicServiceImpl.class);

  private final PetRepository petRepository;
  private final VetRepository vetRepository;
  private final OwnerRepository ownerRepository;
  private final VisitRepository visitRepository;

  /**
   * Constructor to inject the repositories.
   *
   * @param petRepository   The pet repository.
   * @param vetRepository   The vet repository.
   * @param ownerRepository The owner repository.
   * @param visitRepository The visit repository.
   */
  @Autowired
  public ClinicServiceImpl(
      PetRepository petRepository,
      VetRepository vetRepository,
      OwnerRepository ownerRepository,
      VisitRepository visitRepository) {
    this.petRepository = petRepository;
    this.vetRepository = vetRepository;
    this.ownerRepository = ownerRepository;
    this.visitRepository = visitRepository;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "vets")
  @CircuitBreaker(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @RateLimiter(name = PetclinicConstants.DEFAULT_RATE_LIMITER)
  @Retry(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @NonNull
  public Collection<Vet> findVets() throws DataAccessException {
    logger.debug("Fetching all vets");
    return vetRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  @CircuitBreaker(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @Retry(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @NonNull
  public Collection<PetType> findPetTypes() throws DataAccessException {
    logger.debug("Fetching all pet types");
    Collection<PetType> types = petRepository.findPetTypes();
    if (types == null) {
      return new java.util.ArrayList<>();
    }
    return types;
  }

  @Override
  @Transactional(readOnly = true)
  @CircuitBreaker(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @Retry(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  public Owner findOwnerById(@NonNull Long id) throws DataAccessException {
    logger.debug("Fetching owner by ID: {}", id);
    return ownerRepository.findById(id).orElse(null);
  }

  @Override
  @CircuitBreaker(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @Retry(name = PetclinicConstants.DEFAULT_CIRCUIT_BREAKER)
  @NonNull
  public Page<Owner> findOwnerByLastName(String lastName, @NonNull Pageable pageable)
      throws DataAccessException {
    Page<Owner> result = ownerRepository.findByLastName(lastName, pageable);
    if (result == null) {
      throw new RuntimeException("Unexpected null result from ownerRepository");
    }
    return result;
  }

  @Override
  @Transactional
  public void saveOwner(@NotNull Owner owner) throws DataAccessException {
    logger.info("Saving owner: {}", owner);
    if (owner != null) {
      ownerRepository.save(owner);
    }
  }

  @Override
  @Transactional
  public void saveVisit(@NotNull Visit visit) throws DataAccessException {
    logger.info("Saving visit: {}", visit);
    if (visit != null) {
      visitRepository.save(visit);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Pet findPetById(@NonNull Long id) throws DataAccessException {
    Pet pet = petRepository.findById(id).orElse(null);
    if (pet == null) {
      throw new IllegalArgumentException("Pet not found with id: " + id);
    }
    return pet;
  }

  @Override
  @Transactional
  public void savePet(@NotNull Pet pet) throws DataAccessException {
    petRepository.save(pet);
  }
}
