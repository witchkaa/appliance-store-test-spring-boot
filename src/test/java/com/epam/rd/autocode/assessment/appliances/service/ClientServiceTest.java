package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private void mockAuthentication(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void save_ShouldEncodePasswordAndSave() {
        Client client = new Client();
        client.setPassword("rawPass");

        when(repository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        Client saved = clientService.save(client);

        assertNotNull(saved.getPassword());
        assertNotEquals("rawPass", saved.getPassword());
        verify(repository).save(client);
    }

    @Test
    void findById_ShouldReturnClient() {
        Client client = new Client();
        when(repository.findById(1L)).thenReturn(Optional.of(client));

        Optional<Client> found = clientService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(client, found.get());
    }

    @Test
    void search_ById() {
        Client client = new Client();
        when(repository.findById(5L)).thenReturn(Optional.of(client));

        List<Client> result = clientService.search(5L, null);

        assertEquals(1, result.size());
        assertEquals(client, result.get(0));
    }

    @Test
    void search_ByName() {
        List<Client> clients = List.of(new Client(), new Client());
        when(repository.findByNameContainingIgnoreCase("john")).thenReturn(clients);

        List<Client> result = clientService.search(null, "john");

        assertEquals(2, result.size());
    }

    @Test
    void getCurrentClient_ShouldReturnClientFromSecurityContext() {
        String email = "test@example.com";
        Client client = new Client();
        client.setEmail(email);

        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));

        Client current = clientService.getCurrentClient();

        assertEquals(email, current.getEmail());
    }

    @Test
    void getCurrentClient_NoAuthentication_ShouldThrow() {
        SecurityContextHolder.clearContext();

        assertThrows(NullPointerException.class, () -> clientService.getCurrentClient());
    }

    @Test
    void topUpBalance_ShouldIncreaseBalance() {
        String email = "email@example.com";
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(100));
        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        clientService.topUpBalance(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), client.getBalance());
        verify(clientRepository).save(client);
    }

    @Test
    void hasSufficientBalance_ShouldReturnTrueIfEnough() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(200));
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));

        boolean result = clientService.hasSufficientBalance(BigDecimal.valueOf(150));

        assertTrue(result);
    }

    @Test
    void hasSufficientBalance_ShouldReturnFalseIfNotEnough() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(50));
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));

        boolean result = clientService.hasSufficientBalance(BigDecimal.valueOf(100));

        assertFalse(result);
    }

    @Test
    void deductBalance_ShouldDecreaseBalance() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(100));
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        clientService.deductBalance(BigDecimal.valueOf(40));

        assertEquals(BigDecimal.valueOf(60), client.getBalance());
        verify(clientRepository).save(client);
    }

    @Test
    void deductBalance_NotEnoughMoney_ShouldThrow() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(30));
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clientService.deductBalance(BigDecimal.valueOf(50)));

        assertEquals("Not enough money", ex.getMessage());
    }

    @Test
    void changePassword_Success() {
        String email = "email@example.com";
        Client client = new Client();
        client.setEmail(email);
        // Хэш пароля для "oldPass"
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        client.setPassword(encoder.encode("oldPass"));

        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        clientService.changePassword("oldPass", "newStrongPass");

        assertTrue(encoder.matches("newStrongPass", client.getPassword()));
    }

    @Test
    void changePassword_WrongOldPassword_ShouldThrow() {
        Client client = new Client();
        client.setPassword(new BCryptPasswordEncoder().encode("correctOld"));
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clientService.changePassword("wrongOld", "newPass"));

        assertEquals("profile.password.invalid", ex.getMessage());
    }

    @Test
    void changeEmail_Success() {
        String currentEmail = "current@example.com";
        String newEmail = "new@example.com";

        Client client = new Client();
        client.setEmail(currentEmail);

        mockAuthentication(currentEmail);
        when(clientRepository.findByEmail(currentEmail)).thenReturn(Optional.of(client));
        when(repository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        clientService.changeEmail(currentEmail, newEmail);

        assertEquals(newEmail, client.getEmail());
    }

    @Test
    void changeEmail_EmailMismatch_ShouldThrow() {
        Client client = new Client();
        client.setEmail("user@example.com");

        mockAuthentication("user@example.com");
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clientService.changeEmail("wrong@example.com", "new@example.com"));

        assertEquals("profile.email.mismatch", ex.getMessage());
    }

    @Test
    void changeEmail_EmailExists_ShouldThrow() {
        Client client = new Client();
        client.setEmail("user@example.com");

        mockAuthentication("user@example.com");
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));
        when(repository.findByEmail("existing@example.com")).thenReturn(Optional.of(new Client()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clientService.changeEmail("user@example.com", "existing@example.com"));

        assertEquals("profile.email.exists", ex.getMessage());
    }
}