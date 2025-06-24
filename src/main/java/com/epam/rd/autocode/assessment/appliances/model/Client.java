package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client extends User {
    private String card;
}