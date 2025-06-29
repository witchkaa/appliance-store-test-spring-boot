package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.exception.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return repository.findAll();
    }

    public Client save(Client client) {
        log.info("Saving client: {}", client);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return repository.save(client);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ClientNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public Optional<Client> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Client> search(Long id, String name) {
        if (id != null) {
            return repository.findById(id).map(List::of).orElseGet(List::of);
        }
        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name);
        }
        return repository.findAll();
    }
    @Override
    public Client getCurrentClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with email: " + email));
    }

    @Override
    public List<Orders> getOrdersForCurrentClient() {
        Client client = getCurrentClient();
        return ordersRepository.findByClient_Email(client.getEmail());
    }
    @Override
    @Transactional
    public void topUpBalance(BigDecimal amount) {
        Client client = getCurrentClient();
        client.setBalance(client.getBalance().add(amount));
        clientRepository.save(client);
    }

    @Override
    public boolean hasSufficientBalance(BigDecimal orderAmount) {
        return getCurrentClient().getBalance().compareTo(orderAmount) >= 0;
    }

    @Override
    @Transactional
    public void deductBalance(BigDecimal orderAmount) {
        Client client = getCurrentClient();
        if (client.getBalance().compareTo(orderAmount) < 0) {
            throw new IllegalStateException("Not enough money");
        }
        client.setBalance(client.getBalance().subtract(orderAmount));
        clientRepository.save(client);
    }
    @Override
    public boolean emailExists(String email) {
        return repository.findByEmail(email).isPresent();
    }
}