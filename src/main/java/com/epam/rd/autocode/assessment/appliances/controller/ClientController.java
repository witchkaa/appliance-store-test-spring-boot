package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       Model model) {
        List<Client> clients = clientService.search(id, name);
        model.addAttribute("clients", clients);
        model.addAttribute("searchId", id);
        model.addAttribute("searchName", name);
        return "client/clients";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("client", new Client());
        return "client/newClient";
    }

    @PostMapping("/add-client")
    public String save(@ModelAttribute @Valid Client client,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "client/newClient";
        }
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientService.delete(id); // выбросит ClientNotFoundException, если не найден
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        model.addAttribute("client", client);
        return "client/editClient";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute @Valid Client client,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "client/editClient";
        }

        Client existing = clientService.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (client.getPassword() == null || client.getPassword().isBlank()) {
            client.setPassword(existing.getPassword()); // сохраняем старый, если не ввели
        }
        client.setId(id);
        clientService.save(client);
        return "redirect:/clients";
    }
}