package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerDeleteException;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    public List<Manufacturer> getAll() {
        return manufacturerRepository.findAll();
    }

    @Override
    public Manufacturer save(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    @Override
    public void delete(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new ManufacturerNotFoundException(id);
        }

        try {
            manufacturerRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage("error.manufacturer.delete", null, locale);
            throw new ManufacturerDeleteException(message);
        }
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        return manufacturerRepository.findById(id);
    }

    @Override
    public List<Manufacturer> search(Long id, String name) {
        if (id != null) {
            return manufacturerRepository.findById(id).map(List::of).orElse(List.of());
        }

        if (name != null && !name.isBlank()) {
            return manufacturerRepository.findByNameContainingIgnoreCase(name);
        }

        return manufacturerRepository.findAll();
    }
}