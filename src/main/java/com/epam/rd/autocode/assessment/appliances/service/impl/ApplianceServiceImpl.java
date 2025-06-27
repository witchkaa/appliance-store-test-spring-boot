package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;

    @Override
    public Page<Appliance> getAll(Pageable pageable) {
        log.debug("Fetching appliances page: size={}, page={}", pageable.getPageSize(), pageable.getPageNumber());
        return applianceRepository.findAll(pageable);
    }

    @Override
    public Appliance save(Appliance appliance) {
        log.info("Saving appliance: {}", appliance);
        return applianceRepository.save(appliance);
    }

    @Override
    public void delete(Long id) {
        log.warn("Deleting appliance with id: {}", id);
        applianceRepository.deleteById(id);
    }

    @Override
    public Optional<Appliance> findById(Long id) {
        log.debug("Finding appliance by id: {}", id);
        return applianceRepository.findById(id);
    }
    @Override
    public Page<Appliance> getByType(ProductType type, Pageable pageable) {
        log.debug("Fetching appliances by type {} with pageable {}", type, pageable);
        return applianceRepository.findByType(type, pageable);
    }
    @Override
    public Page<Appliance> searchAppliances(Long id, String name, String manufacturer, Pageable pageable) {
        if (id != null) {
            return applianceRepository.findById(id)
                    .map(appliance -> new PageImpl<>(List.of(appliance), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(List.of(), pageable, 0));
        }
        if ((name != null && !name.isBlank()) || (manufacturer != null && !manufacturer.isBlank())) {
            return applianceRepository.findByNameContainingIgnoreCaseAndManufacturer_NameContainingIgnoreCase(
                    name == null ? "" : name,
                    manufacturer == null ? "" : manufacturer,
                    pageable
            );
        }

        return applianceRepository.findAll(pageable);
    }
}
