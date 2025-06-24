package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("manufacturers", manufacturerService.getAll());
        return "manufacture/manufacturers";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());
        return "manufacture/newManufacturer";
    }

    @PostMapping("/add-manufacturer")
    public String save(@ModelAttribute @Valid Manufacturer manufacturer, BindingResult result) {
        if (result.hasErrors()) return "manufacture/newManufacturer";
        manufacturerService.save(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        manufacturerService.delete(id);
        return "redirect:/manufacturers";
    }
}