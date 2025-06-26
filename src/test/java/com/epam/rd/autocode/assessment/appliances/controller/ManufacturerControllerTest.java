package com.epam.rd.autocode.assessment.appliances.controller;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ManufacturerControllerTest {

    @Mock
    private ManufacturerService manufacturerService;

    @InjectMocks
    private ManufacturerController manufacturerController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Test
    void list_shouldAddManufacturersToModelAndReturnView() {
        List<Manufacturer> manufacturers = List.of(new Manufacturer());
        Mockito.when(manufacturerService.getAll()).thenReturn(manufacturers);

        String view = manufacturerController.list(model);

        Mockito.verify(model).addAttribute("manufacturers", manufacturers);
        Assertions.assertEquals("manufacture/manufacturers", view);
    }

    @Test
    void createForm_shouldAddNewManufacturerAndReturnView() {
        String view = manufacturerController.createForm(model);

        Mockito.verify(model).addAttribute(eq("manufacturer"), any(Manufacturer.class));
        Assertions.assertEquals("manufacture/newManufacturer", view);
    }

    @Test
    void save_whenValidationErrors_shouldReturnFormView() {
        Manufacturer manufacturer = new Manufacturer();
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        String view = manufacturerController.save(manufacturer, bindingResult);

        Assertions.assertEquals("manufacture/newManufacturer", view);
    }

    @Test
    void save_whenNoValidationErrors_shouldSaveAndRedirect() {
        Manufacturer manufacturer = new Manufacturer();
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        String view = manufacturerController.save(manufacturer, bindingResult);

        Mockito.verify(manufacturerService).save(manufacturer);
        Assertions.assertEquals("redirect:/manufacturers", view);
    }

    @Test
    void delete_whenNoException_shouldAddSuccessFlashAndRedirect() {
        Long id = 1L;

        String view = manufacturerController.delete(id, redirectAttributes);

        Mockito.verify(manufacturerService).delete(id);
        Mockito.verify(redirectAttributes).addFlashAttribute("success", "Manufacturer deleted successfully.");
        Assertions.assertEquals("redirect:/manufacturers", view);
    }

    @Test
    void delete_whenIllegalStateException_shouldAddErrorFlashAndRedirect() {
        Long id = 1L;
        Mockito.doThrow(new IllegalStateException("Cannot delete manufacturer with existing appliances."))
                .when(manufacturerService).delete(id);

        String view = manufacturerController.delete(id, redirectAttributes);

        Mockito.verify(manufacturerService).delete(id);
        Mockito.verify(redirectAttributes).addFlashAttribute("error", "Cannot delete manufacturer with existing appliances.");
        Assertions.assertEquals("redirect:/manufacturers", view);
    }
}
