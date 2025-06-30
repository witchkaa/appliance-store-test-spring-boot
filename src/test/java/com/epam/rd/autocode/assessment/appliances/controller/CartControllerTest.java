package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.CartItem;
import com.epam.rd.autocode.assessment.appliances.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CartController controller;

    @Test
    void viewCart_ShouldAddItemsAndTotalAmountToModel() {
        Appliance appliance1 = new Appliance();
        appliance1.setPrice(new BigDecimal("10.5"));
        Appliance appliance2 = new Appliance();
        appliance2.setPrice(new BigDecimal("20"));

        CartItem item1 = new CartItem();
        item1.setAppliance(appliance1);
        item1.setQuantity(2);  // 21.0

        CartItem item2 = new CartItem();
        item2.setAppliance(appliance2);
        item2.setQuantity(1);  // 20.0

        List<CartItem> cartItems = List.of(item1, item2);

        when(cartService.getCartItems()).thenReturn(cartItems);

        String view = controller.viewCart(model);

        verify(model).addAttribute("cartItems", cartItems);
        verify(model).addAttribute("totalAmount", new BigDecimal("41.0"));
        assertEquals("cart/view", view);
    }

    @Test
    void addToCart_ShouldAddFlashAttributeAndRedirect() {
        Long applianceId = 7L;

        String view = controller.addToCart(applianceId, redirectAttributes);

        verify(cartService).addToCart(applianceId, 1);
        verify(redirectAttributes).addFlashAttribute("addedToCart", true);
        assertEquals("redirect:/catalog/appliances/" + applianceId, view);
    }

    @Test
    void removeFromCart_Success_ShouldRedirectWithoutError() {
        Long applianceId = 5L;

        String view = controller.removeFromCart(applianceId, redirectAttributes);

        verify(cartService).removeFromCart(applianceId);
        verifyNoInteractions(redirectAttributes); // no error flash attribute added
        assertEquals("redirect:/cart", view);
    }

    @Test
    void removeFromCart_Exception_ShouldAddErrorFlashAndRedirect() {
        Long applianceId = 5L;
        String errorMsg = "item not found";

        doThrow(new RuntimeException(errorMsg)).when(cartService).removeFromCart(applianceId);

        String view = controller.removeFromCart(applianceId, redirectAttributes);

        verify(cartService).removeFromCart(applianceId);
        verify(redirectAttributes).addFlashAttribute("error", "Couldn't remove: " + errorMsg);
        assertEquals("redirect:/cart", view);
    }

    @Test
    void checkout_Success_ShouldAddSuccessMessageAndRedirect() {
        Locale locale = Locale.ENGLISH;
        String successMsg = "Checkout successful!";

        when(messageSource.getMessage("cart.checkout.success", null, locale)).thenReturn(successMsg);

        String view = controller.checkout(redirectAttributes, locale);

        verify(cartService).checkout();
        verify(messageSource).getMessage("cart.checkout.success", null, locale);
        verify(redirectAttributes).addFlashAttribute("message", successMsg);
        assertEquals("redirect:/cart", view);
    }

    @Test
    void checkout_IllegalStateException_ShouldAddErrorMessageAndRedirect() {
        Locale locale = Locale.ENGLISH;
        String errorMsg = "Checkout error";

        doThrow(new IllegalStateException()).when(cartService).checkout();
        when(messageSource.getMessage("cart.checkout.error", null, locale)).thenReturn(errorMsg);

        String view = controller.checkout(redirectAttributes, locale);

        verify(cartService).checkout();
        verify(messageSource).getMessage("cart.checkout.error", null, locale);
        verify(redirectAttributes).addFlashAttribute("error", errorMsg);
        assertEquals("redirect:/cart", view);
    }
}