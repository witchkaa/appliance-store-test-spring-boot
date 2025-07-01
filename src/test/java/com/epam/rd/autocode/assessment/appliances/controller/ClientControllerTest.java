package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestCreateDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestEditDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Model model;

    @InjectMocks
    private ClientController controller;

    @Test
    void listShouldAddAttributesAndReturnView() {
        Long id = 1L;
        String name = "John";

        Client client = new Client();
        ClientResponseDto dto = new ClientResponseDto();

        when(clientService.search(id, name)).thenReturn(List.of(client));
        when(modelMapper.map(client, ClientResponseDto.class)).thenReturn(dto);

        String view = controller.list(id, name, model);

        verify(clientService).search(id, name);
        verify(modelMapper).map(client, ClientResponseDto.class);
        verify(model).addAttribute("clients", List.of(dto));
        verify(model).addAttribute("searchId", id);
        verify(model).addAttribute("searchName", name);

        assertEquals("client/clients", view);
    }

    @Test
    void createFormShouldAddEmptyDtoAndReturnView() {
        String view = controller.createForm(model);

        verify(model).addAttribute(eq("client"), any(ClientRequestCreateDto.class));
        assertEquals("client/newClient", view);
    }

    @Test
    void saveWithValidationErrorsShouldReturnForm() {
        ClientRequestCreateDto dto = new ClientRequestCreateDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String view = controller.save(dto, result);

        verify(result).hasErrors();
        verifyNoInteractions(clientService, modelMapper);
        assertEquals("client/newClient", view);
    }

    @Test
    void saveValidDtoShouldSaveAndRedirect() {
        ClientRequestCreateDto dto = new ClientRequestCreateDto();
        Client client = new Client();

        when(modelMapper.map(dto, Client.class)).thenReturn(client);
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String view = controller.save(dto, result);

        verify(clientService).save(client);
        assertEquals("redirect:/clients", view);
    }

    @Test
    void deleteShouldCallServiceAndRedirect() {
        Long id = 42L;

        String view = controller.delete(id);

        verify(clientService).delete(id);
        assertEquals("redirect:/clients", view);
    }

    @Test
    void editFormClientNotFoundShouldThrowException() {
        Long id = 10L;
        when(clientService.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> controller.editForm(id, model));
    }

    @Test
    void updateWithValidationErrorsShouldReturnEditForm() {
        Long id = 7L;
        ClientRequestEditDto dto = new ClientRequestEditDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String view = controller.update(id, dto, result);

        verify(result).hasErrors();
        assertEquals("client/editClient", view);
    }

    @Test
    void updateClientNotFoundShouldThrowException() {
        Long id = 999L;
        ClientRequestEditDto dto = new ClientRequestEditDto();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        when(clientService.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> controller.update(id, dto, result));
    }
}