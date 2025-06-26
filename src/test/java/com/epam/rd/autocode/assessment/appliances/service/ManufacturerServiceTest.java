package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.ManufacturerServiceImpl;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @InjectMocks
    private ManufacturerServiceImpl manufacturerService;

    @Test
    void getAll_shouldReturnListOfManufacturers() {
        List<Manufacturer> manufacturers = List.of(new Manufacturer(), new Manufacturer());
        Mockito.when(manufacturerRepository.findAll()).thenReturn(manufacturers);

        List<Manufacturer> result = manufacturerService.getAll();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(manufacturerRepository).findAll();
    }

    @Test
    void save_shouldSaveManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        Mockito.when(manufacturerRepository.save(manufacturer)).thenReturn(manufacturer);

        Manufacturer result = manufacturerService.save(manufacturer);

        Assertions.assertEquals(manufacturer, result);
        Mockito.verify(manufacturerRepository).save(manufacturer);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long id = 1L;

        manufacturerService.delete(id);

        Mockito.verify(manufacturerRepository).deleteById(id);
    }

    @Test
    void delete_whenDataIntegrityViolationException_shouldThrowIllegalStateException() {
        Long id = 1L;
        Mockito.doThrow(DataIntegrityViolationException.class).when(manufacturerRepository).deleteById(id);

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            manufacturerService.delete(id);
        });

        Assertions.assertEquals("Cannot delete manufacturer with existing appliances.", exception.getMessage());
    }

    @Test
    void findById_shouldReturnManufacturer() {
        Long id = 1L;
        Manufacturer manufacturer = new Manufacturer();
        Mockito.when(manufacturerRepository.findById(id)).thenReturn(Optional.of(manufacturer));

        Optional<Manufacturer> result = manufacturerService.findById(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(manufacturer, result.get());
        Mockito.verify(manufacturerRepository).findById(id);
    }
}