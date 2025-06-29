package com.epam.rd.autocode.assessment.appliances.auth;

import com.epam.rd.autocode.assessment.appliances.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("login.error.locked");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String password = authentication.getCredentials().toString();

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            loginAttemptService.loginSucceeded(username);
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            loginAttemptService.loginFailed(username);
            throw new BadCredentialsException("login.error.invalid");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}