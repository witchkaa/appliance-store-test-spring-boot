package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRowDto {

    private Long id;

    @NotNull(message = "{orderRow.appliance.notnull}")
    private Long applianceId;

    private String applianceName;

    private BigDecimal appliancePrice;

    @NotNull(message = "{orderRow.number.notnull}")
    @Min(value = 1, message = "{orderRow.number.min}")
    private Long number;

    @NotNull(message = "{orderRow.amount.notnull}")
    @DecimalMin(value = "0.01", message = "{orderRow.amount.min}")
    private BigDecimal amount;
}