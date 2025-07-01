package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrderFormDto;
import com.epam.rd.autocode.assessment.appliances.exception.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exception.OrderNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exception.UnauthorizedOrderAccessException;
import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderRowRepository orderRowRepository;
    private final ApplianceRepository applianceRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<Orders> getAllPageable(Pageable pageable) {
        log.debug("Fetching orders with pageable: {}", pageable);
        if (isEmployee()) {
            return ordersRepository.findAll(pageable);
        } else if (isClient()) {
            return ordersRepository.findByClient_Email(getCurrentUsername(), pageable);
        }
        return Page.empty();
    }

    @Override
    public List<Orders> getAll() {
        log.debug("Fetching all orders");
        if (isEmployee()) {
            return ordersRepository.findAll();
        } else if (isClient()) {
            return ordersRepository.findByClient_Email(getCurrentUsername());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Orders> getByClientId(Long clientId) {
        log.debug("Fetching orders for client id {}", clientId);
        return ordersRepository.findByClient_Id(clientId);
    }

    @Override
    public Orders save(Orders order) {
        log.info("Saving order: {}", order);
        if (isClient()) {
            Client currentClient = clientRepository.findByEmail(getCurrentUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Client not found"));
            order.setClient(currentClient);
            order.setApproved(false);
        }
        Orders savedOrder = ordersRepository.save(order);
        log.info("Order saved with id: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @PreAuthorize("@authService.isEmployee() || @authService.isOrderOwner(#id)")
    public void delete(Long id) {
        log.warn("Deleting order id {}", id);

        Orders order = getById(id);
        List<OrderRow> rows = orderRowRepository.findByOrder_Id(id);

        for (OrderRow row : rows) {
            Appliance appliance = row.getAppliance();
            appliance.setQuantityInStock((int) (appliance.getQuantityInStock() + row.getNumber()));
            applianceRepository.save(appliance);
            log.debug("Restocked appliance id {} by {}", appliance.getId(), row.getNumber());
        }

        if (!order.getApproved() && order.getClient() != null) {
            BigDecimal amountToReturn = order.getAmount();
            Client client = order.getClient();
            client.setBalance(client.getBalance().add(amountToReturn));
            clientRepository.save(client);
            log.info("Returned {} ₴ to client '{}', new balance: {}", amountToReturn, client.getName(), client.getBalance());
        }

        ordersRepository.delete(order);
        log.info("Order id {} deleted", id);
    }

    @Override
    @PreAuthorize("@authService.isEmployee() || @authService.isOrderOwner(#id)")
    public Orders getById(Long id) {
        log.debug("Getting order by id: {}", id);
        return ordersRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Override
    public void approve(Long id) {
        log.info("Approving order id {}", id);
        Orders order = getById(id);
        order.setApproved(true);
        order.setEmployee(getCurrentEmployee());
        ordersRepository.save(order);
        log.info("Order id {} approved", id);
    }

    @Secured("ROLE_EMPLOYEE")
    @Override
    public void unapprove(Long id) {
        log.info("Unapproving order id {}", id);
        Orders order = getById(id);
        order.setApproved(false);
        order.setEmployee(getCurrentEmployee());
        ordersRepository.save(order);
        log.info("Order id {} unapproved", id);
    }

    @Override
    public List<OrderRow> getOrderRows(Long orderId) {
        log.debug("Getting order rows for order id {}", orderId);
        Orders order = getById(orderId);
        checkAccess(order);
        return orderRowRepository.findByOrder_Id(orderId);
    }

    @Override
    public List<Appliance> getAvailableAppliances() {
        log.debug("Fetching available appliances");
        return applianceRepository.findAll();
    }

    @Override
    public void addApplianceToOrder(Long orderId, Long applianceId, int numbers, BigDecimal price) {
        log.info("Adding appliance {} to order {} with quantity {}", applianceId, orderId, numbers);
        Orders order = getById(orderId);
        checkAccess(order);

        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new ApplianceNotFoundException(applianceId));

        OrderRow row = new OrderRow();
        row.setOrder(order);
        row.setAppliance(appliance);
        row.setNumber((long) numbers);
        row.setAmount(price.multiply(BigDecimal.valueOf(numbers)));

        orderRowRepository.save(row);
        log.info("Appliance {} added to order {}", applianceId, orderId);
    }

    @Transactional
    public void createOrderWithItems(List<Long> applianceIds, List<Integer> quantities) {
        Client client = getCurrentClient();
        log.info("Creating order with items for client {}", client.getEmail());

        Orders order = new Orders();
        order.setClient(client);
        order.setApproved(false);

        Set<OrderRow> orderRows = new HashSet<>();

        for (int i = 0; i < applianceIds.size(); i++) {
            Long applianceId = applianceIds.get(i);
            Integer qty = quantities.get(i);

            Appliance appliance = applianceRepository.findById(applianceId)
                    .orElseThrow(() -> new ApplianceNotFoundException(applianceId));

            applianceRepository.save(appliance);

            OrderRow row = new OrderRow();
            row.setOrder(order);
            row.setAppliance(appliance);
            row.setNumber(qty.longValue());
            row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(qty)));

            orderRows.add(row);
        }

        order.setOrderRowSet(orderRows);
        ordersRepository.save(order);
        log.info("Order created for client {} with {} items", client.getEmail(), orderRows.size());
    }

    @Override
    @Transactional
    public Orders createOrderFromCart(List<CartItem> items, Client client) {
        log.info("Creating order from cart for client {}", client.getEmail());

        Orders order = new Orders();
        order.setClient(client);
        order.setApproved(false);
        order.setOrderDateTime(LocalDateTime.now());
        Set<OrderRow> rows = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : items) {
            Appliance appliance = item.getAppliance();
            int qty = item.getQuantity();

            OrderRow row = new OrderRow();
            row.setAppliance(appliance);
            row.setOrder(order);
            row.setNumber((long) qty);

            BigDecimal amount = appliance.getPrice().multiply(BigDecimal.valueOf(qty));
            row.setAmount(amount);

            totalAmount = totalAmount.add(amount);
            rows.add(row);
        }

        order.setOrderRowSet(rows);
        order.setAmount(totalAmount);

        log.info("Order total amount: {}", totalAmount);
        return ordersRepository.save(order);
    }

    @Override
    public Page<Orders> searchOrders(Long id, String clientName, Pageable pageable) {
        log.debug("Searching orders with id: {}, clientName: {}", id, clientName);

        if (id != null) {
            return ordersRepository.findById(id)
                    .map(order -> new PageImpl<>(List.of(order), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(List.of(), pageable, 0));
        }

        if (clientName != null && !clientName.isBlank()) {
            return ordersRepository.findByClientNameContainingIgnoreCase(clientName, pageable);
        }

        return ordersRepository.findAll(pageable);
    }

    // Access + Auth utilities
    private void checkAccess(Orders order) {
        if (isEmployee()) return;

        if (isClient()) {
            String currentEmail = getCurrentUsername();
            if (!order.getClient().getEmail().equals(currentEmail)) {
                throw new UnauthorizedOrderAccessException();
            }
        }
    }

    private boolean isClient() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    public boolean isEmployee() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Client getCurrentClient() {
        String email = getCurrentUsername();
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with email: " + email));
    }

    private Employee getCurrentEmployee() {
        String email = getCurrentUsername();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));
    }

    @Transactional
    public void createOrderWithAppliances(OrderFormDto orderForm) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        Orders order = new Orders();
        order.setClient(client);
        order.setApproved(false);
        order = ordersRepository.save(order);

        for (int i = 0; i < orderForm.getApplianceIds().size(); i++) {
            Long applianceId = orderForm.getApplianceIds().get(i);
            Integer qty = orderForm.getQuantities().get(i);

            Appliance appliance = applianceRepository.findById(applianceId)
                    .orElseThrow(() -> new ApplianceNotFoundException(applianceId));

            OrderRow row = new OrderRow();
            row.setOrder(order);
            row.setAppliance(appliance);
            row.setNumber(qty.longValue());
            row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(qty)));

            orderRowRepository.save(row);
        }
        log.info("Order with appliances created for client {}", client.getEmail());
    }

    @Transactional
    public Orders chargeClientForOrder(Orders order) {
        Client client = order.getClient();
        BigDecimal totalAmount = order.getAmount();

        log.info("Charging client {} for order id {} with amount {}", client.getEmail(), order.getId(), totalAmount);

        if (client.getBalance().compareTo(totalAmount) >= 0) {
            client.setBalance(client.getBalance().subtract(totalAmount));
            order.setPaid(true);
            clientRepository.save(client);
            log.info("Client {} charged successfully. New balance: {}", client.getEmail(), client.getBalance());
        } else {
            order.setPaid(false);
            log.warn("Client {} has insufficient balance for order id {}", client.getEmail(), order.getId());
        }

        return ordersRepository.save(order);
    }

    public Long deleteOrderRow(Long rowId) {
        log.info("Deleting order row with id {}", rowId);
        OrderRow row = orderRowRepository.findById(rowId)
                .orElseThrow(() -> new RuntimeException("Order row not found"));

        Long orderId = row.getOrder().getId();
        orderRowRepository.delete(row);
        log.info("Order row {} deleted", rowId);
        return orderId;
    }

    @Transactional
    public Long deleteOrderRowAndUpdateState(Long rowId) {
        log.info("Deleting order row and updating state, rowId: {}", rowId);

        OrderRow row = orderRowRepository.findById(rowId)
                .orElseThrow(() -> new RuntimeException("Order row not found"));

        Orders order = row.getOrder();
        Appliance appliance = row.getAppliance();
        Client client = order.getClient();

        int quantityToReturn = Math.toIntExact(row.getNumber());
        appliance.setQuantityInStock(appliance.getQuantityInStock() + quantityToReturn);
        applianceRepository.save(appliance);
        log.debug("Returned {} items to stock for appliance {}", quantityToReturn, appliance.getId());

        BigDecimal refundAmount = row.getAmount();
        if (!order.getApproved() && client != null) {
            client.setBalance(client.getBalance().add(refundAmount));
            clientRepository.save(client);
            log.info("Refunded {} ₴ to client {} for deleted order row", refundAmount, client.getEmail());
        }

        orderRowRepository.delete(row);
        log.info("Order row {} deleted", rowId);

        return order.getId();
    }
}