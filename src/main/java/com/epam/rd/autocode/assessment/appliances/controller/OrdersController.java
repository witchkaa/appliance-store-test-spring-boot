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

import java.math.BigDecimal;

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
        return "order/orders";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("order", new Orders());
        model.addAttribute("clients", clientService.getAll());
        model.addAttribute("employees", employeeService.getAll());
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    public String save(@ModelAttribute @Valid Orders order, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.getAll());
            model.addAttribute("employees", employeeService.getAll());
            return "order/newOrder";
        }
        ordersService.save(order);
        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Orders order = ordersService.getById(id);
        model.addAttribute("order", order);
        model.addAttribute("rows", ordersService.getOrderRows(id)); // предполагаем, что метод возвращает детали
        return "order/editOrder";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        ordersService.delete(id);
        return "redirect:/orders";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        ordersService.approve(id);
        return "redirect:/orders";
    }

    @GetMapping("/unapproved/{id}")
    public String unapprove(@PathVariable Long id) {
        ordersService.unapprove(id);
        return "redirect:/orders";
    }

    @GetMapping("/choice-appliance/{ordersId}")
    public String choiceAppliance(@PathVariable Long ordersId, Model model) {
        model.addAttribute("ordersId", ordersId);
        model.addAttribute("appliances", ordersService.getAvailableAppliances());
        return "order/choiceAppliance";
    }

    @PostMapping("/add-into-order")
    public String addIntoOrder(@RequestParam Long ordersId,
                               @RequestParam Long applianceId,
                               @RequestParam int numbers,
                               @RequestParam BigDecimal price) {
        ordersService.addApplianceToOrder(ordersId, applianceId, numbers, price);
        return "redirect:/orders/edit/" + ordersId;
    }
}