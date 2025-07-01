package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRegistrationDto {

    @NotBlank(message = "{user.name.notblank}")
    @Size(min = 2, max = 50, message = "{user.name.size}")
    private String name;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.valid}")
    private String email;

    @NotBlank
    @ValidPassword
    @NotBlank(message = "{user.password.notblank}")
    private String password;

    @NotBlank(message = "{client.card.notblank}")
    @Size(min = 9, max = 9, message = "{client.card.size}")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "{client.card.pattern}")
    private String card;
}