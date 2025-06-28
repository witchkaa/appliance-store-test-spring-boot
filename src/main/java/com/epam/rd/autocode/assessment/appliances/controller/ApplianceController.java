package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.exception.ApplianceNotFoundException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    private static final int PAGE_SIZE = 8;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String manufacturer,
                       @PageableDefault(size = PAGE_SIZE) Pageable pageable,
                       Model model) {

        Page<Appliance> appliances = applianceService.searchAppliances(id, name, manufacturer, pageable);
        model.addAttribute("appliances", appliances);
        model.addAttribute("paramId", id);
        model.addAttribute("paramName", name);
        model.addAttribute("paramManufacturer", manufacturer);
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("appliance", new Appliance());
        populateFormModel(model);
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String save(@ModelAttribute @Valid Appliance appliance,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "appliance/newAppliance";
        }
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        applianceService.delete(id);
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Appliance appliance = applianceService.findById(id)
                .orElseThrow(() -> new ApplianceNotFoundException(id));
        model.addAttribute("appliance", appliance);
        populateFormModel(model);
        return "appliance/editAppliance";
    }

    @PostMapping("/{id}/edit")
    public String updateAppliance(@PathVariable Long id,
                                  @ModelAttribute @Valid Appliance appliance,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "appliance/editAppliance";
        }
        appliance.setId(id);
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    private void populateFormModel(Model model) {
        model.addAttribute("manufacturers", manufacturerService.getAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
    }
}