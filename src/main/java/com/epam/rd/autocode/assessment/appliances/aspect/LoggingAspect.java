package com.epam.rd.autocode.assessment.appliances.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.epam.rd.autocode.assessment.appliances.service..*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String username = "anonymous";
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            username = auth.getName();
        }

        String methodName = joinPoint.getSignature().toShortString();
        String argsJson;
        try {
            argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
        } catch (Exception e) {
            argsJson = Arrays.toString(joinPoint.getArgs());
        }

        log.info("User: {}, Entering method: {} with args: {}", username, methodName, argsJson);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("User: {}, Method {} executed in {} ms with result: {}", username, methodName, duration, result);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("User: {}, Method {} failed in {} ms with exception: {}", username, methodName, duration, ex.toString());
            throw ex;
        }
    }
}