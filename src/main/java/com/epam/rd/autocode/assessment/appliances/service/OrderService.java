package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;

import java.math.BigDecimal;
import java.util.List;
public interface OrderService {
    List<Orders> getAll();
    List<Orders> getByClientId(Long clientId);
    Orders save(Orders order);
    void delete(Long id);
    Orders getById(Long id);
    void approve(Long id);
    void unapprove(Long id);
    List<OrderRow> getOrderRows(Long orderId);
    List<Appliance> getAvailableAppliances();
    void addApplianceToOrder(Long orderId, Long applianceId, int numbers, BigDecimal price);
}