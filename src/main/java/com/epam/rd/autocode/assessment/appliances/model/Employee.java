package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends User {
    @NotBlank(message = "{employee.department.notblank}")
    @Size(min = 2, max = 100, message = "{employee.department.size}")
    private String department;
}