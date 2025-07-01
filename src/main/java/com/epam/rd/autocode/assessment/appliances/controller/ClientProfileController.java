package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.PasswordChangeRequest;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientService clientService;
    private final CustomUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/profile")
    public String profile(Model model) {
        Client client = clientService.getCurrentClient();
        List<Orders> orders = clientService.getOrdersForCurrentClient();

        model.addAttribute("client", client);
        model.addAttribute("orders", orders);
        model.addAttribute("newBalance", client.getBalance());

        return "profile/profile";
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/profile/balance")
    public String updateBalance(@RequestParam BigDecimal newBalance) {
        clientService.topUpBalance(newBalance);
        return "redirect:/profile";
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute PasswordChangeRequest request,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
//

        try {
            clientService.changePassword(request.getOldPassword(), request.getNewPassword());

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            String email = currentAuth.getName();
            UserDetails updatedUser = userDetailsService.loadUserByUsername(email);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser, updatedUser.getPassword(), updatedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("success", "profile.password.changed");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/profile/change-email")
    public String changeEmail(@RequestParam String currentEmail,
                              @RequestParam String newEmail,
                              RedirectAttributes redirectAttributes) {
        try {
            clientService.changeEmail(currentEmail, newEmail);

            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = currentAuth.getAuthorities();
            Authentication newAuth = new UsernamePasswordAuthenticationToken(newEmail, currentAuth.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("success", "profile.email.changed");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}