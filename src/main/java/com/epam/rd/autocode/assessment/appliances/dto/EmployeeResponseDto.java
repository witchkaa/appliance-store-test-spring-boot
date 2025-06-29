package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDto {
    private Long id;
    private String name;
    private String email;
    private String department;
    private String role;
    private String password;
}