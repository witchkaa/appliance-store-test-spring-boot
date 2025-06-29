package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.validator.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClientRequestDto {
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

    @NotBlank(message = "{client.card.notblank}")
    @Size(min = 9, max = 9, message = "{client.card.size}")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "{client.card.pattern}")
    private String card;

    private BigDecimal balance;
}