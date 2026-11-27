package pt.ua.tqs.mober.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pt.ua.tqs.mober.dto.AuthResponse;
import pt.ua.tqs.mober.dto.LoginRequest;
import pt.ua.tqs.mober.dto.RegisterRequest;
import pt.ua.tqs.mober.dto.UserResponse;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.repository.UserRepository;
import pt.ua.tqs.mober.security.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("André Pereira", "andre@mober.pt", "encoded_secret", Role.ROLE_COURIER);
        sampleUser.setId(1L);
    }

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("André Pereira", "andre@mober.pt", "SecurePass123!", Role.ROLE_COURIER);

        when(userRepository.existsByEmail("andre@mober.pt")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("encoded_secret");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(sampleUser)).thenReturn("sample.jwt.token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("sample.jwt.token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("andre@mober.pt", response.email());
        assertEquals(Role.ROLE_COURIER, response.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerWithNullRoleDefaultsToCourier() {
        RegisterRequest request = new RegisterRequest("André Pereira", "andre@mober.pt", "SecurePass123!", null);

        when(userRepository.existsByEmail("andre@mober.pt")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(Role.ROLE_COURIER, user.getRole());
            return sampleUser;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("sample.jwt.token");

        AuthResponse response = authService.register(request);
        assertNotNull(response);
    }

    @Test
    void registerDuplicateEmailThrowsException() {
        RegisterRequest request = new RegisterRequest("André Pereira", "andre@mober.pt", "SecurePass123!", Role.ROLE_COURIER);

        when(userRepository.existsByEmail("andre@mober.pt")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSuccess() {
        LoginRequest request = new LoginRequest("andre@mober.pt", "SecurePass123!");

        when(userRepository.findByEmail("andre@mober.pt")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("SecurePass123!", "encoded_secret")).thenReturn(true);
        when(jwtService.generateToken(sampleUser)).thenReturn("sample.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("sample.jwt.token", response.token());
        assertEquals("andre@mober.pt", response.email());
    }

    @Test
    void loginUserNotFoundThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("unknown@mober.pt", "Password123!");

        when(userRepository.findByEmail("unknown@mober.pt")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void loginWrongPasswordThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("andre@mober.pt", "WrongPassword");

        when(userRepository.findByEmail("andre@mober.pt")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", "encoded_secret")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void getProfileSuccess() {
        when(userRepository.findByEmail("andre@mober.pt")).thenReturn(Optional.of(sampleUser));

        UserResponse response = authService.getProfile("andre@mober.pt");

        assertNotNull(response);
        assertEquals("andre@mober.pt", response.email());
        assertEquals(Role.ROLE_COURIER, response.role());
    }

    @Test
    void getProfileNotFoundThrowsException() {
        when(userRepository.findByEmail("unknown@mober.pt")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authService.getProfile("unknown@mober.pt"));
    }
}
