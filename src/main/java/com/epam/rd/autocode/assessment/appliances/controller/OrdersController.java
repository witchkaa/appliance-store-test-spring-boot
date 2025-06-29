package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService ordersService;
    private final ModelMapper modelMapper;

    @GetMapping
    public String list(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String client,
                       @PageableDefault(size = 5, sort = "id") Pageable pageable,
                       Model model) {

        log.info("Fetching orders: id={}, client={}", id, client);
        Page<Orders> ordersPage;

        if (ordersService.isEmployee()) {
            if (id != null || (client != null && !client.isBlank())) {
                ordersPage = ordersService.searchOrders(id, client, pageable);
            } else {
                ordersPage = ordersService.getAllPageable(pageable);
            }
        } else {
            ordersPage = ordersService.getAllPageable(pageable);
        }

        Page<OrdersDto> dtoPage = ordersPage.map(order -> modelMapper.map(order, OrdersDto.class));

        model.addAttribute("orders", dtoPage);
        return "order/orders";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        log.info("Opening order creation form");
        // Передаём пустой DTO для формы
        model.addAttribute("order", new OrdersDto());
        model.addAttribute("appliances", ordersService.getAvailableAppliances());
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    public String saveOrder(@RequestParam("applianceIds") List<Long> applianceIds,
                            @RequestParam("quantities") List<Integer> quantities,
                            Model model) {

        if (applianceIds == null || applianceIds.isEmpty()) {
            model.addAttribute("error", "You must select at least one appliance.");
            model.addAttribute("appliances", ordersService.getAvailableAppliances());
            return "order/newOrder";
        }

        if (applianceIds.size() != quantities.size()) {
            model.addAttribute("error", "Mismatch between appliances and quantities.");
            model.addAttribute("appliances", ordersService.getAvailableAppliances());
            return "order/newOrder";
        }

        try {
            ordersService.createOrderWithItems(applianceIds, quantities);
        } catch (Exception e) {
            log.error("Failed to create order", e);
            model.addAttribute("error", "Failed to create order: " + e.getMessage());
            model.addAttribute("appliances", ordersService.getAvailableAppliances());
            return "order/newOrder";
        }

        return "redirect:/orders";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Orders order = ordersService.getById(id);
            OrdersDto orderDto = modelMapper.map(order, OrdersDto.class);

            List<OrderRow> rows = ordersService.getOrderRows(id);
            List<OrderRowDto> rowDtos = rows.stream()
                    .map(row -> modelMapper.map(row, OrderRowDto.class))
                    .toList();

            model.addAttribute("order", orderDto);
            model.addAttribute("rows", rowDtos);
            return "order/editOrder";
        } catch (Exception e) {
            log.error("Failed to open edit form for order {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to open order: " + e.getMessage());
            return "redirect:/orders";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ordersService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting order {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error deleting order: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ordersService.approve(id);
            redirectAttributes.addFlashAttribute("success", "Order approved.");
        } catch (Exception e) {
            log.error("Error approving order {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error approving order: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/unapproved/{id}")
    public String unapprove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ordersService.unapprove(id);
            redirectAttributes.addFlashAttribute("success", "Order unapproved.");
        } catch (Exception e) {
            log.error("Error unapproving order {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error unapproving order: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @GetMapping("/choice-appliance/{ordersId}")
    public String choiceAppliance(@PathVariable Long ordersId, Model model) {
        log.info("Choosing appliance for order {}", ordersId);
        model.addAttribute("ordersId", ordersId);
        model.addAttribute("appliances", ordersService.getAvailableAppliances());
        return "order/choiceAppliance";
    }

    @PostMapping("/add-into-order")
    public String addIntoOrder(@RequestParam Long ordersId,
                               @RequestParam Long applianceId,
                               @RequestParam int numbers,
                               @RequestParam BigDecimal price,
                               RedirectAttributes redirectAttributes) {
        try {
            log.info("Adding appliance {} into order {}", applianceId, ordersId);
            ordersService.addApplianceToOrder(ordersId, applianceId, numbers, price);
        } catch (Exception e) {
            log.error("Failed to add appliance to order", e);
            redirectAttributes.addFlashAttribute("error", "Failed to add appliance: " + e.getMessage());
        }
        return "redirect:/orders/edit/" + ordersId;
    }

    @GetMapping("/details/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Orders order = ordersService.getById(id);
        List<OrderRow> rows = ordersService.getOrderRows(id);

        OrdersDto orderDto = modelMapper.map(order, OrdersDto.class);
        List<OrderRowDto> rowDtos = rows.stream()
                .map(row -> modelMapper.map(row, OrderRowDto.class))
                .toList();

        model.addAttribute("order", orderDto);
        model.addAttribute("rows", rowDtos);
        model.addAttribute("amount", order.getAmount());
        return "order/orderDetails :: details";
    }

    @GetMapping("/delete-row/{rowId}")
    public String deleteOrderRow(@PathVariable Long rowId, RedirectAttributes redirectAttributes) {
        try {
            Long orderId = ordersService.deleteOrderRowAndUpdateState(rowId);
            redirectAttributes.addFlashAttribute("success", "Item removed, stock updated, and amount refunded.");
            return "redirect:/orders/edit/" + orderId;
        } catch (Exception e) {
            log.error("Failed to delete order row {}", rowId, e);
            redirectAttributes.addFlashAttribute("error", "Error deleting item: " + e.getMessage());
            return "redirect:/orders";
        }
    }
}
