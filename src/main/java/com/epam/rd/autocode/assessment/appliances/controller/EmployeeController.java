package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.getAll());
        return "employee/employees";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@ModelAttribute @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        employeeService.delete(id);
        return "redirect:/employees";
    }
}
