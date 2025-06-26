package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.service.impl.ApplianceServiceImpl;
import org.junit.jupiter.api.Test;
import com.epam.rd.autocode.assessment.appliances.repository.*;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ClientService clientService;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private OrdersController ordersController;

    @Mock
    private Model model;

    @Test
    void list_shouldAddOrdersAndPageToModel() {
        Pageable pageable = PageRequest.of(0, 5);
        Orders order = new Orders();
        Page<Orders> ordersPage = new PageImpl<>(List.of(order));

        Mockito.when(orderService.getAllPageable(pageable)).thenReturn(ordersPage);

        String view = ordersController.list(pageable, model);

        Mockito.verify(model).addAttribute("orders", ordersPage.getContent());
        Mockito.verify(model).addAttribute("page", ordersPage);
        Assertions.assertEquals("order/orders", view);
    }

    @Test
    void createForm_shouldAddOrderAndAppliances() {
        List<Appliance> appliances = List.of(new Appliance());
        Mockito.when(orderService.getAvailableAppliances()).thenReturn(appliances);

        String view = ordersController.createForm(model);

        Mockito.verify(model).addAttribute(eq("order"), any(Orders.class));
        Mockito.verify(model).addAttribute("appliances", appliances);
        Assertions.assertEquals("order/newOrder", view);
    }

    @Test
    void saveOrder_whenNoAppliancesSelected_shouldReturnFormWithError() {
        String view = ordersController.saveOrder(Collections.emptyList(), List.of(1), model);

        Mockito.verify(model).addAttribute(eq("error"), anyString());
        Mockito.verify(model).addAttribute(eq("appliances"), anyList());
        Assertions.assertEquals("order/newOrder", view);
    }

    @Test
    void saveOrder_whenMismatchApplianceAndQuantity_shouldReturnFormWithError() {
        String view = ordersController.saveOrder(List.of(1L, 2L), List.of(1), model);

        Mockito.verify(model).addAttribute(eq("error"), anyString());
        Mockito.verify(model).addAttribute(eq("appliances"), anyList());
        Assertions.assertEquals("order/newOrder", view);
    }

    @Test
    void saveOrder_whenValidInput_shouldRedirect() {
        List<Long> applianceIds = List.of(1L, 2L);
        List<Integer> quantities = List.of(3, 4);

        String view = ordersController.saveOrder(applianceIds, quantities, model);

        Mockito.verify(orderService).createOrderWithItems(applianceIds, quantities);
        Assertions.assertEquals("redirect:/orders", view);
    }

    @Test
    void editForm_shouldAddOrderAndRows() {
        Long id = 1L;
        Orders order = new Orders();
        List<OrderRow> rows = List.of(new OrderRow());

        Mockito.when(orderService.getById(id)).thenReturn(order);
        Mockito.when(orderService.getOrderRows(id)).thenReturn(rows);

        String view = ordersController.editForm(id, model);

        Mockito.verify(model).addAttribute("order", order);
        Mockito.verify(model).addAttribute("rows", rows);
        Assertions.assertEquals("order/editOrder", view);
    }

    @Test
    void delete_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = ordersController.delete(id);

        Mockito.verify(orderService).delete(id);
        Assertions.assertEquals("redirect:/orders", view);
    }

    @Test
    void approve_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = ordersController.approve(id);

        Mockito.verify(orderService).approve(id);
        Assertions.assertEquals("redirect:/orders", view);
    }

    @Test
    void unapprove_shouldCallServiceAndRedirect() {
        Long id = 1L;

        String view = ordersController.unapprove(id);

        Mockito.verify(orderService).unapprove(id);
        Assertions.assertEquals("redirect:/orders", view);
    }

    @Test
    void choiceAppliance_shouldAddOrdersIdAndAppliances() {
        Long ordersId = 5L;
        List<Appliance> appliances = List.of(new Appliance());

        Mockito.when(orderService.getAvailableAppliances()).thenReturn(appliances);

        String view = ordersController.choiceAppliance(ordersId, model);

        Mockito.verify(model).addAttribute("ordersId", ordersId);
        Mockito.verify(model).addAttribute("appliances", appliances);
        Assertions.assertEquals("order/choiceAppliance", view);
    }

    @Test
    void addIntoOrder_shouldCallServiceAndRedirect() {
        Long ordersId = 2L;
        Long applianceId = 3L;
        int numbers = 5;
        BigDecimal price = BigDecimal.valueOf(100);

        String view = ordersController.addIntoOrder(ordersId, applianceId, numbers, price);

        Mockito.verify(orderService).addApplianceToOrder(ordersId, applianceId, numbers, price);
        Assertions.assertEquals("redirect:/orders/edit/" + ordersId, view);
    }
}
