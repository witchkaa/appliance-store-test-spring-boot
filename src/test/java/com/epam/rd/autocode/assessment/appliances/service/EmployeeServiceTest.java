package com.epam.rd.autocode.assessment.appliances.service;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(repository);
    }

    @Test
    void getAll_shouldReturnListOfEmployees() {
        List<Employee> employees = List.of(new Employee(), new Employee());
        Mockito.when(repository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAll();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(repository).findAll();
    }


    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long id = 1L;
        employeeService.delete(id);
        Mockito.verify(repository).deleteById(id);
    }

    @Test
    void findById_shouldReturnEmployeeOptional() {
        Long id = 1L;
        Employee employee = new Employee();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.findById(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(employee, result.get());
        Mockito.verify(repository).findById(id);
    }
}