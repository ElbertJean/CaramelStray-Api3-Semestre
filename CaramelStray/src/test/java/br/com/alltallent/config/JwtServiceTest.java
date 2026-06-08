package br.com.alltallent.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Generate a cryptographically secure key for HS256 algorithm dynamically to avoid hardcoded credentials
        javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String testSecretKey = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
    }

    @Test
    void testGenerateAndExtractToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@test.com");

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("test@test.com", username);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testTokenValidation_DifferentUser() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@test.com");

        String token = jwtService.generateToken(userDetails);

        UserDetails userDetailsOther = mock(UserDetails.class);
        when(userDetailsOther.getUsername()).thenReturn("other@test.com");

        assertFalse(jwtService.isTokenValid(token, userDetailsOther));
    }
}
