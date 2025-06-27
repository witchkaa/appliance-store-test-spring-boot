package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> getAll();
    Client save(Client client);
    void delete(Long id);
    Optional<Client> findById(Long id);
    List<Client> search(Long id, String name);
}
