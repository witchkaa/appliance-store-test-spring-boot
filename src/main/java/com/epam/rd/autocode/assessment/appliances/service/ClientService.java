package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> getAll();
    Client save(Client client);
    void delete(Long id);
    Optional<Client> findById(Long id);
    List<Client> search(Long id, String name);
    Client getCurrentClient();
    List<Orders> getOrdersForCurrentClient();
    void topUpBalance(BigDecimal amount);
    boolean hasSufficientBalance(BigDecimal orderAmount);
    void deductBalance(BigDecimal orderAmount);
    boolean emailExists(String email);
}
