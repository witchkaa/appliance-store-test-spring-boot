package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerDeleteException;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.ManufacturerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ManufacturerServiceImpl manufacturerService;

    @Test
    void getAll_ShouldReturnList() {
        List<Manufacturer> expected = List.of(new Manufacturer(), new Manufacturer());
        when(manufacturerRepository.findAll()).thenReturn(expected);

        List<Manufacturer> result = manufacturerService.getAll();

        assertEquals(2, result.size());
        verify(manufacturerRepository).findAll();
    }

    @Test
    void save_ShouldReturnSavedManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        when(manufacturerRepository.save(manufacturer)).thenReturn(manufacturer);

        Manufacturer saved = manufacturerService.save(manufacturer);

        assertEquals(manufacturer, saved);
        verify(manufacturerRepository).save(manufacturer);
    }

    @Test
    void delete_ShouldDeleteWhenExists() {
        Long id = 1L;
        when(manufacturerRepository.existsById(id)).thenReturn(true);

        manufacturerService.delete(id);

        verify(manufacturerRepository).deleteById(id);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotExists() {
        when(manufacturerRepository.existsById(1L)).thenReturn(false);

        assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.delete(1L));
    }

    @Test
    void delete_ShouldThrowDeleteException_WhenDataIntegrityViolation() {
        Long id = 1L;
        when(manufacturerRepository.existsById(id)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(manufacturerRepository).deleteById(id);
        when(messageSource.getMessage(eq("error.manufacturer.delete"), any(), any())).thenReturn("Cannot delete");

        ManufacturerDeleteException ex = assertThrows(
                ManufacturerDeleteException.class,
                () -> manufacturerService.delete(id)
        );

        assertEquals("Cannot delete", ex.getMessage());
    }

    @Test
    void findById_ShouldReturnOptional() {
        Manufacturer manufacturer = new Manufacturer();
        when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));

        Optional<Manufacturer> result = manufacturerService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(manufacturer, result.get());
    }

    @Test
    void search_ByIdFound_ShouldReturnOne() {
        Manufacturer m = new Manufacturer();
        when(manufacturerRepository.findById(5L)).thenReturn(Optional.of(m));

        List<Manufacturer> result = manufacturerService.search(5L, null);

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
    }

    @Test
    void search_ByIdNotFound_ShouldReturnEmptyList() {
        when(manufacturerRepository.findById(5L)).thenReturn(Optional.empty());

        List<Manufacturer> result = manufacturerService.search(5L, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_ByName_ShouldReturnFilteredList() {
        List<Manufacturer> list = List.of(new Manufacturer());
        when(manufacturerRepository.findByNameContainingIgnoreCase("Samsung")).thenReturn(list);

        List<Manufacturer> result = manufacturerService.search(null, "Samsung");

        assertEquals(1, result.size());
    }

    @Test
    void search_EmptyParams_ShouldReturnAll() {
        List<Manufacturer> all = List.of(new Manufacturer());
        when(manufacturerRepository.findAll()).thenReturn(all);

        List<Manufacturer> result = manufacturerService.search(null, null);

        assertEquals(1, result.size());
    }
}