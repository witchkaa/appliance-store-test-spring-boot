package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.model.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ApplianceResponseDto {

    private Long id;
    private String name;
    private Category category;
    private String model;
    private ManufacturerResponseDto manufacturer;
    private PowerType powerType;
    private String characteristic;
    private String description;
    private Integer power;
    private BigDecimal price;
    private ProductType type;
    private Integer quantityInStock;

}