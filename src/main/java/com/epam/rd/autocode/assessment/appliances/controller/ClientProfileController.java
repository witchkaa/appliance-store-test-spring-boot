package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientService clientService;

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
}