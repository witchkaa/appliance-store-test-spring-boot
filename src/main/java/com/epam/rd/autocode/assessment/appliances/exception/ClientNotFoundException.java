package com.epam.rd.autocode.assessment.appliances.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Client with id " + id + " not found");
    }
}