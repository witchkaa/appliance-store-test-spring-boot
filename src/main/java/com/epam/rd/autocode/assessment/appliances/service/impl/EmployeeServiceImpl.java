package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;

    public List<Employee> getAll() {
        log.debug("Getting all employees from repository");
        return repository.findAll();
    }

    public Employee save(Employee employee) {
        log.info("Saving employee to repository: {}", employee);
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
}
