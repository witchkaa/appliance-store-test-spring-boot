package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrdersRepository ordersRepository;
    @Mock private OrderRowRepository orderRowRepository;
    @Mock private EmployeeRepository employeeRepository;
    @InjectMocks private OrderServiceImpl orderService;

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }


    @Test
    void delete_ShouldReturnMoneyAndDelete_WhenNotApproved() {
        Orders order = new Orders();
        order.setId(1L);
        order.setApproved(false);
        order.setAmount(BigDecimal.TEN);
        Client client = new Client();
        client.setBalance(BigDecimal.ZERO);
        order.setClient(client);

        OrderRow row = new OrderRow();
        row.setAppliance(new Appliance());
        row.setNumber(2L);

        when(orderService.getById(1L)).thenReturn(order);
        when(orderRowRepository.findByOrder_Id(1L)).thenReturn(List.of(row));

        orderService.delete(1L);

        assertEquals(BigDecimal.TEN, client.getBalance());
        verify(ordersRepository).delete(order);
    }

    @Test
    void createOrderFromCart_ShouldReturnSavedOrder() {
        Client client = new Client();
        Appliance appliance = new Appliance();
        appliance.setPrice(BigDecimal.valueOf(500));

        CartItem item = new CartItem();
        item.setAppliance(appliance);
        item.setQuantity(2);

        when(ordersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Orders result = orderService.createOrderFromCart(List.of(item), client);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        assertFalse(result.getApproved());
    }

    @Test
    void approve_ShouldSetApprovedAndAssignEmployee() {
        Orders order = new Orders();
        order.setId(1L);
        Employee employee = new Employee();
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail(any())).thenReturn(Optional.of(employee));
        when(authentication.getName()).thenReturn("emp@example.com");

        orderService.approve(1L);

        assertTrue(order.getApproved());
        assertEquals(employee, order.getEmployee());
        verify(ordersRepository).save(order);
    }

    @Test
    void chargeClientForOrder_ShouldChargeWhenBalanceEnough() {
        Orders order = new Orders();
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(1000));
        order.setClient(client);
        order.setAmount(BigDecimal.valueOf(500));

        when(ordersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        assertEquals(BigDecimal.valueOf(1000), client.getBalance());
    }
}