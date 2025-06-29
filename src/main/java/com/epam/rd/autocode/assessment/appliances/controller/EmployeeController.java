package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String department,
                       @RequestParam(required = false, defaultValue = "id") String sortBy,
                       Model model) {

        List<Employee> employees = employeeService.searchAndSort(id, name, department, sortBy);

        List<EmployeeResponseDto> dtos = employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeResponseDto.class))
                .toList();

        model.addAttribute("employees", dtos);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("searchId", id);
        model.addAttribute("searchName", name);
        model.addAttribute("searchDepartment", department);
        return "employee/employees";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDto());
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@ModelAttribute("employee") @Valid EmployeeRequestDto dto,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        Employee employee = modelMapper.map(dto, Employee.class);
        employeeService.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        employeeService.delete(id);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        EmployeeRequestDto dto = modelMapper.map(employee, EmployeeRequestDto.class);
        model.addAttribute("employee", dto);
        return "employee/editEmployee";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("employee") @Valid EmployeeRequestDto dto,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "employee/editEmployee";
        }

        Employee existing = employeeService.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            dto.setPassword(existing.getPassword());
        }

        Employee employee = modelMapper.map(dto, Employee.class);
        employee.setId(id);
        employeeService.save(employee);
        return "redirect:/employees";
    }
}