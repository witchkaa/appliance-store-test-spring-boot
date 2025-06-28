package com.epam.rd.autocode.assessment.appliances.controller;


import com.epam.rd.autocode.assessment.appliances.model.Orders;

import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService ordersService;

    private Orders sampleOrder;

    @BeforeEach
    void setup() {
        sampleOrder = new Orders();
        sampleOrder.setId(1L);
        sampleOrder.setApproved(false);
        sampleOrder.setAmount(BigDecimal.valueOf(1000));
        // setup other fields as needed
    }

    @Test
    void testListOrders_noParams_returnsPage() throws Exception {
        when(ordersService.isEmployee()).thenReturn(true);
        when(ordersService.getAllPageable(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrder)));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("order/orders"));

        verify(ordersService).getAllPageable(any(Pageable.class));
    }

    @Test
    void testListOrders_withSearchParams_callsSearch() throws Exception {
        when(ordersService.isEmployee()).thenReturn(true);
        when(ordersService.searchOrders(eq(1L), eq("clientName"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrder)));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .param("id", "1")
                        .param("client", "clientName"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("order/orders"));

        verify(ordersService).searchOrders(eq(1L), eq("clientName"), any(Pageable.class));
    }

    @Test
    void testCreateForm_returnsNewOrderForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("appliances"))
                .andExpect(view().name("order/newOrder"));
    }

    @Test
    void testSaveOrder_validRequest_redirects() throws Exception {
        when(ordersService.getAvailableAppliances()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/add-order")
                        .param("applianceIds", "1", "2")
                        .param("quantities", "3", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(ordersService).createOrderWithItems(List.of(1L, 2L), List.of(3, 5));
    }

    @Test
    void testSaveOrder_noAppliances_showsError() throws Exception {
        when(ordersService.getAvailableAppliances()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/add-order"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("order/newOrder"));
    }

    @Test
    void testDeleteOrder_success_redirects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(ordersService).delete(1L);
    }

    @Test
    void testApproveOrder_success_redirects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/approve/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(ordersService).approve(1L);
    }

    @Test
    void testUnapproveOrder_success_redirects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/unapproved/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(ordersService).unapprove(1L);
    }

    @Test
    void testAddIntoOrder_redirects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/add-into-order")
                        .param("ordersId", "1")
                        .param("applianceId", "10")
                        .param("numbers", "2")
                        .param("price", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/edit/1"));

        verify(ordersService).addApplianceToOrder(1L, 10L, 2, new BigDecimal("100.00"));
    }

    @Test
    void testOrderDetails_returnsFragment() throws Exception {
        when(ordersService.getById(1L)).thenReturn(sampleOrder);
        when(ordersService.getOrderRows(1L)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/details/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("rows"))
                .andExpect(model().attributeExists("amount"))
                .andExpect(view().name("order/orderDetails :: details"));
    }

    @Test
    void testCreateFromDto_redirectsOnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/orders/add-order-dto")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("applianceIds[0]", "1")
                        .param("quantities[0]", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(ordersService).createOrderWithAppliances(any());
    }

}