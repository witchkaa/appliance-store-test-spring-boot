package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService ordersService;
    private final ClientService clientService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", ordersService.getAll());
        return "order/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("order", new Orders());
        model.addAttribute("clients", clientService.getAll());
        model.addAttribute("employees", employeeService.getAll());
        return "order/form";
    }

    @PostMapping
    public String save(@ModelAttribute @Valid Orders order, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            return "order/form";
        }
        ordersService.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        ordersService.approve(id);
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        ordersService.delete(id);
        return "redirect:/orders";
    }
}