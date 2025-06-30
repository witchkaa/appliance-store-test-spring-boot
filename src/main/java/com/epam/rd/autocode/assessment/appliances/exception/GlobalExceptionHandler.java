package com.epam.rd.autocode.assessment.appliances.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String getLocalizedMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    @ExceptionHandler(ApplianceNotFoundException.class)
    public String handleApplianceNotFound(ApplianceNotFoundException ex, Model model) {
        log.warn("Handled exception: {}", ex.getMessage());
        Long id = extractIdFromMessage(ex.getMessage());
        String message = getLocalizedMessage("error.appliance.notfound", id);
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public String handleClientNotFound(ClientNotFoundException ex, Model model) {
        log.warn("Handled exception: {}", ex.getMessage());
        Long id = extractIdFromMessage(ex.getMessage());
        String message = getLocalizedMessage("error.client.notfound", id);
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public String handleEmployeeNotFound(EmployeeNotFoundException ex, Model model) {
        log.warn("Handled exception: {}", ex.getMessage());
        Long id = extractIdFromMessage(ex.getMessage());
        String message = getLocalizedMessage("error.employee.notfound", id);
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(ManufacturerNotFoundException.class)
    public String handleManufacturerNotFound(ManufacturerNotFoundException ex, Model model) {
        log.warn("Manufacturer not found: {}", ex.getMessage());
        Long id = extractIdFromMessage(ex.getMessage());
        String message = getLocalizedMessage("error.manufacturer.notfound", id);
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(ManufacturerDeleteException.class)
    public String handleManufacturerDeleteException(ManufacturerDeleteException ex, Model model) {
        log.warn("Manufacturer delete error: {}", ex.getMessage());
        String message = getLocalizedMessage("error.manufacturer.delete", ex.getMessage());
        model.addAttribute("status", HttpStatus.BAD_REQUEST);
        model.addAttribute("error", "Bad Request");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public String handleOrderNotFound(OrderNotFoundException ex, Model model) {
        log.warn("Order not found: {}", ex.getMessage());
        Long id = extractIdFromMessage(ex.getMessage());
        String message = getLocalizedMessage("error.order.notfound", id);
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(UnauthorizedOrderAccessException.class)
    public String handleUnauthorizedOrderAccess(UnauthorizedOrderAccessException ex, Model model) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        String message = getLocalizedMessage("error.order.access");
        model.addAttribute("status", HttpStatus.FORBIDDEN);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", message);
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        log.error("Unhandled exception", ex);
        String message = getLocalizedMessage("error.generic");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", message);
        return "error/error";
    }

    private Long extractIdFromMessage(String message) {
        if (message == null) return null;
        try {
            String[] parts = message.split(" ");
            for (String part : parts) {
                if (part.matches("\\d+")) {
                    return Long.parseLong(part);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract id from exception message: {}", message);
        }
        return null;
    }
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("Access denied: {}", ex.getMessage());
        String message = getLocalizedMessage("error.access.denied");
        model.addAttribute("status", HttpStatus.FORBIDDEN);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", message);
        return "error/error";
    }
}
