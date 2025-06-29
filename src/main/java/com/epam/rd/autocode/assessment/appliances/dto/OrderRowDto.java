package com.epam.rd.autocode.assessment.appliances.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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