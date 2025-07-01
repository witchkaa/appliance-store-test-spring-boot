package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock private ClientRepository clientRepository;
    @Mock private OrdersRepository ordersRepository;

    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private ClientServiceImpl clientService;

    private final String testEmail = "test@example.com";
    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setEmail(testEmail);
        testClient.setPassword(new BCryptPasswordEncoder().encode("oldPass"));
        testClient.setBalance(BigDecimal.valueOf(100));

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(testEmail);
    }

    @Test
    void getCurrentClient_returnsClient() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        Client result = clientService.getCurrentClient();

        assertEquals(testEmail, result.getEmail());
    }

    @Test
    void topUpBalance_addsAmountToClientBalance() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        clientService.topUpBalance(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), testClient.getBalance());
        verify(clientRepository).save(testClient);
    }

    @Test
    void deductBalance_reducesBalance_whenSufficient() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        clientService.deductBalance(BigDecimal.valueOf(60));

        assertEquals(BigDecimal.valueOf(40), testClient.getBalance());
        verify(clientRepository).save(testClient);
    }

    @Test
    void deductBalance_throwsException_whenInsufficient() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        assertThrows(IllegalStateException.class, () ->
                clientService.deductBalance(BigDecimal.valueOf(200)));
    }

    @Test
    void changePassword_successfullyChangesPassword() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        clientService.changePassword("oldPass", "newPass123");

        assertTrue(new BCryptPasswordEncoder().matches("newPass123", testClient.getPassword()));
        verify(clientRepository).save(testClient);
    }

    @Test
    void changePassword_throwsException_onWrongOldPassword() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        assertThrows(IllegalArgumentException.class, () ->
                clientService.changePassword("wrongPass", "newPass"));
    }

    @Test
    void changeEmail_successfullyChangesEmail() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));
        when(clientRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        clientService.changeEmail(testEmail, "new@example.com");

        assertEquals("new@example.com", testClient.getEmail());
        verify(clientRepository).save(testClient);
    }

    @Test
    void changeEmail_throwsException_onEmailMismatch() {
        when(clientRepository.findByEmail(testEmail)).thenReturn(Optional.of(testClient));

        assertThrows(IllegalArgumentException.class, () ->
                clientService.changeEmail("wrong@example.com", "new@example.com"));
    }


}