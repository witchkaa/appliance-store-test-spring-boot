package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public List<Client> getAll() {
        log.debug("Fetching all clients");
        return repository.findAll();
    }

    public Client save(Client client) {
        log.info("Saving client: {}", client);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return repository.save(client);
    }

    public void delete(Long id) {
        log.warn("Deleting client with id: {}", id);
        repository.deleteById(id);
    }

    public Optional<Client> findById(Long id) {
        log.debug("Finding client by id: {}", id);
        return repository.findById(id);
    }
    @Override
    public List<Client> search(Long id, String name) {
        if (id != null) {
            return repository.findById(id)
                    .map(List::of)
                    .orElseGet(List::of);
        }

        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name);
        }

        return repository.findAll();
    }
}
