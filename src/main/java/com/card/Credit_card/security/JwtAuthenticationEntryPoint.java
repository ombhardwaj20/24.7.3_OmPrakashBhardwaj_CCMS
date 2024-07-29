package com.card.Credit_card.security;

import com.card.Credit_card.model.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@Slf4j

public  class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //@Override
    public void commence(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException) throws IOException, jakarta.servlet.ServletException {
        logger.trace("Entered commence");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Throwable authenticationException = new Throwable();
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED,
                authenticationException.getMessage(), "Unauthorized");
        final ObjectMapper mapper = new ObjectMapper();
        logger.trace("Exited commence");
        mapper.writeValue(response.getOutputStream(), errorDetails);
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

    }
}
