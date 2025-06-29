package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ApplianceRequestDto {

    @NotBlank(message = "{appliance.name.notblank}")
    @Size(max = 100, message = "{appliance.name.size}")
    private String name;

    @NotNull(message = "{appliance.category.notnull}")
    private Category category;

    @NotBlank(message = "{appliance.model.notblank}")
    private String model;

    @NotNull(message = "{appliance.manufacturer.notnull}")
    private Long manufacturerId;

    @NotNull(message = "{appliance.powerType.notnull}")
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

    @NotNull(message = "{appliance.type.notnull}")
    private ProductType type;

    @NotNull(message = "{appliance.quantityInStock.notnull}")
    private Integer quantityInStock;

    private Long id;
}