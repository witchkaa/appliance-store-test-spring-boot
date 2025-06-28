package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.service.impl.ApplianceServiceImpl;
import org.junit.jupiter.api.Test;
import com.epam.rd.autocode.assessment.appliances.repository.*;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Test
    void createForm_shouldAddNewEmployeeAndReturnView() {
        String view = employeeController.createForm(model);

        Mockito.verify(model).addAttribute(eq("employee"), any(Employee.class));
        Assertions.assertEquals("employee/newEmployee", view);
    }

    @Test
    void save_whenValidationErrors_shouldReturnFormView() {
        Employee employee = new Employee();
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        String view = employeeController.save(employee, bindingResult);

        Assertions.assertEquals("employee/newEmployee", view);
    }

    @Test
    void save_whenNoValidationErrors_shouldSaveAndRedirect() {
        Employee employee = new Employee();
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        String view = employeeController.save(employee, bindingResult);

        Mockito.verify(employeeService).save(employee);
        Assertions.assertEquals("redirect:/employees", view);
    }

    @Test
    void delete_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = employeeController.delete(id);

        Mockito.verify(employeeService).delete(id);
        Assertions.assertEquals("redirect:/employees", view);
    }

    @Test
    void editForm_whenEmployeeFound_shouldAddEmployeeAndReturnView() {
        Long id = 1L;
        Employee employee = new Employee();
        Mockito.when(employeeService.findById(id)).thenReturn(Optional.of(employee));

        String view = employeeController.editForm(id, model);

        Mockito.verify(model).addAttribute("employee", employee);
        Assertions.assertEquals("employee/editEmployee", view);
    }

    @Test
    void editForm_whenEmployeeNotFound_shouldThrowException() {
        Long id = 1L;
        Mockito.when(employeeService.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> employeeController.editForm(id, model));
    }

    @Test
    void update_whenValidationErrors_shouldReturnFormView() {
        Long id = 1L;
        Employee employee = new Employee();
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        String view = employeeController.update(id, employee, bindingResult);

        Assertions.assertEquals("employee/editEmployee", view);
    }

    @Test
    void update_whenNoValidationErrorsAndPasswordBlank_shouldKeepOldPasswordAndSave() {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setPassword("");
        Employee existing = new Employee();
        existing.setPassword("encodedPassword");

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(employeeService.findById(id)).thenReturn(Optional.of(existing));

        String view = employeeController.update(id, employee, bindingResult);

        Assertions.assertEquals("redirect:/employees", view);
        Assertions.assertEquals("encodedPassword", employee.getPassword());
        Mockito.verify(employeeService).save(employee);
    }

    @Test
    void update_whenNoValidationErrorsAndPasswordSet_shouldSaveWithNewPassword() {
        Long id = 1L;
        Employee employee = new Employee();
        employee.setPassword("newPassword");
        Employee existing = new Employee();
        existing.setPassword("oldPassword");

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(employeeService.findById(id)).thenReturn(Optional.of(existing));

        String view = employeeController.update(id, employee, bindingResult);

        Assertions.assertEquals("redirect:/employees", view);
        Assertions.assertEquals("newPassword", employee.getPassword());
        Mockito.verify(employeeService).save(employee);
    }

    @Test
    void update_whenEmployeeNotFound_shouldThrowException() {
        Long id = 1L;
        Employee employee = new Employee();

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(employeeService.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> employeeController.update(id, employee, bindingResult));
    }
}