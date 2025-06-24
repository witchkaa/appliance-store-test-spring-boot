package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    @GetMapping
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("appliances", applianceService.getAll(pageable));
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("appliance", new Appliance());
        model.addAttribute("manufacturers", manufacturerService.getAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String save(@ModelAttribute @Valid Appliance appliance, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("manufacturers", manufacturerService.getAll());
            model.addAttribute("categories", Category.values());
            model.addAttribute("powerTypes", PowerType.values());
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
}