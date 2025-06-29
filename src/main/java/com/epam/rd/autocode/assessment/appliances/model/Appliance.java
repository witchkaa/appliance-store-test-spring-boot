package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appliance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String model;

    @ManyToOne
    private Manufacturer manufacturer;

    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    private String characteristic;

    private String description;

    private Integer power;

    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private Integer quantityInStock;
}