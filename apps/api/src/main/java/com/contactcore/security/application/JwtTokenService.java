// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.application;

import com.contactcore.security.config.JwtProperties;
import com.contactcore.shared.api.InvalidRequestException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {};

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Autowired
    public JwtTokenService(JwtProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, Clock.systemUTC());
    }

    JwtTokenService(JwtProperties properties, ObjectMapper objectMapper, Clock clock) {
        if (properties.secret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new InvalidRequestException("JWT secret must contain at least 32 bytes.");
        }
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public String createToken(UserPrincipal principal) {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plusSeconds(properties.ttlMinutes() * 60);
        List<String> roles = principal.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                .toList();
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", properties.issuer());
        claims.put("sub", principal.getUsername());
        claims.put("uid", principal.id());
        claims.put("email", principal.email());
        claims.put("roles", roles);
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());
        return sign(Map.of("alg", "HS256", "typ", "JWT"), claims);
    }

    public String validateAndGetSubject(String token) {
        Map<String, Object> claims = validate(token);
        Object subject = claims.get("sub");
        if (!(subject instanceof String value) || value.isBlank()) {
            throw new InvalidRequestException("Invalid token subject.");
        }
        return value;
    }

    Map<String, Object> validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new InvalidRequestException("Invalid token format.");
            }

            String signedContent = parts[0] + "." + parts[1];
            byte[] expected = hmac(signedContent);
            byte[] actual = URL_DECODER.decode(parts[2]);
            if (!MessageDigest.isEqual(expected, actual)) {
                throw new InvalidRequestException("Invalid token signature.");
            }

            Map<String, Object> claims = objectMapper.readValue(URL_DECODER.decode(parts[1]), CLAIMS_TYPE);
            if (!properties.issuer().equals(claims.get("iss"))) {
                throw new InvalidRequestException("Invalid token issuer.");
            }

            Number expiresAt = (Number) claims.get("exp");
            if (expiresAt == null || Instant.ofEpochSecond(expiresAt.longValue()).isBefore(Instant.now(clock))) {
                throw new InvalidRequestException("Token has expired.");
            }
            return claims;
        } catch (InvalidRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidRequestException("Invalid authentication token.");
        }
    }

    private String sign(Map<String, Object> header, Map<String, Object> claims) {
        try {
            String encodedHeader = URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(header));
            String encodedClaims = URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(claims));
            String signedContent = encodedHeader + "." + encodedClaims;
            return signedContent + "." + URL_ENCODER.encodeToString(hmac(signedContent));
        } catch (Exception ex) {
            throw new InvalidRequestException("Could not create authentication token.");
        }
    }

    private byte[] hmac(String value) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
}
