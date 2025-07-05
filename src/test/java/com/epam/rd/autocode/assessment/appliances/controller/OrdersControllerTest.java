package com.epam.rd.autocode.assessment.appliances.controller;


import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock
    private OrderService ordersService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private OrdersController controller;

    @Test
    void list_AsEmployeeWithFilters_ShouldCallSearchAndReturnView() {
        Long id = 1L;
        String client = "John";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));

        Orders order = new Orders();
        OrdersDto dto = new OrdersDto();

        when(ordersService.isEmployee()).thenReturn(true);
        when(ordersService.searchOrders(id, client, pageable)).thenReturn(new PageImpl<>(List.of(order)));
        when(modelMapper.map(order, OrdersDto.class)).thenReturn(dto);

        String view = controller.list(id, client, pageable, model);

        verify(ordersService).searchOrders(id, client, pageable);
        verify(modelMapper).map(order, OrdersDto.class);
        verify(model).addAttribute("orders", new PageImpl<>(List.of(dto)));
        assertEquals("order/orders", view);
    }

    @Test
    void list_AsNonEmployee_ShouldCallGetAllPageableAndReturnView() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));

        Orders order = new Orders();
        OrdersDto dto = new OrdersDto();

        when(ordersService.isEmployee()).thenReturn(false);
        when(ordersService.getAllPageable(pageable)).thenReturn(new PageImpl<>(List.of(order)));
        when(modelMapper.map(order, OrdersDto.class)).thenReturn(dto);

        String view = controller.list(null, null, pageable, model);

        verify(ordersService).getAllPageable(pageable);
        verify(modelMapper).map(order, OrdersDto.class);
        verify(model).addAttribute("orders", new PageImpl<>(List.of(dto)));
        assertEquals("order/orders", view);
    }

    @Test
    void createForm_ShouldAddAttributesAndReturnView() {
        List<Appliance> appliances = List.of(new Appliance());

        when(ordersService.getAvailableAppliances()).thenReturn(appliances);

        String view = controller.createForm(model);

        verify(model).addAttribute(eq("order"), any(OrdersDto.class));
        verify(model).addAttribute("appliances", appliances);
        assertEquals("order/newOrder", view);
    }

    @Test
    void saveOrder_ValidData_ShouldCallServiceAndRedirect() {
        List<Long> applianceIds = List.of(1L, 2L);
        List<Integer> quantities = List.of(1, 3);

        String view = controller.saveOrder(applianceIds, quantities, model);

        verify(ordersService).createOrderWithItems(applianceIds, quantities);
        assertEquals("redirect:/orders", view);
    }

    @Test
    void saveOrder_ServiceThrowsException_ShouldReturnFormWithError() {
        List<Long> applianceIds = List.of(1L);
        List<Integer> quantities = List.of(1);

        when(ordersService.getAvailableAppliances()).thenReturn(List.of(new Appliance()));
        doThrow(new RuntimeException("DB error")).when(ordersService).createOrderWithItems(applianceIds, quantities);

        String view = controller.saveOrder(applianceIds, quantities, model);

        verify(model).addAttribute(startsWith("error"), contains("Failed to create order"));
        verify(model).addAttribute(eq("appliances"), any());
        assertEquals("order/newOrder", view);
    }

    @Test
    void editForm_ValidId_ShouldPopulateModelAndReturnView() {
        Long id = 5L;
        Orders order = new Orders();
        OrdersDto orderDto = new OrdersDto();
        OrderRow row = new OrderRow();
        OrderRowDto rowDto = new OrderRowDto();

        when(ordersService.getById(id)).thenReturn(order);
        when(modelMapper.map(order, OrdersDto.class)).thenReturn(orderDto);
        when(ordersService.getOrderRows(id)).thenReturn(List.of(row));
        when(modelMapper.map(row, OrderRowDto.class)).thenReturn(rowDto);

        String view = controller.editForm(id, model, redirectAttributes);

        verify(model).addAttribute("order", orderDto);
        verify(model).addAttribute("rows", List.of(rowDto));
        assertEquals("order/editOrder", view);
    }

    @Test
    void editForm_ServiceThrowsException_ShouldRedirectWithError() {
        Long id = 10L;
        RuntimeException ex = new RuntimeException("Not found");
        doThrow(ex).when(ordersService).getById(id);

        String view = controller.editForm(id, model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "Failed to open order: " + ex.getMessage());
        assertEquals("redirect:/orders", view);
    }

    @Test
    void approve_Success_ShouldRedirectWithSuccessMessage() {
        Long id = 15L;

        String view = controller.approve(id, redirectAttributes);

        verify(ordersService).approve(id);
        verify(redirectAttributes).addFlashAttribute("success", "Order approved.");
        assertEquals("redirect:/orders", view);
    }

    @Test
    void approve_ServiceThrowsException_ShouldRedirectWithError() {
        Long id = 15L;
        RuntimeException ex = new RuntimeException("Error");
        doThrow(ex).when(ordersService).approve(id);

        String view = controller.approve(id, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "Error approving order: " + ex.getMessage());
        assertEquals("redirect:/orders", view);
    }

    @Test
    void unapprove_Success_ShouldRedirectWithSuccessMessage() {
        Long id = 18L;

        String view = controller.unapprove(id, redirectAttributes);

        verify(ordersService).unapprove(id);
        verify(redirectAttributes).addFlashAttribute("success", "Order unapproved.");
        assertEquals("redirect:/orders", view);
    }

    @Test
    void unapprove_ServiceThrowsException_ShouldRedirectWithError() {
        Long id = 18L;
        RuntimeException ex = new RuntimeException("Error");
        doThrow(ex).when(ordersService).unapprove(id);

        String view = controller.unapprove(id, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "Error unapproving order: " + ex.getMessage());
        assertEquals("redirect:/orders", view);
    }

    @Test
    void choiceAppliance_ShouldAddAttributesAndReturnView() {
        Long orderId = 7L;
        List<Appliance> appliances = List.of(new Appliance());

        when(ordersService.getAvailableAppliances()).thenReturn(appliances);

        String view = controller.choiceAppliance(orderId, model);

        verify(model).addAttribute("ordersId", orderId);
        verify(model).addAttribute("appliances", appliances);
        assertEquals("order/choiceAppliance", view);
    }

    @Test
    void addIntoOrder_Success_ShouldRedirect() {
        Long ordersId = 10L;
        Long applianceId = 5L;
        int numbers = 3;
        BigDecimal price = BigDecimal.valueOf(100);

        String view = controller.addIntoOrder(ordersId, applianceId, numbers, price, redirectAttributes);

        verify(ordersService).addApplianceToOrder(ordersId, applianceId, numbers, price);
        assertEquals("redirect:/orders/edit/" + ordersId, view);
    }

    @Test
    void addIntoOrder_ServiceThrowsException_ShouldRedirectWithError() {
        Long ordersId = 10L;
        Long applianceId = 5L;
        int numbers = 3;
        BigDecimal price = BigDecimal.valueOf(100);

        doThrow(new RuntimeException("Add failed")).when(ordersService)
                .addApplianceToOrder(ordersId, applianceId, numbers, price);

        String view = controller.addIntoOrder(ordersId, applianceId, numbers, price, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "Failed to add appliance: Add failed");
        assertEquals("redirect:/orders/edit/" + ordersId, view);
    }

    @Test
    void orderDetails_ShouldAddAttributesAndReturnFragment() {
        Long id = 12L;
        Orders order = new Orders();
        order.setAmount(BigDecimal.valueOf(500));
        List<OrderRow> rows = List.of(new OrderRow());
        OrdersDto orderDto = new OrdersDto();
        OrderRowDto rowDto = new OrderRowDto();

        when(ordersService.getById(id)).thenReturn(order);
        when(ordersService.getOrderRows(id)).thenReturn(rows);
        when(modelMapper.map(order, OrdersDto.class)).thenReturn(orderDto);
        when(modelMapper.map(any(OrderRow.class), eq(OrderRowDto.class))).thenReturn(rowDto);

        String view = controller.orderDetails(id, model);

        verify(model).addAttribute("order", orderDto);
        verify(model).addAttribute("rows", List.of(rowDto));
        verify(model).addAttribute("amount", order.getAmount());
        assertEquals("order/orderDetails :: details", view);
    }

    @Test
    void deleteOrderRow_Success_ShouldRedirectWithSuccessMessage() {
        Long rowId = 8L;
        Long orderId = 100L;

        when(ordersService.deleteOrderRowAndUpdateState(rowId)).thenReturn(orderId);

        String view = controller.deleteOrderRow(rowId, redirectAttributes);

        verify(ordersService).deleteOrderRowAndUpdateState(rowId);
        verify(redirectAttributes).addFlashAttribute("success", "Item removed, stock updated, and amount refunded.");
        assertEquals("redirect:/orders/edit/" + orderId, view);
    }

    @Test
    void deleteOrderRow_ServiceThrowsException_ShouldRedirectWithError() {
        Long rowId = 8L;
        RuntimeException ex = new RuntimeException("Delete failed");

        doThrow(ex).when(ordersService).deleteOrderRowAndUpdateState(rowId);

        String view = controller.deleteOrderRow(rowId, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("error", "Error deleting item: " + ex.getMessage());
        assertEquals("redirect:/orders", view);
    }
}