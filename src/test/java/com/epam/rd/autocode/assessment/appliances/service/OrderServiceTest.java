package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.exception.OrderNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks @Spy
    private OrderServiceImpl orderService;

    @Mock(lenient = true)
    private OrdersRepository ordersRepository;

    @Mock(lenient = true)
    private OrderRowRepository orderRowRepository;

    @Mock(lenient = true)
    private ApplianceRepository applianceRepository;

    @Mock(lenient = true)
    private ClientRepository clientRepository;

    @Mock(lenient = true)
    private EmployeeRepository employeeRepository;

    Appliance appliance;
    Client client;
    Orders order;
    OrderRow row;

    @BeforeEach
    void setup() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user@mail.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        appliance = new Appliance();
        appliance.setId(1L);
        appliance.setPrice(BigDecimal.valueOf(100));
        appliance.setQuantityInStock(3);

        client = new Client();
        client.setId(1L);
        client.setEmail("user@mail.com");
        client.setBalance(BigDecimal.valueOf(500));

        order = new Orders();
        order.setId(1L);
        order.setClient(client);
        order.setAmount(BigDecimal.valueOf(200));
        order.setApproved(false);

        row = new OrderRow();
        row.setId(1L);
        row.setOrder(order);
        row.setAppliance(appliance);
        row.setNumber(2L);
        row.setAmount(BigDecimal.valueOf(200));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }



    @Test
    void chargeClientForOrder_shouldNotChargeIfInsufficient() {
        client.setBalance(BigDecimal.valueOf(100));
        order.setPaid(false);

        Orders result = orderService.chargeClientForOrder(order);


        assertEquals(BigDecimal.valueOf(100), client.getBalance());
        verify(clientRepository, never()).save(client);
    }

    @Test
    void getAllPageable_asClient() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Orders> page = new PageImpl<>(List.of(order));

        when(ordersRepository.findByClient_Email("user@mail.com", pageable)).thenReturn(page);

        Page<Orders> result = orderService.getAllPageable(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllPageable_asEmployee() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@mail.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<Orders> page = new PageImpl<>(List.of(order));

        when(ordersRepository.findAll(pageable)).thenReturn(page);

        Page<Orders> result = orderService.getAllPageable(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void approve_shouldSetApprovedAndEmployee() {
        order.setApproved(false);
        Employee employee = new Employee();
        employee.setEmail("admin@mail.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@mail.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
        );

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(employee));

        orderService.approve(1L);

        assertTrue(order.getApproved());
        assertEquals(employee, order.getEmployee());
        verify(ordersRepository).save(order);
    }

    @Test
    void getById_existing_shouldReturn() {
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        Orders result = orderService.getById(1L);
        assertEquals(order, result);
    }

    @Test
    void getById_missing_shouldThrow() {
        when(ordersRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getById(999L));
    }

    @Test
    void addApplianceToOrder_shouldSaveRow() {
        order.setClient(client);
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        when(applianceRepository.findById(1L)).thenReturn(Optional.of(appliance));

        orderService.addApplianceToOrder(1L, 1L, 2, BigDecimal.valueOf(100));

        verify(orderRowRepository).save(argThat(row ->
                row.getAppliance().getId().equals(1L)
                        && row.getNumber() == 2
                        && row.getAmount().equals(BigDecimal.valueOf(200))
        ));
    }
}