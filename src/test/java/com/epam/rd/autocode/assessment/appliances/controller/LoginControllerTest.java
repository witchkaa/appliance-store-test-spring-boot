package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRegistrationDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Role;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private LoginController loginController;

    @Test
    void login_ShouldReturnLoginView_WhenNoError() {
        String viewName = loginController.login(null, model, Locale.ENGLISH);
        assertEquals("auth/login", viewName);
        verifyNoInteractions(messageSource);
    }

    @Test
    void login_ShouldAddErrorMessage_WhenInvalidCredentials() {
        when(messageSource.getMessage("login.error.invalid", null, Locale.ENGLISH))
                .thenReturn("Invalid credentials");

        String viewName = loginController.login("invalid", model, Locale.ENGLISH);

        assertEquals("auth/login", viewName);
        verify(model).addAttribute("loginErrorMessage", "Invalid credentials");
    }

    @Test
    void login_ShouldAddLockedMessage_WhenAccountLocked() {
        when(messageSource.getMessage("login.error.locked", null, Locale.ENGLISH))
                .thenReturn("Account locked");

        String viewName = loginController.login("locked", model, Locale.ENGLISH);

        assertEquals("auth/login", viewName);
        verify(model).addAttribute("loginErrorMessage", "Account locked");
    }

    @Test
    void registerForm_ShouldReturnRegisterViewWithClientDto() {
        String viewName = loginController.registerForm(model);

        assertEquals("auth/register", viewName);
        verify(model).addAttribute(eq("client"), any(ClientRegistrationDto.class));
    }


}