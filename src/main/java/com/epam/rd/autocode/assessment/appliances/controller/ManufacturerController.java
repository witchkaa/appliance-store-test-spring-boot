package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerDeleteException;
import com.epam.rd.autocode.assessment.appliances.exception.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService manufacturerService;
    private final ModelMapper modelMapper;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       Model model) {
        List<Manufacturer> manufacturers = manufacturerService.search(id, name);
        List<ManufacturerResponseDto> dtos = manufacturers.stream()
                .map(m -> modelMapper.map(m, ManufacturerResponseDto.class))
                .toList();
        model.addAttribute("manufacturers", dtos);
        model.addAttribute("paramId", id);
        model.addAttribute("paramName", name);
        return "manufacture/manufacturers";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("manufacturer", new ManufacturerRequestDto());
        return "manufacture/newManufacturer";
    }

    @PostMapping("/add-manufacturer")
    public String save(@ModelAttribute("manufacturer") @Valid ManufacturerRequestDto dto,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "manufacture/newManufacturer";
        }
        Manufacturer manufacturer = modelMapper.map(dto, Manufacturer.class);
        manufacturerService.save(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            manufacturerService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Manufacturer deleted successfully.");
        } catch (ManufacturerDeleteException | ManufacturerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manufacturers";
    }
}