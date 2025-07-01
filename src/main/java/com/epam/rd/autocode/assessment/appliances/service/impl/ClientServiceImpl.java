package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ClientRepository clientRepository;
    private final OrdersRepository ordersRepository;

    public List<Client> getAll() {
        log.info("Fetching all clients");
        List<Client> clients = repository.findAll();
        log.debug("Found {} clients", clients.size());
        return clients;
    }

    public Client save(Client client) {
        log.info("Saving client with email: {}", client.getEmail());
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        Client saved = repository.save(client);
        log.info("Client saved with id: {}", saved.getId());
        return saved;
    }

    public void delete(Long id) {
        log.info("Deleting client with id: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Client with id {} not found for deletion", id);
            throw new ClientNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Client with id {} deleted", id);
    }

    public Optional<Client> findById(Long id) {
        log.info("Finding client by id: {}", id);
        Optional<Client> client = repository.findById(id);
        if (client.isPresent()) {
            log.debug("Client found: {}", client.get());
        } else {
            log.warn("Client with id {} not found", id);
        }
        return client;
    }

    @Override
    public List<Client> search(Long id, String name) {
        log.info("Searching clients with id: {}, name: {}", id, name);
        List<Client> result;
        if (id != null) {
            result = repository.findById(id).map(List::of).orElseGet(List::of);
        } else if (name != null && !name.isBlank()) {
            result = repository.findByNameContainingIgnoreCase(name);
        } else {
            result = repository.findAll();
        }
        log.debug("Search returned {} clients", result.size());
        return result;
    }

    @Override
    public Client getCurrentClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        log.debug("Getting current client by email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Client not found with email: {}", email);
                    return new UsernameNotFoundException("Client not found with email: " + email);
                });
        return client;
    }

    @Override
    public List<Orders> getOrdersForCurrentClient() {
        Client client = getCurrentClient();
        log.info("Getting orders for client id: {}", client.getId());
        List<Orders> orders = ordersRepository.findByClient_Email(client.getEmail());
        log.debug("Found {} orders for client id: {}", orders.size(), client.getId());
        return orders;
    }

    @Override
    @Transactional
    public void topUpBalance(BigDecimal amount) {
        Client client = getCurrentClient();
        log.info("Topping up balance for client id {} by amount {}", client.getId(), amount);
        client.setBalance(client.getBalance().add(amount));
        clientRepository.save(client);
        log.info("Balance topped up for client id {}", client.getId());
    }

    @Override
    public boolean hasSufficientBalance(BigDecimal orderAmount) {
        Client client = getCurrentClient();
        boolean result = client.getBalance().compareTo(orderAmount) >= 0;
        log.debug("Checking sufficient balance for client id {}: needed {}, available {}, result {}", client.getId(), orderAmount, client.getBalance(), result);
        return result;
    }

    @Override
    @Transactional
    public void deductBalance(BigDecimal orderAmount) {
        Client client = getCurrentClient();
        log.info("Deducting balance for client id {} by amount {}", client.getId(), orderAmount);
        if (client.getBalance().compareTo(orderAmount) < 0) {
            log.warn("Insufficient balance for client id {}: balance {}, required {}", client.getId(), client.getBalance(), orderAmount);
            throw new IllegalStateException("Not enough money");
        }
        client.setBalance(client.getBalance().subtract(orderAmount));
        clientRepository.save(client);
        log.info("Balance deducted for client id {}", client.getId());
    }

    @Override
    public boolean emailExists(String email) {
        boolean exists = repository.findByEmail(email).isPresent();
        log.debug("Checking if email exists: {} -> {}", email, exists);
        return exists;
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Client client = getCurrentClient();
        log.info("Changing password for client id {}", client.getId());

        if (oldPassword == null || oldPassword.isBlank()) {
            log.warn("Old password is blank for client id {}", client.getId());
            throw new IllegalArgumentException("profile.password.old.required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            log.warn("New password is blank for client id {}", client.getId());
            throw new IllegalArgumentException("profile.password.new.required");
        }

        if (!passwordEncoder.matches(oldPassword, client.getPassword())) {
            log.warn("Invalid old password for client id {}", client.getId());
            throw new IllegalArgumentException("profile.password.invalid");
        }

        if (newPassword.length() < 6) {
            log.warn("New password too short for client id {}", client.getId());
            throw new IllegalArgumentException("profile.password.tooShort");
        }

        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);
        log.info("Password changed for client id {}", client.getId());
    }

    @Override
    @Transactional
    public void changeEmail(String currentEmail, String newEmail) {
        Client client = getCurrentClient();
        log.info("Changing email for client id {}", client.getId());

        if (currentEmail == null || !currentEmail.equalsIgnoreCase(client.getEmail())) {
            log.warn("Current email mismatch for client id {}", client.getId());
            throw new IllegalArgumentException("profile.email.mismatch");
        }

        if (newEmail == null || newEmail.isBlank()) {
            log.warn("New email is blank for client id {}", client.getId());
            throw new IllegalArgumentException("profile.email.required");
        }

        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            log.warn("New email format invalid for client id {}", client.getId());
            throw new IllegalArgumentException("profile.email.invalidFormat");
        }

        if (emailExists(newEmail)) {
            log.warn("New email already exists: {} for client id {}", newEmail, client.getId());
            throw new IllegalArgumentException("profile.email.exists");
        }

        client.setEmail(newEmail);
        clientRepository.save(client);
        log.info("Email changed to {} for client id {}", newEmail, client.getId());
    }
}