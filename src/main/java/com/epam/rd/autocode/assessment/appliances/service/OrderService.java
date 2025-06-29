package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
public interface OrderService {
    Page<Orders> getAllPageable(Pageable pageable);
    List<Orders> getAll();
    List<Orders> getByClientId(Long clientId);
    Orders save(Orders order);
    void delete(Long id);
    Orders getById(Long id);
    void approve(Long id);
    void unapprove(Long id);
    List<OrderRow> getOrderRows(Long orderId);
    List<Appliance> getAvailableAppliances();
    void createOrderWithItems(List<Long> applianceIds, List<Integer> quantities);
    void createOrderWithAppliances(OrderFormDto orderForm);
    void addApplianceToOrder(Long orderId, Long applianceId, int numbers, BigDecimal price);
    Orders createOrderFromCart(List<CartItem> cartItems, Client client);
    Page<Orders> searchOrders(Long id, String clientName, Pageable pageable);
    boolean isEmployee();
    Orders chargeClientForOrder(Orders order);
}