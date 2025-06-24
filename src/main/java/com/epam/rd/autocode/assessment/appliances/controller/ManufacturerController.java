package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping
    public String list(Model model) {
        log.info("Getting list of all manufacturers");
        model.addAttribute("manufacturers", manufacturerService.getAll());
        return "manufacture/manufacturers";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening manufacturer creation form");
        model.addAttribute("manufacturer", new Manufacturer());
        return "manufacture/newManufacturer";
    }

    @PostMapping("/add-manufacturer")
    public String save(@ModelAttribute @Valid Manufacturer manufacturer, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Validation errors while saving manufacturer: {}", result.getAllErrors());
            return "manufacture/newManufacturer";
        }
        log.info("Saving manufacturer: {}", manufacturer);
        manufacturerService.save(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("Deleting manufacturer with id {}", id);
        manufacturerService.delete(id);
        return "redirect:/manufacturers";
    }
}