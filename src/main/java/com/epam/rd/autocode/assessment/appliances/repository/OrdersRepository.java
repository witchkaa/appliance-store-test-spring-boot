package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByClient_Id(Long clientId);
    List<Orders> findByClient_Email(String email);
    Page<Orders> findByClient_Email(String email, Pageable pageable);
    Page<Orders> findByClientNameContainingIgnoreCase(String name, Pageable pageable);
}
