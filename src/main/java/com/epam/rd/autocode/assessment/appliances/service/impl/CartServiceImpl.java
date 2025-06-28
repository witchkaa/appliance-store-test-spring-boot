package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.CartItem;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.CartItemRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.CartService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ApplianceService applianceService;
    private final OrderService orderService;
    private final ClientRepository clientRepository;

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

    public void addToCart(Long applianceId, int quantity) {
        Client client = getCurrentClient();
        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> new EntityNotFoundException("Appliance not found"));

        CartItem cartItem = cartItemRepository.findByClientAndAppliance(client, appliance)
                .orElse(new CartItem(null, client, appliance, 0));

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
    }
    @Transactional
    public void removeFromCart(Long applianceId) {
        Client client = getCurrentClient();
        Appliance appliance = applianceService.findById(applianceId)
                .orElseThrow(() -> new EntityNotFoundException("Appliance not found"));
        cartItemRepository.deleteByClientAndAppliance(client, appliance);
    }
    @Transactional
    public Orders checkout() {
        Client client = getCurrentClient();
        List<CartItem> items = cartItemRepository.findByClient(client);
        if (items.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        Orders order = orderService.createOrderFromCart(items, client);

        cartItemRepository.deleteAll(items);

        return order;
    }
}