package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRowRequestDto {

    private Long id;

    @NotNull(message = "{orderRow.appliance.notnull}")
    private Long applianceId;

    @NotNull(message = "{orderRow.number.notnull}")
    @Min(value = 1, message = "{orderRow.number.min}")
    private Long number;

    @NotNull(message = "{orderRow.amount.notnull}")
    @DecimalMin(value = "0.01", message = "{orderRow.amount.min}")
    private BigDecimal amount;
}