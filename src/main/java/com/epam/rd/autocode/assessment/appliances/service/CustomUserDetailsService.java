package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.User;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
}