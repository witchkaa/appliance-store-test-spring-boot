package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRowRepository extends JpaRepository<OrderRow, Long> {
    @Query("SELECT r FROM OrderRow r WHERE r.order.id = :orderId")
    List<OrderRow> findByOrder_Id(@Param("orderId") Long orderId);
}