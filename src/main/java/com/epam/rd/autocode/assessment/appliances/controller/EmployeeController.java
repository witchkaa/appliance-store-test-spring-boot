package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String department,
                       @RequestParam(required = false, defaultValue = "id") String sortBy,
                       Model model) {

        List<Employee> employees = employeeService.searchAndSort(id, name, department, sortBy);
        model.addAttribute("employees", employees);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("searchId", id);
        model.addAttribute("searchName", name);
        model.addAttribute("searchDepartment", department);
        return "employee/employees";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@ModelAttribute @Valid Employee employee,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        employeeService.delete(id); // выбросит EmployeeNotFoundException, если не найден
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
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
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (employee.getPassword() == null || employee.getPassword().isBlank()) {
            employee.setPassword(existing.getPassword());
        }

        employee.setId(id);
        employeeService.save(employee);
        return "redirect:/employees";
    }
}