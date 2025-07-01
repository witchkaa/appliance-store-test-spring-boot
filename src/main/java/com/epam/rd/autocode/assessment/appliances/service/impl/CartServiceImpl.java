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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
            log.warn("Attempt to get current client but user is not authenticated");
            throw new RuntimeException("User is not authenticated");
        }

        String email = auth.getName();
        log.debug("Getting current client by email: {}", email);
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Client not found for email: {}", email);
                    return new RuntimeException("Client not found for email: " + email);
                });
    }

    public List<CartItem> getCartItems() {
        Client client = getCurrentClient();
        log.debug("Fetching cart items for client id: {}", client.getId());
        return cartItemRepository.findByClient(client);
    }

    @Transactional
    public void addToCart(Long applianceId, int quantity) {
        Client client = getCurrentClient();
        log.info("Adding appliance with id {} to cart of client id {} with quantity {}", applianceId, client.getId(), quantity);

        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> {
                    log.error("Appliance not found with id: {}", applianceId);
                    return new EntityNotFoundException("Appliance not found");
                });

        if (appliance.getQuantityInStock() < quantity) {
            log.warn("Not enough stock for appliance id {}. Requested: {}, Available: {}", applianceId, quantity, appliance.getQuantityInStock());
            throw new IllegalArgumentException("Appliance is out of stock");
        }

        CartItem cartItem = cartItemRepository.findByClientAndAppliance(client, appliance)
                .orElse(new CartItem(null, client, appliance, 0));

        appliance.setQuantityInStock(appliance.getQuantityInStock() - quantity);
        applianceService.save(appliance);

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);

        log.info("Added appliance id {} to client id {} cart. New quantity: {}", applianceId, client.getId(), cartItem.getQuantity());
    }

    @Transactional
    public void removeFromCart(Long applianceId) {
        Client client = getCurrentClient();
        log.info("Removing appliance with id {} from client id {} cart", applianceId, client.getId());

        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> {
                    log.error("Appliance not found with id: {}", applianceId);
                    return new EntityNotFoundException("Appliance not found");
                });

        CartItem cartItem = cartItemRepository.findByClientAndAppliance(client, appliance)
                .orElseThrow(() -> {
                    log.error("Cart item not found for client id {} and appliance id {}", client.getId(), applianceId);
                    return new EntityNotFoundException("Cart item not found");
                });

        appliance.setQuantityInStock(appliance.getQuantityInStock() + cartItem.getQuantity());
        applianceService.save(appliance);

        cartItemRepository.delete(cartItem);

        log.info("Removed appliance id {} from client id {} cart", applianceId, client.getId());
    }

    @Transactional
    public Orders checkout() {
        Client client = clientService.getCurrentClient();
        log.info("Client id {} starting checkout", client.getId());

        List<CartItem> items = cartItemRepository.findByClient(client);

        if (items.isEmpty()) {
            log.warn("Checkout failed: cart is empty for client id {}", client.getId());
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal total = items.stream()
                .map(item -> item.getAppliance().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Total order amount for client id {}: {}", client.getId(), total);

        if (!clientService.hasSufficientBalance(total)) {
            log.warn("Checkout failed: insufficient balance for client id {}. Required: {}", client.getId(), total);
            throw new IllegalStateException("Not enough balance to place the order");
        }

        Orders order = orderService.createOrderFromCart(items, client);
        clientService.deductBalance(total);

        cartItemRepository.deleteAll(items);

        log.info("Checkout successful for client id {}. Order id: {}", client.getId(), order.getId());
        return order;
    }
}