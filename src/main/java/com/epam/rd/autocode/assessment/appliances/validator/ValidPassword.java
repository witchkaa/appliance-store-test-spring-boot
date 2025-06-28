package com.epam.rd.autocode.assessment.appliances.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "{password.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}