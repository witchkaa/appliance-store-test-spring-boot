package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;


@Slf4j
@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;
    private final ModelMapper modelMapper;

    private static final int PAGE_SIZE = 8;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String manufacturer,
                       @PageableDefault(size = PAGE_SIZE) Pageable pageable,
                       Model model) {

        Page<Appliance> appliances = applianceService.searchAppliances(id, name, manufacturer, pageable);

        Page<ApplianceResponseDto> dtos = appliances.map(appliance -> modelMapper.map(appliance, ApplianceResponseDto.class));

        model.addAttribute("appliances", dtos);
        model.addAttribute("paramId", id);
        model.addAttribute("paramName", name);
        model.addAttribute("paramManufacturer", manufacturer);
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("appliance", new ApplianceRequestDto());
        model.addAttribute("types", ProductType.values());
        populateFormModel(model);
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String save(@ModelAttribute("appliance") @Valid ApplianceRequestDto dto,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "appliance/newAppliance";
        }

        Appliance appliance = convertToEntity(dto);
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            applianceService.delete(id);
        } catch (Exception e) {
            return "redirect:/appliances";
        }
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Appliance appliance = applianceService.findById(id)
                .orElseThrow(() -> new ApplianceNotFoundException(id));
        ApplianceRequestDto dto = convertToDto(appliance);
        model.addAttribute("appliance", dto);
        model.addAttribute("types", ProductType.values());
        populateFormModel(model);
        return "appliance/editAppliance";
    }

    @PostMapping("/{id}/edit")
    public String updateAppliance(@PathVariable Long id,
                                  @ModelAttribute("appliance") @Valid ApplianceRequestDto dto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "appliance/editAppliance";
        }

        Appliance appliance = convertToEntity(dto);
        appliance.setId(id);
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    private void populateFormModel(Model model) {
        model.addAttribute("manufacturers", manufacturerService.getAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
    }


    private Appliance convertToEntity(ApplianceRequestDto dto) {
        Appliance appliance = modelMapper.map(dto, Appliance.class);

        Manufacturer manufacturer = manufacturerService.findById(dto.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        appliance.setManufacturer(manufacturer);

        return appliance;
    }

    private ApplianceRequestDto convertToDto(Appliance appliance) {
        ApplianceRequestDto dto = modelMapper.map(appliance, ApplianceRequestDto.class);
        if (appliance.getManufacturer() != null) {
            dto.setManufacturerId(appliance.getManufacturer().getId());
        }
        return dto;
    }
}