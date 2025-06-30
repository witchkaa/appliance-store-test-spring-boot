package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.auth.UserPrincipal;
import com.epam.rd.autocode.assessment.appliances.model.User;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<? extends User> client = clientRepository.findByEmail(email);
        Optional<? extends User> employee = employeeRepository.findByEmail(email);

        User user = client.map(c -> (User) c)
                .or(() -> employee.map(e -> (User) e))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (user.getRole() == null) {
            throw new IllegalStateException("User role is null: " + email);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    @PostConstruct
    public void debugAllUsers() {
        System.out.println("==== CLIENTS ====");
        clientRepository.findAll().forEach(user ->
                System.out.println(user.getEmail() + " / " + user.getPassword() + " / " + user.getRole())
        );

        System.out.println("==== EMPLOYEES ====");
        employeeRepository.findAll().forEach(user ->
                System.out.println(user.getEmail() + " / " + user.getPassword() + " / " + user.getRole())
        );
    }
    public UserPrincipal loadUserById(Long userId) throws UsernameNotFoundException {
        User user = findUserById(userId);
        return createUserPrincipal(user);
    }
    private UserPrincipal createUserPrincipal(User user) {
        if (user.getRole() == null) {
            throw new IllegalStateException("User role is null: " + user.getEmail());
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    private User findUserById(Long userId) {
        return clientRepository.findById(userId)
                .map(c -> (User) c)
                .or(() -> employeeRepository.findById(userId).map(e -> (User) e))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

}