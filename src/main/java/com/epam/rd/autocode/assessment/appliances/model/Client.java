package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client extends User {
    @NotBlank(message = "{client.card.notblank}")
    @Size(min = 9, max = 9, message = "{client.card.size}")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "{client.card.pattern}")
    private String card;
    public Client(Long id, String name, String email, String password, String card) {
        super(id, name, email, password);
        this.card = card;
    }
}