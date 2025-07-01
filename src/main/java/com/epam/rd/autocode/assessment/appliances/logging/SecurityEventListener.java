package com.epam.rd.autocode.assessment.appliances.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        Authentication auth = event.getAuthentication();
        String username = auth.getName();

        if (event instanceof AuthenticationSuccessEvent) {
            log.info("Authentication success for user: {}", username);
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            log.warn("Authentication failed for user: {} (bad credentials)", username);
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            log.warn("Authentication failed (account locked): {}", username);
        } else if (event instanceof AuthenticationFailureDisabledEvent) {
            log.warn("Authentication failed (account disabled): {}", username);
        } else if (event instanceof AuthenticationFailureExpiredEvent) {
            log.warn("Authentication failed (account expired): {}", username);
        } else if (event instanceof InteractiveAuthenticationSuccessEvent) {
            log.info("Interactive login success for user: {}", username);
        } else {
            log.debug("⚠Other security event: {} for user {}", event.getClass().getSimpleName(), username);
        }
    }
}