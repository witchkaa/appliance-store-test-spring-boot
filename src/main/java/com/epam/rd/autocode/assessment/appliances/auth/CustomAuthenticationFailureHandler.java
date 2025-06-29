package com.epam.rd.autocode.assessment.appliances.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorKey = "login.error.invalid";

        if (exception instanceof LockedException) {
            errorKey = "login.error.locked";
        }

        request.getSession().setAttribute("loginErrorMessage", errorKey);
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}