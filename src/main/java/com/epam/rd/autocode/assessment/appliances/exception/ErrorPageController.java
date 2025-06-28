package com.epam.rd.autocode.assessment.appliances.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class ErrorPageController {

    private final MessageSource messageSource;

    public ErrorPageController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/error/403")
    public String accessDeniedPage(Model model, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = messageSource.getMessage(
                (String) request.getAttribute("message"), null, locale);
        model.addAttribute("status", HttpStatus.FORBIDDEN);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", localizedMessage);
        return "error/error";
    }
}