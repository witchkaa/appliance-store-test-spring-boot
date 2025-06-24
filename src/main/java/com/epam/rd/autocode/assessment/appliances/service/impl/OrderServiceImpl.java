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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderRowRepository orderRowRepository;
    private final ApplianceRepository applianceRepository;
    @Override
    public List<Orders> getAll() {
        return ordersRepository.findAll();
    }

    @Override
    public List<Orders> getByClientId(Long clientId) {
        return ordersRepository.findByClient_Id(clientId);
    }

    @Override
    public Orders save(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public void delete(Long id) {
        ordersRepository.deleteById(id);
    }

    @Override
    public Orders getById(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
    }

    @Override
    public void approve(Long id) {
        ordersRepository.findById(id).ifPresent(order -> {
            order.setApproved(true);
            ordersRepository.save(order);
        });
    }

    @Override
    public void unapprove(Long id) {
        ordersRepository.findById(id).ifPresent(order -> {
            order.setApproved(false);
            ordersRepository.save(order);
        });
    }

    @Override
    public List<OrderRow> getOrderRows(Long orderId) {
        return orderRowRepository.findByOrder_Id(orderId);
    }

    @Override
    public List<Appliance> getAvailableAppliances() {
        return applianceRepository.findAll();
    }

    @Override
    public void addApplianceToOrder(Long orderId, Long applianceId, int numbers, BigDecimal price) {
        Orders order = getById(orderId);
        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new EntityNotFoundException("Appliance not found with id " + applianceId));

        OrderRow row = new OrderRow();
        row.setOrder(order);
        row.setAppliance(appliance);
        row.setNumber((long) numbers);
        row.setAmount(price.multiply(BigDecimal.valueOf(numbers)));

        orderRowRepository.save(row);
    }
}