package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrderRowRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderRowRepository orderRowRepository;
    private final ApplianceRepository applianceRepository;
    @Override
    public List<Orders> getAll() {
        log.debug("Fetching all orders");
        return ordersRepository.findAll();
    }

    @Override
    public List<Orders> getByClientId(Long clientId) {
        log.debug("Fetching orders for client id {}", clientId);
        return ordersRepository.findByClient_Id(clientId);
    }

    @Override
    public Orders save(Orders order) {
        log.info("Saving order: {}", order);
        return ordersRepository.save(order);
    }

    @Override
    public void delete(Long id) {
        log.warn("Deleting order id {}", id);
        ordersRepository.deleteById(id);
    }

    @Override
    public Orders getById(Long id) {
        log.debug("Getting order by id: {}", id);
        return ordersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with id {}", id);
                    return new EntityNotFoundException("Order not found with id " + id);
                });
    }

    @Override
    public void approve(Long id) {
        log.info("Approving order id {}", id);
        ordersRepository.findById(id).ifPresent(order -> {
            order.setApproved(true);
            ordersRepository.save(order);
        });
    }

    @Override
    public void unapprove(Long id) {
        log.info("Unapproving order id {}", id);
        ordersRepository.findById(id).ifPresent(order -> {
            order.setApproved(false);
            ordersRepository.save(order);
        });
    }

    @Override
    public List<OrderRow> getOrderRows(Long orderId) {
        log.debug("Getting order rows for order id {}", orderId);
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
}