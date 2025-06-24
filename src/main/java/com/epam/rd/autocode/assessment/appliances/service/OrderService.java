package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Orders> getAll();
    List<Orders> getByClientId(Long clientId);
    Orders save(Orders order);
    void delete(Long id);
    Optional<Orders> findById(Long id);
    void approve(Long id);
}
