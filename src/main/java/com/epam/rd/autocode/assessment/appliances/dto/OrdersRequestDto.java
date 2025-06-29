package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class OrdersRequestDto {

    private Long id;

    private Long employeeId;

    @NotNull(message = "{orders.client.notnull}")
    private Long clientId;

    private Boolean approved;

    private BigDecimal amount;

    private boolean paid;

    private LocalDateTime orderDateTime;

    private Set<OrderRowRequestDto> orderRows;

}