package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        log.info("Fetching list of employees");
        model.addAttribute("employees", employeeService.getAll());
        return "employee/employees";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening form to add a new employee");
        model.addAttribute("employee", new Employee());
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@ModelAttribute @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Validation error while adding employee: {}", result.getAllErrors());
            return "employee/newEmployee";
        }
        log.info("Saving employee: {}", employee);
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("Deleting employee with id: {}", id);
        employeeService.delete(id);
        return "redirect:/employees";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        model.addAttribute("employee", employee);
        return "employee/editEmployee";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Employee employee,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "employee/editEmployee";
        }

        Employee existing = employeeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        if (employee.getPassword() == null || employee.getPassword().isBlank()) {
            employee.setPassword(existing.getPassword());
        }

        employee.setId(id);
        employeeService.save(employee);
        return "redirect:/employees";
    }
}
