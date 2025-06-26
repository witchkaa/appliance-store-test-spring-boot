package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderRow {


    public OrderRow(Long id, BigDecimal amount, Long order, Appliance appliance) {
        this.id = id;
        this.amount=amount;
        this.appliance = appliance;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "{orderRow.appliance.notnull}")
    private Appliance appliance;

    private BigDecimal amount;

    @NotNull(message = "{orderRow.number.notnull}")
    @Min(value = 1, message = "{orderRow.number.min}")
    private Long number;
}