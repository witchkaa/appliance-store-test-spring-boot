package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User {
    @NotBlank(message = "{client.card.notblank}")
    @Size(min = 8, max = 20, message = "{client.card.size}")
    private String card;
}