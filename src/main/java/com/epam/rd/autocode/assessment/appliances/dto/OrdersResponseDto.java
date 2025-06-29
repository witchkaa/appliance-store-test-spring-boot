package com.epam.rd.autocode.assessment.appliances.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class OrdersResponseDto {
    private Long id;
    private Long employeeId;
    private Long clientId;
    private Boolean approved;
    private BigDecimal amount;
    private boolean paid;
    private LocalDateTime orderDateTime;
    private Set<OrderRowResponseDto> orderRows;
}