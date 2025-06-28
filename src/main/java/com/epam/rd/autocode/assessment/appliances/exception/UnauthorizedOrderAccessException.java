package com.epam.rd.autocode.assessment.appliances.exception;

public class UnauthorizedOrderAccessException extends RuntimeException {
    public UnauthorizedOrderAccessException() {
        super("Access denied to this order");
    }
}