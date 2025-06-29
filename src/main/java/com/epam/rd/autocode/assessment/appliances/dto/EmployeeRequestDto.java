package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.validator.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequestDto {
    private Long id;
    @NotBlank(message = "{user.name.notblank}")
    @Size(min = 2, max = 50, message = "{user.name.size}")
    private String name;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.valid}")
    private String email;

    @ValidPassword
    @NotBlank(message = "{user.password.notblank}")
    private String password;

    @NotBlank(message = "{employee.department.notblank}")
    @Size(min = 2, max = 100, message = "{employee.department.size}")
    private String department;

}