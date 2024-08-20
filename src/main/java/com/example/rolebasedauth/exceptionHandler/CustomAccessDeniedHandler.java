package com.example.rolebasedauth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        // Get the current timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Create the JSON response
        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"error\": \"Access Denied. You do not have the required permissions to access this resource.\"}",
                timestamp
        );

        // Write the JSON response
        response.getWriter().write(jsonResponse);
    }
}
