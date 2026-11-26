package pt.ua.tqs.mober.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long testExpiration = 3600000; // 1 hour
        jwtService = new JwtService(testSecret, testExpiration);

        user = new User("Carlos Técnico", "carlos@mober.pt", "password", Role.ROLE_OPERATOR);
        user.setId(10L);
    }

    @Test
    void generateTokenAndExtractClaims() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());

        String email = jwtService.extractEmail(token);
        Role role = jwtService.extractRole(token);

        assertEquals("carlos@mober.pt", email);
        assertEquals(Role.ROLE_OPERATOR, role);
    }

    @Test
    void isTokenValidReturnsTrueForMatchingUser() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        String token = jwtService.generateToken(user);

        User anotherUser = new User("Outro", "outro@mober.pt", "pass", Role.ROLE_COURIER);
        anotherUser.setId(20L);

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    void isTokenValidReturnsFalseForMalformedToken() {
        assertFalse(jwtService.isTokenValid("invalid.jwt.token", user));
    }
}
