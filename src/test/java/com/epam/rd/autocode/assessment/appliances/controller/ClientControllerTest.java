package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.*;
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
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;


    @Test
    void createForm_shouldAddNewClientAndReturnView() {
        String view = clientController.createForm(model);

        Mockito.verify(model).addAttribute(eq("client"), any(Client.class));
        Assertions.assertEquals("client/newClient", view);
    }

    @Test
    void save_whenValidationErrors_shouldReturnFormView() {
        Client client = new Client();
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        String view = clientController.save(client, bindingResult);

        Assertions.assertEquals("client/newClient", view);
    }

    @Test
    void save_whenNoValidationErrors_shouldSaveAndRedirect() {
        Client client = new Client();
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        String view = clientController.save(client, bindingResult);

        Mockito.verify(clientService).save(client);
        Assertions.assertEquals("redirect:/clients", view);
    }

    @Test
    void delete_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = clientController.delete(id);

        Mockito.verify(clientService).delete(id);
        Assertions.assertEquals("redirect:/clients", view);
    }

    @Test
    void editForm_whenClientFound_shouldAddClientAndReturnView() {
        Long id = 1L;
        Client client = new Client();
        Mockito.when(clientService.findById(id)).thenReturn(Optional.of(client));

        String view = clientController.editForm(id, model);

        Mockito.verify(model).addAttribute("client", client);
        Assertions.assertEquals("client/editClient", view);
    }

    @Test
    void editForm_whenClientNotFound_shouldThrowException() {
        Long id = 1L;
        Mockito.when(clientService.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> clientController.editForm(id, model));
    }

    @Test
    void update_whenValidationErrors_shouldReturnFormView() {
        Long id = 1L;
        Client client = new Client();
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        String view = clientController.update(id, client, bindingResult);

        Assertions.assertEquals("client/editClient", view);
    }

    @Test
    void update_whenNoValidationErrorsAndPasswordBlank_shouldKeepOldPasswordAndSave() {
        Long id = 1L;
        Client client = new Client();
        client.setPassword("");
        Client existing = new Client();
        existing.setPassword("encodedPassword");

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(clientService.findById(id)).thenReturn(Optional.of(existing));

        String view = clientController.update(id, client, bindingResult);

        Assertions.assertEquals("redirect:/clients", view);
        Assertions.assertEquals("encodedPassword", client.getPassword());
        Mockito.verify(clientService).save(client);
    }

    @Test
    void update_whenNoValidationErrorsAndPasswordSet_shouldSaveWithNewPassword() {
        Long id = 1L;
        Client client = new Client();
        client.setPassword("newPassword");
        Client existing = new Client();
        existing.setPassword("oldPassword");

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(clientService.findById(id)).thenReturn(Optional.of(existing));

        String view = clientController.update(id, client, bindingResult);

        Assertions.assertEquals("redirect:/clients", view);
        Assertions.assertEquals("newPassword", client.getPassword());
        Mockito.verify(clientService).save(client);
    }

    @Test
    void update_whenClientNotFound_shouldThrowException() {
        Long id = 1L;
        Client client = new Client();

        Mockito.when(bindingResult.hasErrors()).thenReturn(false);
        Mockito.when(clientService.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> clientController.update(id, client, bindingResult));
    }
}
