package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void save_ShouldEncodePasswordAndSave() {
        Employee employee = new Employee();
        employee.setPassword("rawPassword");

        when(repository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee saved = employeeService.save(employee);

        assertNotNull(saved.getPassword());
        assertNotEquals("rawPassword", saved.getPassword());
        verify(repository).save(employee);
    }

    @Test
    void delete_ExistingId_ShouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        employeeService.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_NonExistingId_ShouldThrow() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.delete(1L));
    }

    @Test
    void findById_ShouldReturnOptional() {
        Employee employee = new Employee();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<Employee> found = employeeService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(employee, found.get());
    }

    @Test
    void getAllSorted_DefaultSortById() {
        List<Employee> employees = List.of(new Employee(), new Employee());
        when(repository.findAll(Sort.by("id"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllSorted("unknown");

        assertEquals(2, result.size());
        verify(repository).findAll(Sort.by("id"));
    }

    @Test
    void getAllSorted_SortByName() {
        List<Employee> employees = List.of(new Employee());
        when(repository.findAll(Sort.by("name"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllSorted("name");

        assertEquals(1, result.size());
        verify(repository).findAll(Sort.by("name"));
    }

    @Test
    void getAllSorted_SortByDepartment() {
        List<Employee> employees = List.of(new Employee());
        when(repository.findAll(Sort.by("department"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllSorted("department");

        assertEquals(1, result.size());
        verify(repository).findAll(Sort.by("department"));
    }

    @Test
    void searchAndSort_ByIdFound() {
        Employee employee = new Employee();
        when(repository.findById(5L)).thenReturn(Optional.of(employee));

        List<Employee> result = employeeService.searchAndSort(5L, null, null, "id");

        assertEquals(1, result.size());
        assertEquals(employee, result.get(0));
    }

    @Test
    void searchAndSort_ByIdNotFound() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        List<Employee> result = employeeService.searchAndSort(5L, null, null, "id");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAndSort_ByNameOrDepartment() {
        List<Employee> employees = List.of(new Employee(), new Employee());
        Sort sort = Sort.by("name");
        when(repository.findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(
                "John", "Sales", sort)).thenReturn(employees);

        List<Employee> result = employeeService.searchAndSort(null, "John", "Sales", "name");

        assertEquals(2, result.size());
    }

    @Test
    void searchAndSort_NoFilters() {
        List<Employee> employees = List.of(new Employee());
        Sort sort = Sort.by("id");
        when(repository.findAll(sort)).thenReturn(employees);

        List<Employee> result = employeeService.searchAndSort(null, null, null, "id");

        assertEquals(1, result.size());
    }
}