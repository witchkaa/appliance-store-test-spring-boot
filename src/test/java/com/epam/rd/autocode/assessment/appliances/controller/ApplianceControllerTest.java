package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.service.*;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ApplianceControllerTest {

    @Mock
    private ApplianceService applianceService;

    @Mock
    private ManufacturerService manufacturerService;

    @InjectMocks
    private ApplianceController applianceController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Test
    void createForm_shouldAddAttributesAndReturnView() {
        List<Manufacturer> manufacturers = List.of(new Manufacturer());
        Mockito.when(manufacturerService.getAll()).thenReturn(manufacturers);

        String view = applianceController.createForm(model);

        Mockito.verify(model).addAttribute(eq("appliance"), any(Appliance.class));
        Mockito.verify(model).addAttribute("manufacturers", manufacturers);
        Mockito.verify(model).addAttribute("categories", Category.values());
        Mockito.verify(model).addAttribute("powerTypes", PowerType.values());
        Assertions.assertEquals("appliance/newAppliance", view);
    }


    @Test
    void delete_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = applianceController.delete(id);

        Mockito.verify(applianceService).delete(id);
        Assertions.assertEquals("redirect:/appliances", view);
    }

    @Test
    void editForm_whenApplianceFound_shouldAddAttributesAndReturnView() {
        Long id = 1L;
        Appliance appliance = new Appliance();
        List<Manufacturer> manufacturers = List.of(new Manufacturer());
        Mockito.when(applianceService.findById(id)).thenReturn(Optional.of(appliance));
        Mockito.when(manufacturerService.getAll()).thenReturn(manufacturers);

        String view = applianceController.editForm(id, model);

        Mockito.verify(model).addAttribute("appliance", appliance);
        Mockito.verify(model).addAttribute("manufacturers", manufacturers);
        Mockito.verify(model).addAttribute("categories", Category.values());
        Mockito.verify(model).addAttribute("powerTypes", PowerType.values());
        Assertions.assertEquals("appliance/editAppliance", view);
    }

    @Test
    void editForm_whenApplianceNotFound_shouldThrowException() {
        Long id = 1L;
        Mockito.when(applianceService.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResponseStatusException.class, () -> applianceController.editForm(id, model));
    }

}
