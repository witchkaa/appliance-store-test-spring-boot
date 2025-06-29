package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.CartItem;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.CartItemRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.CartService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ApplianceService applianceService;
    private final OrderService orderService;
    private final ClientRepository clientRepository;
    private final ClientService clientService;

    private Client getCurrentClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = auth.getName();
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found for email: " + email));
    }

    public List<CartItem> getCartItems() {
        Client client = getCurrentClient();
        return cartItemRepository.findByClient(client);
    }

    @Transactional
    public void addToCart(Long applianceId, int quantity) {
        Client client = getCurrentClient();
        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> new EntityNotFoundException("Appliance not found"));

        if (appliance.getQuantityInStock() < quantity) {
            throw new IllegalArgumentException("Appliance is out of stock");
        }

        CartItem cartItem = cartItemRepository.findByClientAndAppliance(client, appliance)
                .orElse(new CartItem(null, client, appliance, 0));

        appliance.setQuantityInStock(appliance.getQuantityInStock() - quantity);
        applianceService.save(appliance); // Или applianceRepository.save(appliance);

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long applianceId) {
        Client client = getCurrentClient();
        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> new EntityNotFoundException("Appliance not found"));

        CartItem cartItem = cartItemRepository.findByClientAndAppliance(client, appliance)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        appliance.setQuantityInStock(appliance.getQuantityInStock() + cartItem.getQuantity());
        applianceService.save(appliance);

        cartItemRepository.delete(cartItem);
    }
    @Transactional
    public Orders checkout() {
        Client client = clientService.getCurrentClient();
        List<CartItem> items = cartItemRepository.findByClient(client);

        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal total = items.stream()
                .map(item -> item.getAppliance().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!clientService.hasSufficientBalance(total)) {
            throw new IllegalStateException("Not enough balance to place the order");
        }

        Orders order = orderService.createOrderFromCart(items, client);
        clientService.deductBalance(total);

        cartItemRepository.deleteAll(items);

        return order;
    }
}