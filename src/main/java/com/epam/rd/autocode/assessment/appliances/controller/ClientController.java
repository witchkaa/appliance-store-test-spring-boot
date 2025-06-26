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
        return "client/clients";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening client creation form");
        model.addAttribute("client", new Client());
        return "client/newClient";
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
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("Opening edit form for client with id: {}", id);
        Client client = clientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID: " + id));
        model.addAttribute("client", client);
        return "client/editClient";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute @Valid Client client, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Validation errors while editing client: {}", result.getAllErrors());
            return "client/editClient";
        }
        client.setId(id);
        Client existing = clientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID"));

        if (client.getPassword() == null || client.getPassword().isBlank()) {
            client.setPassword(existing.getPassword());
        }
        clientService.save(client);
        return "redirect:/clients";
    }
}