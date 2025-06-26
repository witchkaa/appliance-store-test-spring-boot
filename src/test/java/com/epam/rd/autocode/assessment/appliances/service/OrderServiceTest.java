package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private OrderServiceImpl orderService;


    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticationWithRole(String username, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "pass", authorities);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getAllPageable_asEmployee_returnsAllOrdersPage() {
        mockAuthenticationWithRole("employee@example.com", "ROLE_EMPLOYEE");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Orders> page = new PageImpl<>(List.of(new Orders(), new Orders()));
        Mockito.when(ordersRepository.findAll(pageable)).thenReturn(page);

        Page<Orders> result = orderService.getAllPageable(pageable);

        Assertions.assertEquals(2, result.getTotalElements());
        Mockito.verify(ordersRepository).findAll(pageable);
    }

    @Test
    void getAllPageable_asClient_returnsOrdersOfClient() {
        String clientEmail = "client@example.com";
        mockAuthenticationWithRole(clientEmail, "ROLE_CLIENT");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Orders> page = new PageImpl<>(List.of(new Orders()));
        Mockito.when(ordersRepository.findByClient_Email(clientEmail, pageable)).thenReturn(page);

        Page<Orders> result = orderService.getAllPageable(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(ordersRepository).findByClient_Email(clientEmail, pageable);
    }

    @Test
    void getAllPageable_asOtherRole_returnsEmptyPage() {
        mockAuthenticationWithRole("unknown@example.com", "ROLE_OTHER");

        Pageable pageable = PageRequest.of(0, 5);

        Page<Orders> result = orderService.getAllPageable(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void save_asClient_setsClientAndNotApproved() {
        String email = "client@example.com";
        mockAuthenticationWithRole(email, "ROLE_CLIENT");

        Client client = new Client();
        client.setEmail(email);

        Orders order = new Orders();

        Mockito.when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        Mockito.when(ordersRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Orders saved = orderService.save(order);

        Assertions.assertEquals(client, saved.getClient());
    }

    @Test
    void delete_existingOrder_employeeCanDelete() {
        mockAuthenticationWithRole("employee@example.com", "ROLE_EMPLOYEE");

        Orders order = new Orders();
        order.setId(1L);

        Mockito.when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L);

        Mockito.verify(ordersRepository).delete(order);
    }

    @Test
    void delete_nonOwnerClient_throwsAccessDenied() {
        String clientEmail = "client1@example.com";
        mockAuthenticationWithRole(clientEmail, "ROLE_CLIENT");

        Client client = new Client();
        client.setEmail("otherclient@example.com"); // different email

        Orders order = new Orders();
        order.setClient(client);

        Mockito.when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        Assertions.assertThrows(AccessDeniedException.class, () -> orderService.delete(1L));
    }

    @Test
    void approve_asEmployee_setsApprovedAndEmployee() {
        String employeeEmail = "employee@example.com";
        mockAuthenticationWithRole(employeeEmail, "ROLE_EMPLOYEE");

        Employee employee = new Employee();
        employee.setEmail(employeeEmail);

        Orders order = new Orders();
        order.setId(1L);
        order.setApproved(false);

        Mockito.when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        Mockito.when(ordersRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.approve(1L);

        Assertions.assertEquals(employee, order.getEmployee());
    }

    @Test
    void approve_asClient_throwsAccessDenied() {
        mockAuthenticationWithRole("client@example.com", "ROLE_CLIENT");

        Assertions.assertThrows(AccessDeniedException.class, () -> orderService.approve(1L));
    }
}