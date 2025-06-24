package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;

    @Override
    public Page<Appliance> getAll(Pageable pageable) {
        return applianceRepository.findAll(pageable);
    }

    @Override
    public Appliance save(Appliance appliance) {
        return applianceRepository.save(appliance);
    }

    @Override
    public void delete(Long id) {
        applianceRepository.deleteById(id);
    }

    @Override
    public Optional<Appliance> findById(Long id) {
        return applianceRepository.findById(id);
    }
}
