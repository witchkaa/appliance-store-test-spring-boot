package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;

    @Override
    public Page<Appliance> getAll(Pageable pageable) {
        log.info("Fetching all appliances, page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Appliance> result = applianceRepository.findAll(pageable);
        log.debug("Fetched {} appliances", result.getNumberOfElements());
        return result;
    }

    @Override
    public Appliance save(Appliance appliance) {
        log.info("Saving appliance: {}", appliance);
        Appliance saved = applianceRepository.save(appliance);
        log.info("Appliance saved with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Deleting appliance with id: {}", id);

        if (!applianceRepository.existsById(id)) {
            log.warn("Appliance with id {} not found for deletion", id);
            throw new ApplianceNotFoundException(id);
        }

        try {
            applianceRepository.deleteById(id);
            log.info("Appliance with id {} deleted successfully", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete appliance with id {} - it's used in orders", id);
            throw new IllegalStateException("appliance.delete.error.used");
        }
    }

    @Override
    public Optional<Appliance> findById(Long id) {
        log.info("Finding appliance by id: {}", id);
        Optional<Appliance> appliance = applianceRepository.findById(id);
        if (appliance.isPresent()) {
            log.debug("Appliance found: {}", appliance.get());
        } else {
            log.warn("Appliance with id {} not found", id);
        }
        return appliance;
    }

    @Override
    public Page<Appliance> getByType(ProductType type, Pageable pageable) {
        log.info("Fetching appliances by type: {}, page number: {}, page size: {}", type, pageable.getPageNumber(), pageable.getPageSize());
        Page<Appliance> result = applianceRepository.findByType(type, pageable);
        log.debug("Fetched {} appliances of type {}", result.getNumberOfElements(), type);
        return result;
    }

    @Override
    public Page<Appliance> searchAppliances(Long id, String name, String manufacturer, Pageable pageable) {
        log.info("Searching appliances with id: {}, name: '{}', manufacturer: '{}', page: {}", id, name, manufacturer, pageable.getPageNumber());
        if (id != null) {
            Optional<Appliance> applianceOpt = applianceRepository.findById(id);
            if (applianceOpt.isPresent()) {
                log.debug("Appliance found by id: {}", id);
                return new PageImpl<>(List.of(applianceOpt.get()), pageable, 1);
            } else {
                log.warn("No appliance found by id: {}", id);
                return new PageImpl<>(List.of(), pageable, 0);
            }
        }
        if ((name != null && !name.isBlank()) || (manufacturer != null && !manufacturer.isBlank())) {
            Page<Appliance> result = applianceRepository.findByNameContainingIgnoreCaseAndManufacturer_NameContainingIgnoreCase(
                    name == null ? "" : name,
                    manufacturer == null ? "" : manufacturer,
                    pageable
            );
            log.debug("Found {} appliances by name/manufacturer", result.getNumberOfElements());
            return result;
        }
        Page<Appliance> all = applianceRepository.findAll(pageable);
        log.debug("Returning all appliances: count {}", all.getNumberOfElements());
        return all;
    }

    @Override
    public Page<Appliance> searchAppliances(ProductType type, String name, String manufacturer, Pageable pageable) {
        log.info("Searching appliances with type: {}, name: '{}', manufacturer: '{}', page: {}", type, name, manufacturer, pageable.getPageNumber());

        Page<Appliance> result;

        if (type != null && (name != null && !name.isBlank()) && (manufacturer != null && !manufacturer.isBlank())) {
            result = applianceRepository.findByTypeAndNameContainingIgnoreCaseAndManufacturer_NameContainingIgnoreCase(
                    type, name, manufacturer, pageable);
        } else if (type != null && (name != null && !name.isBlank())) {
            result = applianceRepository.findByTypeAndNameContainingIgnoreCase(type, name, pageable);
        } else if (type != null) {
            result = applianceRepository.findByType(type, pageable);
        } else if ((name != null && !name.isBlank()) || (manufacturer != null && !manufacturer.isBlank())) {
            result = applianceRepository.findByNameContainingIgnoreCaseAndManufacturer_NameContainingIgnoreCase(
                    name == null ? "" : name,
                    manufacturer == null ? "" : manufacturer,
                    pageable);
        } else {
            result = applianceRepository.findAll(pageable);
        }

        log.debug("Found {} appliances matching criteria", result.getNumberOfElements());
        return result;
    }
}