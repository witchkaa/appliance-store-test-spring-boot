package com.epam.rd.autocode.assessment.appliances.exception;

public class ManufacturerNotFoundException extends RuntimeException {
    public ManufacturerNotFoundException(Long id) {
        super("Manufacturer with id " + id + " not found");
    }
}