package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
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
        return repository.findAll();
    }

    public Employee save(Employee employee) {
        log.info("Saving employee: {}", employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return repository.save(employee);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public Optional<Employee> findById(Long id) {
        return repository.findById(id);
    }

    public List<Employee> getAllSorted(String sortBy) {
        return repository.findAll(resolveSort(sortBy));
    }

    @Override
    public List<Employee> searchAndSort(Long id, String name, String department, String sortBy) {
        Sort sort = resolveSort(sortBy);

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

    private Sort resolveSort(String sortBy) {
        return switch (sortBy) {
            case "name" -> Sort.by("name");
            case "department" -> Sort.by("department");
            default -> Sort.by("id");
        };
    }
}