package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.PasswordResetDto;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reset")
public class PasswordResetController {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> pendingResetCodes = new ConcurrentHashMap<>();


    @GetMapping
    public String showResetRequestForm() {
        return "auth/request-reset";
    }

    @PostMapping
    public String handleResetRequest(@RequestParam String email, Model model, RedirectAttributes redirectAttributes) {
        Optional<Client> optional = clientRepository.findByEmail(email);

        if (optional.isPresent()) {
            pendingResetCodes.put(email, "1111"); // Заглушка 1111
            redirectAttributes.addFlashAttribute("info", "reset.code.sent");
            return "redirect:/reset/verify?email=" + email;
        }

        redirectAttributes.addFlashAttribute("error", "reset.email.notfound");
        return "redirect:/reset";
    }

    @GetMapping("/verify")
    public String showCodeInput(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-code";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String email,
                             @RequestParam String code,
                             RedirectAttributes redirectAttributes) {

        String expectedCode = pendingResetCodes.get(email);
        if ("1111".equals(code) && expectedCode != null) {
            return "redirect:/reset/new-password?email=" + email;
        }

        redirectAttributes.addFlashAttribute("error", "reset.code.invalid");
        return "redirect:/reset/verify?email=" + email;
    }

    @GetMapping("/new-password")
    public String showNewPasswordForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("passwordResetDto", new PasswordResetDto());
        return "auth/new-password";
    }

    @PostMapping("/new-password")
    public String setNewPassword(@RequestParam String email,
                                 @Valid PasswordResetDto passwordResetDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("email", email);
            return "auth/new-password";
        }

        Optional<Client> optional = clientRepository.findByEmail(email);
        if (optional.isPresent()) {
            Client client = optional.get();
            client.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
            clientRepository.save(client);
            pendingResetCodes.remove(email);
            redirectAttributes.addFlashAttribute("message", "reset.success");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("error", "reset.email.notfound");
        return "redirect:/reset";
    }
}