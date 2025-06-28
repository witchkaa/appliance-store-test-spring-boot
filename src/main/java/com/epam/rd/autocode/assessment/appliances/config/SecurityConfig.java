package com.epam.rd.autocode.assessment.appliances.config;

import com.epam.rd.autocode.assessment.appliances.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/reset/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/locale", "/login").permitAll()
                        .requestMatchers("/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers("/clients/**").hasRole("EMPLOYEE")
                        .requestMatchers("/appliances/**").hasRole("EMPLOYEE")
                        .requestMatchers("/appliances").hasAnyRole("EMPLOYEE", "CLIENT")
                        .requestMatchers("/manufacturers/**").hasRole("EMPLOYEE")
                        .requestMatchers("/orders/approve/**", "/orders/unapproved/**").hasRole("EMPLOYEE")
                        .requestMatchers("/orders/edit/**", "/orders/delete/**", "/orders/add/**", "/orders/add-order/**").hasRole("CLIENT")
                        .requestMatchers("/orders/**").hasAnyRole("EMPLOYEE", "CLIENT")
                        .requestMatchers("/catalog", "/catalog/**").hasRole("CLIENT")
                        .requestMatchers("/cart", "/cart/**").hasRole("CLIENT")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            request.setAttribute("message", "error.forbidden");
                            request.getRequestDispatcher("/error/403").forward(request, response);
                        })
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }
}