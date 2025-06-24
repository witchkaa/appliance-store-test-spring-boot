package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> getAll();
    Client save(Client client);
    void delete(Long id);
    Optional<Client> findById(Long id);
}
