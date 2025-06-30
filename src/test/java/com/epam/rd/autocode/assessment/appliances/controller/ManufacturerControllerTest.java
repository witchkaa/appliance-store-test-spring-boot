package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerDeleteException;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerControllerTest {

    @Mock
    private ManufacturerService manufacturerService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ManufacturerController controller;

    @Test
    void list_ShouldAddAttributesAndReturnView() {
        Long id = 1L;
        String name = "Acme";

        Manufacturer manufacturer = new Manufacturer();
        ManufacturerResponseDto dto = new ManufacturerResponseDto();

        when(manufacturerService.search(id, name)).thenReturn(List.of(manufacturer));
        when(modelMapper.map(manufacturer, ManufacturerResponseDto.class)).thenReturn(dto);

        String view = controller.list(id, name, model);

        verify(manufacturerService).search(id, name);
        verify(modelMapper).map(manufacturer, ManufacturerResponseDto.class);
        verify(model).addAttribute("manufacturers", List.of(dto));
        verify(model).addAttribute("paramId", id);
        verify(model).addAttribute("paramName", name);

        assertEquals("manufacture/manufacturers", view);
    }

    @Test
    void createForm_ShouldAddEmptyDtoAndReturnView() {
        String view = controller.createForm(model);

        verify(model).addAttribute(eq("manufacturer"), any(ManufacturerRequestDto.class));
        assertEquals("manufacture/newManufacturer", view);
    }

    @Test
    void save_WithValidationErrors_ShouldReturnForm() {
        ManufacturerRequestDto dto = new ManufacturerRequestDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String view = controller.save(dto, result);

        verify(result).hasErrors();
        verifyNoInteractions(manufacturerService, modelMapper);
        assertEquals("manufacture/newManufacturer", view);
    }

    @Test
    void save_ValidDto_ShouldSaveAndRedirect() {
        ManufacturerRequestDto dto = new ManufacturerRequestDto();
        Manufacturer manufacturer = new Manufacturer();

        when(modelMapper.map(dto, Manufacturer.class)).thenReturn(manufacturer);
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String view = controller.save(dto, result);

        verify(manufacturerService).save(manufacturer);
        assertEquals("redirect:/manufacturers", view);
    }

    @Test
    void delete_Success_ShouldRedirectWithSuccessMessage() {
        Long id = 10L;

        String view = controller.delete(id, redirectAttributes);

        verify(manufacturerService).delete(id);
        verify(redirectAttributes).addFlashAttribute("success", "Manufacturer deleted successfully.");
        assertEquals("redirect:/manufacturers", view);
    }

    @Test
    void delete_ManufacturerDeleteException_ShouldRedirectWithErrorMessage() {
        Long id = 10L;
        String errorMessage = "Cannot delete manufacturer";

        doThrow(new ManufacturerDeleteException(errorMessage)).when(manufacturerService).delete(id);

        String view = controller.delete(id, redirectAttributes);

        verify(manufacturerService).delete(id);
        verify(redirectAttributes).addFlashAttribute("error", errorMessage);
        assertEquals("redirect:/manufacturers", view);
    }
}