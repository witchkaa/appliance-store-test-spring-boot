package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import lombok.*;

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
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderRow> orderRowSet;

    private Boolean approved;
}