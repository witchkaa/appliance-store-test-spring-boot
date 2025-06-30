package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Model model;

    @InjectMocks
    private EmployeeController controller;

    @Test
    void list_ShouldAddAttributesAndReturnView() {
        Long id = 1L;
        String name = "Alice";
        String department = "IT";
        String sortBy = "name";

        Employee employee = new Employee();
        EmployeeResponseDto dto = new EmployeeResponseDto();

        when(employeeService.searchAndSort(id, name, department, sortBy)).thenReturn(List.of(employee));
        when(modelMapper.map(employee, EmployeeResponseDto.class)).thenReturn(dto);

        String view = controller.list(id, name, department, sortBy, model);

        verify(employeeService).searchAndSort(id, name, department, sortBy);
        verify(modelMapper).map(employee, EmployeeResponseDto.class);
        verify(model).addAttribute("employees", List.of(dto));
        verify(model).addAttribute("currentSort", sortBy);
        verify(model).addAttribute("searchId", id);
        verify(model).addAttribute("searchName", name);
        verify(model).addAttribute("searchDepartment", department);

        assertEquals("employee/employees", view);
    }

    @Test
    void createForm_ShouldAddEmptyDtoAndReturnView() {
        String view = controller.createForm(model);

        verify(model).addAttribute(eq("employee"), any(EmployeeRequestDto.class));
        assertEquals("employee/newEmployee", view);
    }

    @Test
    void save_WithValidationErrors_ShouldReturnForm() {
        EmployeeRequestDto dto = new EmployeeRequestDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String view = controller.save(dto, result);

        verify(result).hasErrors();
        verifyNoInteractions(employeeService, modelMapper);
        assertEquals("employee/newEmployee", view);
    }

    @Test
    void save_ValidDto_ShouldSaveAndRedirect() {
        EmployeeRequestDto dto = new EmployeeRequestDto();
        Employee employee = new Employee();

        when(modelMapper.map(dto, Employee.class)).thenReturn(employee);
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String view = controller.save(dto, result);

        verify(employeeService).save(employee);
        assertEquals("redirect:/employees", view);
    }

    @Test
    void delete_ShouldCallServiceAndRedirect() {
        Long id = 42L;

        String view = controller.delete(id);

        verify(employeeService).delete(id);
        assertEquals("redirect:/employees", view);
    }

    @Test
    void editForm_ShouldPopulateModelAndReturnView() {
        Long id = 5L;
        Employee employee = new Employee();
        EmployeeRequestDto dto = new EmployeeRequestDto();

        when(employeeService.findById(id)).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeRequestDto.class)).thenReturn(dto);

        String view = controller.editForm(id, model);

        verify(employeeService).findById(id);
        verify(modelMapper).map(employee, EmployeeRequestDto.class);
        verify(model).addAttribute("employee", dto);

        assertEquals("employee/editEmployee", view);
    }

    @Test
    void editForm_EmployeeNotFound_ShouldThrowException() {
        Long id = 10L;
        when(employeeService.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> controller.editForm(id, model));
    }

    @Test
    void update_WithValidationErrors_ShouldReturnEditForm() {
        Long id = 7L;
        EmployeeRequestDto dto = new EmployeeRequestDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String view = controller.update(id, dto, result);

        verify(result).hasErrors();
        verifyNoInteractions(employeeService, modelMapper);
        assertEquals("employee/editEmployee", view);
    }

    @Test
    void update_ValidDto_PasswordBlank_ShouldKeepOldPassword() {
        Long id = 3L;
        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setPassword(" "); // blank password

        Employee existing = new Employee();
        existing.setPassword("oldPassword");

        when(employeeService.findById(id)).thenReturn(Optional.of(existing));
        when(modelMapper.map(dto, Employee.class)).thenReturn(new Employee());

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String view = controller.update(id, dto, result);

        // Проверяем, что пароль обновился в dto на старый
        assertEquals("oldPassword", dto.getPassword());

        verify(employeeService).save(any(Employee.class));
        assertEquals("redirect:/employees", view);
    }

    @Test
    void update_ValidDto_ShouldSaveAndRedirect() {
        Long id = 3L;
        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setPassword("newPass");

        Employee existing = new Employee();
        existing.setPassword("oldPassword");

        Employee employeeEntity = new Employee();

        when(employeeService.findById(id)).thenReturn(Optional.of(existing));
        when(modelMapper.map(dto, Employee.class)).thenReturn(employeeEntity);

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String view = controller.update(id, dto, result);

        verify(employeeService).save(employeeEntity);
        assertEquals(id, employeeEntity.getId());
        assertEquals("redirect:/employees", view);
    }

    @Test
    void update_EmployeeNotFound_ShouldThrowException() {
        Long id = 999L;
        EmployeeRequestDto dto = new EmployeeRequestDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(employeeService.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> controller.update(id, dto, result));
    }
}