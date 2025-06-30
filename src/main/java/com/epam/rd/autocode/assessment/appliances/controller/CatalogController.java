package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final ApplianceService applianceService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public String catalog(@RequestParam(required = false) ProductType type,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false, defaultValue = "price") String sort,
                          @RequestParam(required = false, defaultValue = "asc") String dir,
                          @PageableDefault(size = 3) Pageable pageable,
                          Model model) {

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sort));

        Page<Appliance> appliancesPage;

        if ((name != null && !name.isBlank()) || type != null) {
            appliancesPage = applianceService.searchAppliances(type, name, null, sortedPageable);
        } else {
            appliancesPage = applianceService.getAll(sortedPageable);
        }

        model.addAttribute("appliances", appliancesPage.getContent());
        model.addAttribute("page", appliancesPage);
        model.addAttribute("types", ProductType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("searchName", name);

        return "catalog/catalog";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/appliances/{id}")
    public String viewAppliance(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<Appliance> applianceOptional = applianceService.findById(id);
        Appliance appliance = applianceOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("appliance", appliance);
        return "catalog/appliance-details";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/load")
    public String loadMore(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) ProductType type,
                           @RequestParam(defaultValue = "price") String sort,
                           @RequestParam(defaultValue = "asc") String dir,
                           Model model) {

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable sortedPageable = PageRequest.of(page, 4, Sort.by(direction, sort));

        Page<Appliance> appliancesPage = (type == null)
                ? applianceService.getAll(sortedPageable)
                : applianceService.getByType(type, sortedPageable);

        model.addAttribute("appliances", appliancesPage.getContent());
        model.addAttribute("isLastPage", appliancesPage.isLast());

        return "catalog/fragments :: applianceCards";
    }
}