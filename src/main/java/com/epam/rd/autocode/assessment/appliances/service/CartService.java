package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.CartItem;
import com.epam.rd.autocode.assessment.appliances.model.Orders;

import java.util.List;

public interface CartService {

    List<CartItem> getCartItems();

    void addToCart(Long applianceId, int quantity);

    void removeFromCart(Long applianceId);
    Orders checkout();
}
