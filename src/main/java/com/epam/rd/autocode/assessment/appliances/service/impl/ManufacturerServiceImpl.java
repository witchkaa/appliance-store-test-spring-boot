package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerDeleteException;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final MessageSource messageSource;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public List<Manufacturer> getAll() {
        log.info("Fetching all manufacturers");
        List<Manufacturer> manufacturers = manufacturerRepository.findAll();
        log.debug("Found {} manufacturers", manufacturers.size());
        return manufacturers;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public Manufacturer save(Manufacturer manufacturer) {
        log.info("Saving manufacturer: {}", manufacturer);
        Manufacturer saved = manufacturerRepository.save(manufacturer);
        log.info("Manufacturer saved with id: {}", saved.getId());
        return saved;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public void delete(Long id) {
        log.info("Deleting manufacturer with id: {}", id);
        if (!manufacturerRepository.existsById(id)) {
            log.warn("Manufacturer with id {} not found for deletion", id);
            throw new ManufacturerNotFoundException(id);
        }

        try {
            manufacturerRepository.deleteById(id);
            log.info("Manufacturer with id {} deleted", id);
        } catch (DataIntegrityViolationException e) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage("error.manufacturer.delete", null, locale);
            log.error("Error deleting manufacturer with id {}: {}", id, message, e);
            throw new ManufacturerDeleteException(message);
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public Optional<Manufacturer> findById(Long id) {
        log.info("Finding manufacturer by id: {}", id);
        Optional<Manufacturer> manufacturer = manufacturerRepository.findById(id);
        if (manufacturer.isPresent()) {
            log.debug("Manufacturer found: {}", manufacturer.get());
        } else {
            log.warn("Manufacturer with id {} not found", id);
        }
        return manufacturer;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public List<Manufacturer> search(Long id, String name) {
        log.info("Searching manufacturers with id: {}, name: {}", id, name);

        if (id != null) {
            List<Manufacturer> list = manufacturerRepository.findById(id).map(List::of).orElse(List.of());
            log.debug("Search by id result size: {}", list.size());
            return list;
        }

        if (name != null && !name.isBlank()) {
            List<Manufacturer> list = manufacturerRepository.findByNameContainingIgnoreCase(name);
            log.debug("Search by name result size: {}", list.size());
            return list;
        }

        List<Manufacturer> list = manufacturerRepository.findAll();
        log.debug("Search with no filters result size: {}", list.size());
        return list;
    }
}