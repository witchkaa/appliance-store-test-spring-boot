package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAll();
    Employee save(Employee employee);
    void delete(Long id);
    Optional<Employee> findById(Long id);
    List<Employee> getAllSorted(String sortBy);
    List<Employee> searchAndSort(Long id, String name, String department, String sortBy);
}
