package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.service.impl.ApplianceServiceImpl;
import org.junit.jupiter.api.Test;
import com.epam.rd.autocode.assessment.appliances.repository.*;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class ApplianceServiceTest {

    @Mock
    private ApplianceRepository applianceRepository;

    @InjectMocks
    private ApplianceServiceImpl applianceService;

    @Test
    void getAll_shouldReturnPageOfAppliances() {
        Pageable pageable = PageRequest.of(0, 10);
        Appliance appliance1 = new Appliance();
        Appliance appliance2 = new Appliance();
        Page<Appliance> page = new PageImpl<>(List.of(appliance1, appliance2));

        Mockito.when(applianceRepository.findAll(pageable)).thenReturn(page);

        Page<Appliance> result = applianceService.getAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Mockito.verify(applianceRepository).findAll(pageable);
    }

    @Test
    void save_shouldSaveAndReturnAppliance() {
        Appliance appliance = new Appliance();
        appliance.setId(1L);

        Mockito.when(applianceRepository.save(appliance)).thenReturn(appliance);

        Appliance saved = applianceService.save(appliance);

        Assertions.assertEquals(appliance, saved);
        Mockito.verify(applianceRepository).save(appliance);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long id = 1L;

        applianceService.delete(id);

        Mockito.verify(applianceRepository).deleteById(id);
    }

    @Test
    void findById_shouldReturnOptionalAppliance() {
        Long id = 1L;
        Appliance appliance = new Appliance();

        Mockito.when(applianceRepository.findById(id)).thenReturn(Optional.of(appliance));

        Optional<Appliance> result = applianceService.findById(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(appliance, result.get());
        Mockito.verify(applianceRepository).findById(id);
    }
}