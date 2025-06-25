package com.epam.rd.autocode.assessment.appliances.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderFormDto {
    private List<Long> applianceIds;
    private List<Integer> quantities;

}