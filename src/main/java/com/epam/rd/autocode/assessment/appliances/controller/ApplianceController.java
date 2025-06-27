package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    private static final int PAGE_SIZE = 8;

    @GetMapping
    public String list(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String manufacturer,
            @PageableDefault(size = PAGE_SIZE) Pageable pageable,
            Model model) {

        log.info("Searching appliances by: id={}, name={}, manufacturer={}", id, name, manufacturer);
        Page<Appliance> appliances = applianceService.searchAppliances(id, name, manufacturer, pageable);
        model.addAttribute("appliances", appliances);
        model.addAttribute("paramId", id);
        model.addAttribute("paramName", name);
        model.addAttribute("paramManufacturer", manufacturer);
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening form to add new appliance");
        model.addAttribute("appliance", new Appliance());
        model.addAttribute("manufacturers", manufacturerService.getAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String save(@ModelAttribute @Valid Appliance appliance, BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Validation errors while saving appliance: {}", result.getAllErrors());
            model.addAttribute("manufacturers", manufacturerService.getAll());
            model.addAttribute("categories", Category.values());
            model.addAttribute("powerTypes", PowerType.values());
            return "appliance/newAppliance";
        }
        log.info("Saving new appliance: {}", appliance);
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        log.info("Deleting appliance with id: {}", id);
        applianceService.delete(id);
        return "redirect:/appliances";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("Opening form to edit appliance with id: {}", id);
        Appliance appliance = applianceService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appliance not found"));
        model.addAttribute("appliance", appliance);
        model.addAttribute("manufacturers", manufacturerService.getAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        return "appliance/editAppliance";
    }

    @PostMapping("/{id}/edit")
    public String updateAppliance(@PathVariable Long id,
                                  @ModelAttribute @Valid Appliance appliance,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            log.warn("Validation errors while editing appliance: {}", result.getAllErrors());
            model.addAttribute("manufacturers", manufacturerService.getAll());
            model.addAttribute("categories", Category.values());
            model.addAttribute("powerTypes", PowerType.values());
            return "appliance/editAppliance";
        }
        appliance.setId(id);
        applianceService.save(appliance);
        return "redirect:/appliances";
    }
}