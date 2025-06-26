package com.epam.rd.autocode.assessment.appliances.service;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientServiceImpl(repository);
    }

    @Test
    void getAll_shouldReturnListOfClients() {
        List<Client> clients = List.of(new Client(), new Client());
        Mockito.when(repository.findAll()).thenReturn(clients);

        List<Client> result = clientService.getAll();

        Assertions.assertEquals(2, result.size());
        Mockito.verify(repository).findAll();
    }


    @Test
    void delete_shouldCallRepositoryDeleteById() {
        Long id = 1L;
        clientService.delete(id);
        Mockito.verify(repository).deleteById(id);
    }

    @Test
    void findById_shouldReturnClientOptional() {
        Long id = 1L;
        Client client = new Client();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findById(id);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(client, result.get());
        Mockito.verify(repository).findById(id);
    }
}