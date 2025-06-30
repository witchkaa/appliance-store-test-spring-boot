package com.epam.rd.autocode.assessment.appliances.controller;

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
    private LoginController controller;

    @Test
    void login_WithErrorAndNoMessageKey_ShouldUseDefaultErrorMessage() {
        Locale locale = Locale.ENGLISH;
        when(model.asMap()).thenReturn(Collections.emptyMap());
        when(messageSource.getMessage("login.error.invalid", null, locale)).thenReturn("Invalid username or password");

        String view = controller.login("someError", null, model, locale);

        verify(model).addAttribute("loginErrorMessage", "Invalid username or password");
        assertEquals("auth/login", view);
    }

    @Test
    void login_WithErrorAndMessageKeyInModel_ShouldUseThatMessageKey() {
        Locale locale = Locale.ENGLISH;
        when(model.asMap()).thenReturn(Map.of("loginErrorMessage", "login.error.locked"));
        when(messageSource.getMessage("login.error.locked", null, locale)).thenReturn("Account locked");

        String view = controller.login("error", null, model, locale);

        verify(model).addAttribute("loginErrorMessage", "Account locked");
        assertEquals("auth/login", view);
    }


    @Test
    void registerForm_ShouldAddNewClientAndReturnRegisterView() {
        String view = controller.registerForm(model);

        verify(model).addAttribute(eq("client"), any(Client.class));
        assertEquals("auth/register", view);
    }

    @Test
    void register_WhenEmailExists_ShouldRejectEmailAndReturnRegisterView() {
        Client client = new Client();
        client.setEmail("test@example.com");
        when(clientService.emailExists("test@example.com")).thenReturn(true);

        String view = controller.register(client, bindingResult, model, redirectAttributes, Locale.ENGLISH);

        verify(bindingResult).rejectValue("email", "register.error.email.exists");
        assertEquals("redirect:/login", view);
    }

    @Test
    void register_WhenNoErrors_ShouldSaveClientAndRedirectToLogin() {
        Client client = new Client();
        client.setEmail("new@example.com");
        when(clientService.emailExists("new@example.com")).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(messageSource.getMessage("register.success", null, Locale.ENGLISH)).thenReturn("Registration successful");

        String view = controller.register(client, bindingResult, model, redirectAttributes, Locale.ENGLISH);

        verify(clientService).save(client);
        verify(redirectAttributes).addFlashAttribute("message", "Registration successful");
        assertEquals(Role.CLIENT, client.getRole());
        assertEquals("redirect:/login", view);
    }
}