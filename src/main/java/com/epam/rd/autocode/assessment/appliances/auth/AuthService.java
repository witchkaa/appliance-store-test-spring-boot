package com.epam.rd.autocode.assessment.appliances.auth;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Role;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("authService")
@RequiredArgsConstructor
public class AuthService {

    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;

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
    public boolean isOwnerOrEmployee(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Client> optionalClient = clientRepository.findByEmail(email);
        if (optionalClient.isEmpty()) return false;

        Client currentClient = optionalClient.get();
        if (currentClient.getRole() == Role.EMPLOYEE) return true;

        return ordersRepository.findById(orderId)
                .map(order -> order.getClient().getId().equals(currentClient.getId()))
                .orElse(false);
    }
}
