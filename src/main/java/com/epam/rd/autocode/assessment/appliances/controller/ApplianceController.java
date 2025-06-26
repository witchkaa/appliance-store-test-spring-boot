package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    @GetMapping
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        log.info("Listing appliances with pageable: {}", pageable);
        model.addAttribute("appliances", applianceService.getAll(pageable));
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