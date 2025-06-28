package com.epam.rd.autocode.assessment.appliances.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[@#$%^&+=!].*")) return false;
        return true;
    }
}