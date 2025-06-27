package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Employee> getAll() {
        log.debug("Getting all employees from repository");
        return repository.findAll();
    }

    public Employee save(Employee employee) {
        log.info("Saving employee to repository: {}", employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return repository.save(employee);
    }

    public void delete(Long id) {
        log.warn("Deleting employee with id {}", id);
        repository.deleteById(id);
    }

    public Optional<Employee> findById(Long id) {
        log.debug("Finding employee by id: {}", id);
        return repository.findById(id);
    }
    public List<Employee> getAllSorted(String sortBy) {
        log.debug("Getting all employees sorted by {}", sortBy);

        Sort sort;
        switch (sortBy) {
            case "name" -> sort = Sort.by("name");
            case "department" -> sort = Sort.by("department");
            default -> sort = Sort.by("id"); // fallback
        }

        return repository.findAll(sort);
    }
    @Override
    public List<Employee> searchAndSort(Long id, String name, String department, String sortBy) {
        log.debug("Searching employees by id={}, name='{}', department='{}', sorted by '{}'",
                id, name, department, sortBy);

        Sort sort;
        switch (sortBy) {
            case "name" -> sort = Sort.by("name");
            case "department" -> sort = Sort.by("department");
            default -> sort = Sort.by("id");
        }

        if (id != null) {
            return repository.findById(id)
                    .map(List::of)
                    .orElseGet(List::of);
        }

        if ((name != null && !name.isBlank()) || (department != null && !department.isBlank())) {
            return repository.findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(
                    name == null ? "" : name,
                    department == null ? "" : department,
                    sort
            );
        }

        return repository.findAll(sort);
    }
}
