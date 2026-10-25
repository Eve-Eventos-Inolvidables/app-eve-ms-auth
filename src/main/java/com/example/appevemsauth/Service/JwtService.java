package com.example.appevemsauth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.algorithm}")
    private String algorithm;

    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    @Value("${jwt.issuer:}")
    private String issuer;

    @Value("${jwt.claim.user}")
    private String userClaim;

    @Value("${jwt.claim.roles}")
    private String rolesClaim;

    private SecretKey key;
    private MacAlgorithm macAlgorithm;

    @PostConstruct
    void init() {
        int requiredBits;
        switch (algorithm) {
            case "HS256" -> { macAlgorithm = Jwts.SIG.HS256; requiredBits = 256; }
            case "HS384" -> { macAlgorithm = Jwts.SIG.HS384; requiredBits = 384; }
            case "HS512" -> { macAlgorithm = Jwts.SIG.HS512; requiredBits = 512; }
            default -> throw new IllegalStateException("jwt.algorithm debe ser HS256, HS384 o HS512");
        }
        if (expirationMinutes <= 0) {
            throw new IllegalStateException("jwt.expiration-minutes debe ser mayor a 0");
        }
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (key.getEncoded().length * 8 < requiredBits) {
            throw new IllegalStateException(
                    "jwt.secret es demasiado corto para " + algorithm + " (mínimo " + requiredBits + " bits)");
        }
    }

    public String generate(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim(userClaim, userId)
                .claim(rolesClaim, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)));
        if (!issuer.isBlank()) {
            builder.issuer(issuer);
        }
        return builder.signWith(key, macAlgorithm).compact();
    }

    public Optional<Claims> validate(String token) {
        try {
            JwtParserBuilder parser = Jwts.parser().verifyWith(key);
            if (!issuer.isBlank()) {
                parser.requireIssuer(issuer);
            }
            Jws<Claims> jws = parser.build().parseSignedClaims(token);
            if (!algorithm.equals(jws.getHeader().getAlgorithm())) {
                return Optional.empty();
            }
            return Optional.of(jws.getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token inválido: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
