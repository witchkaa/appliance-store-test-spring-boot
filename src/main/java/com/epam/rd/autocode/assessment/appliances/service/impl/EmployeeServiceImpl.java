package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<Employee> getAll() {
        log.info("Fetching all employees");
        List<Employee> employees = repository.findAll();
        log.debug("Found {} employees", employees.size());
        return employees;
    }

    @Override
    public Employee save(Employee employee) {
        log.info("Saving employee: {}", employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        Employee saved = repository.save(employee);
        log.info("Employee saved with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting employee with id: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Employee with id {} not found for deletion", id);
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Employee with id {} deleted", id);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        log.info("Finding employee by id: {}", id);
        Optional<Employee> employee = repository.findById(id);
        if (employee.isPresent()) {
            log.debug("Employee found: {}", employee.get());
        } else {
            log.warn("Employee with id {} not found", id);
        }
        return employee;
    }

    @Override
    public List<Employee> getAllSorted(String sortBy) {
        log.info("Fetching all employees sorted by: {}", sortBy);
        List<Employee> employees = repository.findAll(resolveSort(sortBy));
        log.debug("Found {} employees", employees.size());
        return employees;
    }

    @Override
    public List<Employee> searchAndSort(Long id, String name, String department, String sortBy) {
        log.info("Searching employees with id: {}, name: {}, department: {}, sorted by: {}", id, name, department, sortBy);
        Sort sort = resolveSort(sortBy);
        List<Employee> result;

        if (id != null) {
            result = repository.findById(id)
                    .map(List::of)
                    .orElseGet(List::of);
            log.debug("Search by id result size: {}", result.size());
            return result;
        }

        if ((name != null && !name.isBlank()) || (department != null && !department.isBlank())) {
            result = repository.findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(
                    name == null ? "" : name,
                    department == null ? "" : department,
                    sort
            );
            log.debug("Search by name/department result size: {}", result.size());
            return result;
        }

        result = repository.findAll(sort);
        log.debug("Search without filters result size: {}", result.size());
        return result;
    }

    private Sort resolveSort(String sortBy) {
        return switch (sortBy) {
            case "name" -> Sort.by("name");
            case "department" -> Sort.by("department");
            default -> Sort.by("id");
        };
    }
}