package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "{appliance.name.notblank}")
    @Size(max = 100, message = "{appliance.name.size}")
    private String name;

    @NotNull(message = "{appliance.category.notnull}")
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotBlank(message = "{appliance.model.notblank}")
    private String model;

    @NotNull(message = "{appliance.manufacturer.notnull}")
    @ManyToOne
    private Manufacturer manufacturer;

    @NotNull(message = "{appliance.powerType.notnull}")
    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    @Size(max = 255, message = "{appliance.characteristic.size}")
    private String characteristic;

    @Size(max = 255, message = "{appliance.description.size}")
    private String description;

    @Min(value = 0, message = "{appliance.power.min}")
    private Integer power;

    @NotNull(message = "{appliance.price.notnull}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{appliance.price.min}")
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;
}