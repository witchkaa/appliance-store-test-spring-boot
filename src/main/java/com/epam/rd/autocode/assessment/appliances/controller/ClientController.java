package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestCreateDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestEditDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       Model model) {
        List<Client> clients = clientService.search(id, name);

        List<ClientResponseDto> dtos = clients.stream()
                .map(client -> modelMapper.map(client, ClientResponseDto.class))
                .toList();

        model.addAttribute("clients", dtos);
        model.addAttribute("searchId", id);
        model.addAttribute("searchName", name);
        return "client/clients";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("client", new ClientRequestCreateDto());
        return "client/newClient";
    }

    @PostMapping("/add-client")
    public String save(@ModelAttribute("client") @Valid ClientRequestCreateDto dto,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "client/newClient";
        }

        Client client = modelMapper.map(dto, Client.class);
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientService.delete(id);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        ClientRequestEditDto dto = modelMapper.map(client, ClientRequestEditDto.class);
        model.addAttribute("client", dto);
        return "client/editClient";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("client") @Valid ClientRequestEditDto dto,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "client/editClient";
        }

        Client existing = clientService.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (!passwordEncoder.matches(dto.getOldPassword(), existing.getPassword())) {
            result.rejectValue("oldPassword", "validation.oldPassword.invalid", "Wrong old password");
            return "client/editClient";
        }

        String newPassword = dto.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 8 ||
                    !newPassword.matches(".*\\d.*") ||
                    !newPassword.matches(".*[A-Z].*") ||
                    !newPassword.matches(".*[@#$%^&+=!].*")) {

                result.rejectValue("password", "validation.password.invalid",
                        "Password must be at least 8 characters, include a digit, an uppercase letter, and a special symbol.");
                return "client/editClient";
            }
        }

        String encodedPassword = (newPassword == null || newPassword.isBlank())
                ? existing.getPassword()
                : passwordEncoder.encode(newPassword);

        Client updated = modelMapper.map(dto, Client.class);
        updated.setId(id);
        updated.setPassword(encodedPassword);

        clientService.save(updated);
        return "redirect:/clients";
    }
}