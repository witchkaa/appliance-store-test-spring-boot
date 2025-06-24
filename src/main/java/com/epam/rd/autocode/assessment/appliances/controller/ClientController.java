package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String list(Model model) {
        log.info("Request to list all clients");
        model.addAttribute("clients", clientService.getAll());
        return "client/clients"; // шаблон clients.html
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening client creation form");
        model.addAttribute("client", new Client());
        return "client/newClient"; // шаблон newClient.html
    }

    @PostMapping("/add-client")
    public String save(@ModelAttribute @Valid Client client, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Validation errors while adding client: {}", result.getAllErrors());
            return "client/newClient";
        }
        log.info("Saving client: {}", client);
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("Deleting client with id: {}", id);
        clientService.delete(id);
        return "redirect:/clients";
    }
}