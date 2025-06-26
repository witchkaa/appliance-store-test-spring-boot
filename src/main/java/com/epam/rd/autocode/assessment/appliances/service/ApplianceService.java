package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ApplianceService {
    Page<Appliance> getAll(Pageable pageable);
    Appliance save(Appliance appliance);
    void delete(Long id);
    Optional<Appliance> findById(Long id);
    Page<Appliance> getByType(ProductType type, Pageable pageable);
}
