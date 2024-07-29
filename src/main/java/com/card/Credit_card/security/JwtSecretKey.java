package com.card.Credit_card.security;


import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;


@Configuration
@RequiredArgsConstructor
public class JwtSecretKey {
    private final JwtConfig jwtConfig;

    /**
     * Secret key secret key.
     *
     * @return the secret key
     */
    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }
}
