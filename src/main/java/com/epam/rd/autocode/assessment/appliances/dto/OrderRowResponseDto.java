package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderRowResponseDto {
    private Long id;
    private Long applianceId;
    private Long number;
    private BigDecimal amount;
}