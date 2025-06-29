package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClientResponseDto {

    private Long id;
    private String name;
    private String email;
    private String card;
    private BigDecimal balance;
    private String role;
    private String password;
}