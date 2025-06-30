package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.PasswordResetDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    @Mock
    private ClientRepository clientRepository;


    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private PasswordResetController controller;

    private final String email = "user@example.com";

    @Test
    void showResetRequestForm_ShouldReturnView() {
        String view = controller.showResetRequestForm();
        assertEquals("auth/request-reset", view);
    }

    @Test
    void handleResetRequest_EmailFound_ShouldPutCodeAndRedirect() {
        Client client = new Client();
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));

        String view = controller.handleResetRequest(email, model, redirectAttributes);

        assertEquals("redirect:/reset/verify?email=" + email, view);
        verify(redirectAttributes).addFlashAttribute("info", "reset.code.sent");
    }

    @Test
    void handleResetRequest_EmailNotFound_ShouldAddErrorAndRedirectBack() {
        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        String view = controller.handleResetRequest(email, model, redirectAttributes);

        assertEquals("redirect:/reset", view);
        verify(redirectAttributes).addFlashAttribute("error", "reset.email.notfound");
    }

    @Test
    void showCodeInput_ShouldAddEmailToModelAndReturnView() {
        String view = controller.showCodeInput(email, model);

        verify(model).addAttribute("email", email);
        assertEquals("auth/verify-code", view);
    }


    @Test
    void showNewPasswordForm_ShouldAddAttributesAndReturnView() {
        String view = controller.showNewPasswordForm(email, model);

        verify(model).addAttribute("email", email);
        verify(model).addAttribute(eq("passwordResetDto"), any(PasswordResetDto.class));
        assertEquals("auth/new-password", view);
    }

    @Test
    void setNewPassword_WithValidationErrors_ShouldReturnFormView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        PasswordResetDto dto = new PasswordResetDto();
        String view = controller.setNewPassword(email, dto, bindingResult, model, redirectAttributes);

        verify(model).addAttribute("email", email);
        assertEquals("auth/new-password", view);
        verify(clientRepository, never()).save(any());
    }
    @Test
    void setNewPassword_EmailNotFound_ShouldAddErrorAndRedirectBack() {
        PasswordResetDto dto = new PasswordResetDto();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        String view = controller.setNewPassword(email, dto, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/reset", view);
        verify(redirectAttributes).addFlashAttribute("error", "reset.email.notfound");
    }
}