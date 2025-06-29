package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Role;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@RequiredArgsConstructor
@Controller
public class LoginController {
    private final ClientService clientService;
    private final MessageSource messageSource;
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("client", new Client());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("client") @Valid Client client,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           Locale locale) {
        if (clientService.emailExists(client.getEmail())) {
            result.rejectValue("email", "register.error.email.exists");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        client.setRole(Role.CLIENT);
        clientService.save(client);

        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("register.success", null, locale));
        return "redirect:/login";
    }
}