package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;

import java.util.List;
import java.util.Optional;

public interface ManufacturerService {
    List<Manufacturer> getAll();
    Manufacturer save(Manufacturer manufacturer);
    void delete(Long id);
    Optional<Manufacturer> findById(Long id);
    List<Manufacturer> search(Long id, String name);
}
