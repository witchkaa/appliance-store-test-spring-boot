package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.exception.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.ApplianceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplianceServiceTest {

    @Mock
    private ApplianceRepository applianceRepository;

    @InjectMocks
    private ApplianceServiceImpl applianceService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final ProductType type = ProductType.TV;

    @Test
    void getAll_ShouldReturnPageFromRepository() {
        Page<Appliance> expected = new PageImpl<>(List.of(new Appliance()));
        when(applianceRepository.findAll(pageable)).thenReturn(expected);

        Page<Appliance> actual = applianceService.getAll(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void save_ShouldDelegateToRepository() {
        Appliance appliance = new Appliance();
        when(applianceRepository.save(appliance)).thenReturn(appliance);

        Appliance result = applianceService.save(appliance);

        assertEquals(appliance, result);
    }

    @Test
    void delete_WhenExists_ShouldCallDelete() {
        Long id = 1L;
        when(applianceRepository.existsById(id)).thenReturn(true);

        applianceService.delete(id);

        verify(applianceRepository).deleteById(id);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        Long id = 1L;
        when(applianceRepository.existsById(id)).thenReturn(false);

        assertThrows(ApplianceNotFoundException.class, () -> applianceService.delete(id));
    }

    @Test
    void findById_ShouldReturnOptionalFromRepository() {
        Long id = 1L;
        Appliance appliance = new Appliance();
        when(applianceRepository.findById(id)).thenReturn(Optional.of(appliance));

        Optional<Appliance> result = applianceService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(appliance, result.get());
    }

    @Test
    void getByType_ShouldCallRepository() {
        Page<Appliance> page = new PageImpl<>(List.of(new Appliance()));
        when(applianceRepository.findByType(type, pageable)).thenReturn(page);

        Page<Appliance> result = applianceService.getByType(type, pageable);

        assertEquals(page, result);
    }

    @Test
    void searchAppliances_ByIdFound_ShouldReturnPageWithOneElement() {
        Long id = 1L;
        Appliance appliance = new Appliance();
        when(applianceRepository.findById(id)).thenReturn(Optional.of(appliance));

        Page<Appliance> result = applianceService.searchAppliances(id, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(appliance, result.getContent().get(0));
    }

    @Test
    void searchAppliances_ByIdNotFound_ShouldReturnEmptyPage() {
        Long id = 1L;
        when(applianceRepository.findById(id)).thenReturn(Optional.empty());

        Page<Appliance> result = applianceService.searchAppliances(id, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }


    @Test
    void searchAppliances_WithTypeNameManufacturer_ShouldCallCorrectRepoMethod() {
        String name = "name";
        String manufacturer = "manufacturer";

        Page<Appliance> page = new PageImpl<>(List.of(new Appliance()));
        when(applianceRepository.findByTypeAndNameContainingIgnoreCaseAndManufacturer_NameContainingIgnoreCase(
                eq(type), eq(name), eq(manufacturer), eq(pageable))).thenReturn(page);

        Page<Appliance> result = applianceService.searchAppliances(type, name, manufacturer, pageable);

        assertEquals(page, result);
    }

    @Test
    void searchAppliances_WithTypeAndName_ShouldCallCorrectRepoMethod() {
        String name = "name";

        Page<Appliance> page = new PageImpl<>(List.of(new Appliance()));
        when(applianceRepository.findByTypeAndNameContainingIgnoreCase(type, name, pageable)).thenReturn(page);

        Page<Appliance> result = applianceService.searchAppliances(type, name, null, pageable);

        assertEquals(page, result);
    }

    @Test
    void searchAppliances_WithOnlyType_ShouldCallFindByType() {
        Page<Appliance> page = new PageImpl<>(List.of(new Appliance()));
        when(applianceRepository.findByType(type, pageable)).thenReturn(page);

        Page<Appliance> result = applianceService.searchAppliances(type, null, null, pageable);

        assertEquals(page, result);
    }
}