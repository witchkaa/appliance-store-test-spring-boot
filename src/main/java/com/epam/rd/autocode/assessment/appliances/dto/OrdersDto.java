package com.epam.rd.autocode.assessment.appliances.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrdersDto {

    private Long id;

    private Long employeeId;

    private String employeeName;

    @NotNull(message = "{orders.client.notnull}")
    private Long clientId;

    private String clientName;

    private Boolean approved;

    @NotNull(message = "{orders.amount.notnull}")
    @DecimalMin(value = "0.00", inclusive = true, message = "{orders.amount.min}")
    private BigDecimal amount;

    private boolean paid;

    private LocalDateTime orderDateTime;

    @NotNull(message = "{orders.rows.notnull}")
    @Size(min = 1, message = "{orders.rows.min}")
    private List<@Valid OrderRowDto> orderRows;

}