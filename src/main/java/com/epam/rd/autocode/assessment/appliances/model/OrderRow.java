package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "{orderRow.appliance.notnull}")
    private Appliance appliance;

    @ManyToOne
    @NotNull(message = "{orderRow.order.notnull}")
    private Orders order;

    @NotNull(message = "{orderRow.number.notnull}")
    @Min(value = 1, message = "{orderRow.number.min}")
    private Long number;

    @NotNull(message = "{orderRow.amount.notnull}")
    @DecimalMin(value = "0.01", message = "{orderRow.amount.min}")
    private BigDecimal amount;
}