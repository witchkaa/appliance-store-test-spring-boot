package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    @NotNull(message = "{orders.client.notnull}")
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderRow> orderRowSet = new HashSet<>();

    private Boolean approved;

    private BigDecimal amount;
}