package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplianceControllerTest {

    @Mock
    private ApplianceService applianceService;

    @Mock
    private ManufacturerService manufacturerService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Model model;

    @InjectMocks
    private ApplianceController controller;

    @Test
    void createForm_ShouldAddAttributesAndReturnView() {
        String view = controller.createForm(model);

        verify(model).addAttribute(eq("appliance"), any(ApplianceRequestDto.class));
        verify(model).addAttribute("types", ProductType.values());
        verify(manufacturerService).getAll();
        verify(model).addAttribute(eq("manufacturers"), any());
        verify(model).addAttribute("categories", Category.values());
        verify(model).addAttribute("powerTypes", PowerType.values());

        assertEquals("appliance/newAppliance", view);
    }

    @Test
    void save_WithErrors_ShouldReturnForm() {
        ApplianceRequestDto dto = new ApplianceRequestDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.save(dto, bindingResult, model);

        verify(bindingResult).hasErrors();
        verify(manufacturerService).getAll();
        verify(model).addAttribute(eq("manufacturers"), any());
        verify(model).addAttribute("categories", Category.values());
        verify(model).addAttribute("powerTypes", PowerType.values());

        assertEquals("appliance/newAppliance", view);
        verifyNoInteractions(applianceService);
    }

    @Test
    void save_ValidDto_ShouldSaveAndRedirect() {
        ApplianceRequestDto dto = new ApplianceRequestDto();
        dto.setManufacturerId(1L);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);

        when(manufacturerService.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(modelMapper.map(dto, Appliance.class)).thenReturn(new Appliance());

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.save(dto, bindingResult, model);

        verify(applianceService).save(any(Appliance.class));
        assertEquals("redirect:/appliances", view);
    }

    @Test
    void delete_ShouldCallServiceAndRedirect() {
        Long id = 5L;
        String view = controller.delete(id);
        verify(applianceService).delete(id);
        assertEquals("redirect:/appliances", view);
    }

    @Test
    void editForm_ShouldPopulateModelAndReturnView() {
        Long id = 10L;
        Appliance appliance = new Appliance();
        ApplianceRequestDto dto = new ApplianceRequestDto();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(2L);
        appliance.setManufacturer(manufacturer);

        when(applianceService.findById(id)).thenReturn(Optional.of(appliance));
        when(modelMapper.map(appliance, ApplianceRequestDto.class)).thenReturn(dto);

        String view = controller.editForm(id, model);

        verify(model).addAttribute("appliance", dto);
        verify(model).addAttribute("types", ProductType.values());
        verify(manufacturerService).getAll();
        verify(model).addAttribute(eq("manufacturers"), any());
        verify(model).addAttribute("categories", Category.values());
        verify(model).addAttribute("powerTypes", PowerType.values());

        assertEquals("appliance/editAppliance", view);
    }

    @Test
    void updateAppliance_WithErrors_ShouldReturnEditForm() {
        Long id = 7L;
        ApplianceRequestDto dto = new ApplianceRequestDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.updateAppliance(id, dto, bindingResult, model);

        verify(manufacturerService).getAll();
        verify(model).addAttribute(eq("manufacturers"), any());
        verify(model).addAttribute("categories", Category.values());
        verify(model).addAttribute("powerTypes", PowerType.values());

        assertEquals("appliance/editAppliance", view);
        verifyNoInteractions(applianceService);
    }

    @Test
    void updateAppliance_ValidDto_ShouldSaveAndRedirect() {
        Long id = 7L;
        ApplianceRequestDto dto = new ApplianceRequestDto();
        dto.setManufacturerId(1L);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);

        Appliance appliance = new Appliance();

        when(manufacturerService.findById(1L)).thenReturn(Optional.of(manufacturer));
        when(modelMapper.map(dto, Appliance.class)).thenReturn(appliance);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.updateAppliance(id, dto, bindingResult, model);

        verify(applianceService).save(appliance);
        assertEquals(id, appliance.getId());
        assertEquals("redirect:/appliances", view);
    }
}