package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.*;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrderRowRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderRowRepository orderRowRepository;
    private final ApplianceRepository applianceRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<Orders> getAll() {
        log.debug("Fetching orders");
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
        return ordersRepository.save(order);
    }

    @Override
    public void delete(Long id) {
        log.warn("Deleting order id {}", id);
        Orders order = getById(id);
        checkAccess(order);
        ordersRepository.delete(order);
    }

    @Override
    public Orders getById(Long id) {
        log.debug("Getting order by id: {}", id);
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));

        if (isClient() && !isCurrentClient(order.getClient())) {
            log.error("The client tried to access an order that does not belong to them");
            throw new AccessDeniedException("Access denied to this order");
        }

        return order;
    }

    private boolean isClient() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    private boolean isCurrentClient(Client orderClient) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderClient.getEmail().equals(email);
    }

    @Override
    public void approve(Long id) {
        if (!isEmployee()) {
            log.info("Client tried to approve an order");
            throw new AccessDeniedException("Only employees can approve orders");
        }
        log.info("Approving order id {}", id);
        Orders order = getById(id);
        order.setApproved(true);
        ordersRepository.save(order);
    }

    @Override
    public void unapprove(Long id) {
        if (!isEmployee()) {
            log.info("Client tried to unapprove an order");
            throw new AccessDeniedException("Only employees can unapprove orders");
        }
        log.info("Unapproving order id {}", id);
        ordersRepository.findById(id).ifPresent(order -> {
            order.setApproved(false);
            ordersRepository.save(order);
        });
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
                .orElseThrow(() -> {
                    log.error("Appliance not found with id {}", applianceId);
                    return new EntityNotFoundException("Appliance not found with id " + applianceId);
                });

        OrderRow row = new OrderRow();
        row.setOrder(order);
        row.setAppliance(appliance);
        row.setNumber((long) numbers);
        row.setAmount(price.multiply(BigDecimal.valueOf(numbers)));

        orderRowRepository.save(row);
        log.info("Appliance {} added to order {}", applianceId, orderId);
    }
    private void checkAccess(Orders order) {
        if (isEmployee()) return;

        if (isClient()) {
            String currentEmail = getCurrentUsername();
            if (!order.getClient().getEmail().equals(currentEmail)) {
                throw new AccessDeniedException("Access denied to this order");
            }
        }
    }

    @Transactional
    public void createOrderWithItems(List<Long> applianceIds, List<Integer> quantities) {
        Client client = getCurrentClient();

        Orders order = new Orders();
        order.setClient(client);
        order.setApproved(false);

        Set<OrderRow> orderRows = new HashSet<>();

        for (int i = 0; i < applianceIds.size(); i++) {
            Appliance appliance = applianceRepository.findById(applianceIds.get(i))
                    .orElseThrow(() -> new EntityNotFoundException("Appliance not found"));

            OrderRow row = new OrderRow();
            row.setOrder(order);
            row.setAppliance(appliance);
            row.setNumber(quantities.get(i).longValue());
            row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(quantities.get(i))));

            orderRows.add(row);
        }

        order.setOrderRowSet(orderRows);

        ordersRepository.save(order);
    }
    // !!!!!!!!!!
    private Client getCurrentClient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with email: " + email));
    }
    private boolean isEmployee() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
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
                    .orElseThrow(() -> new EntityNotFoundException("Appliance not found: " + applianceId));

            OrderRow row = new OrderRow();
            row.setOrder(order);
            row.setAppliance(appliance);
            row.setNumber(qty.longValue());
            row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(qty)));

            orderRowRepository.save(row);
        }
    }
}