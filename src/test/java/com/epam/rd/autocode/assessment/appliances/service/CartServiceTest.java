package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.CartItem;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.CartItemRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.impl.CartServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ApplianceService applianceService;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private CartServiceImpl cartService;

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
    void getCartItems_ShouldReturnItemsForCurrentClient() {
        String email = "test@example.com";
        Client client = new Client();
        List<CartItem> items = List.of(new CartItem());

        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(cartItemRepository.findByClient(client)).thenReturn(items);

        List<CartItem> result = cartService.getCartItems();

        assertEquals(items, result);
    }

    @Test
    void getCartItems_NoAuthentication_ShouldThrow() {
        SecurityContextHolder.clearContext();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cartService.getCartItems());
        assertEquals("User is not authenticated", ex.getMessage());
    }

    @Test
    void addToCart_WhenApplianceExistsAndStockSufficient_ShouldUpdateCartAndStock() {
        String email = "test@example.com";
        Client client = new Client();
        Appliance appliance = new Appliance();
        appliance.setQuantityInStock(10);

        CartItem existingCartItem = new CartItem(1L, client, appliance, 2);

        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(applianceService.findById(100L)).thenReturn(Optional.of(appliance));
        when(cartItemRepository.findByClientAndAppliance(client, appliance)).thenReturn(Optional.of(existingCartItem));
        when(applianceService.save(any())).thenReturn(appliance);
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        cartService.addToCart(100L, 3);

        assertEquals(7, appliance.getQuantityInStock());
        assertEquals(5, existingCartItem.getQuantity());
        verify(applianceService).save(appliance);
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void addToCart_WhenApplianceNotFound_ShouldThrow() {
        mockAuthentication("email");
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(new Client()));
        when(applianceService.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.addToCart(1L, 1));
    }

    @Test
    void addToCart_WhenNotEnoughStock_ShouldThrow() {
        mockAuthentication("email");
        Client client = new Client();
        Appliance appliance = new Appliance();
        appliance.setQuantityInStock(1);

        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));
        when(applianceService.findById(anyLong())).thenReturn(Optional.of(appliance));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(1L, 5));
        assertEquals("Appliance is out of stock", ex.getMessage());
    }

    @Test
    void removeFromCart_ShouldRestoreStockAndDeleteCartItem() {
        String email = "test@example.com";
        Client client = new Client();
        Appliance appliance = new Appliance();
        appliance.setQuantityInStock(5);
        CartItem cartItem = new CartItem(1L, client, appliance, 3);

        mockAuthentication(email);
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(client));
        when(applianceService.findById(10L)).thenReturn(Optional.of(appliance));
        when(cartItemRepository.findByClientAndAppliance(client, appliance)).thenReturn(Optional.of(cartItem));
        when(applianceService.save(any())).thenReturn(appliance);

        cartService.removeFromCart(10L);

        assertEquals(8, appliance.getQuantityInStock());
        verify(applianceService).save(appliance);
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeFromCart_WhenCartItemNotFound_ShouldThrow() {
        mockAuthentication("email");
        Client client = new Client();
        Appliance appliance = new Appliance();

        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(client));
        when(applianceService.findById(anyLong())).thenReturn(Optional.of(appliance));
        when(cartItemRepository.findByClientAndAppliance(client, appliance)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.removeFromCart(1L));
    }

}