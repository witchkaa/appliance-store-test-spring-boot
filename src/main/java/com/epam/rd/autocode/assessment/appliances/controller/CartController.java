package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MessageSource messageSource;

    @GetMapping
    public String viewCart(Model model) {
        var cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getAppliance().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalAmount", totalAmount);

        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long applianceId, RedirectAttributes redirectAttributes) {
        cartService.addToCart(applianceId, 1);
        redirectAttributes.addFlashAttribute("addedToCart", true);
        return "redirect:/catalog/appliances/" + applianceId;
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long applianceId,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(applianceId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Couldn't remove: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes, Locale locale) {
        try {
            cartService.checkout();
            String successMessage = messageSource.getMessage("cart.checkout.success", null, locale);
            redirectAttributes.addFlashAttribute("message", successMessage);
            return "redirect:/cart";
        } catch (IllegalStateException ex) {
            String errorMessage = messageSource.getMessage("cart.checkout.error", null, locale);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/cart";
        }
    }
}