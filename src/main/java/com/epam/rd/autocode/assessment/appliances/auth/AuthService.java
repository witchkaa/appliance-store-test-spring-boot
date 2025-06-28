package com.epam.rd.autocode.assessment.appliances.auth;

import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authService")
@RequiredArgsConstructor
public class AuthService {

    private final OrdersRepository ordersRepository;

    public boolean isOrderOwner(Long orderId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ordersRepository.findById(orderId)
                .map(order -> order.getClient().getEmail().equals(currentEmail))
                .orElse(false);
    }

    public boolean isEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYEE"));
    }
}