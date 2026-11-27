package pt.ua.tqs.mober.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.ua.tqs.mober.dto.LoginRequest;
import pt.ua.tqs.mober.dto.RegisterRequest;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.repository.UserRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("André Pereira", "andre@mober.pt", "SecurePass123!", Role.ROLE_COURIER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.email", is("andre@mober.pt")))
                .andExpect(jsonPath("$.role", is("ROLE_COURIER")));
    }

    @Test
    void registerWithInvalidEmailFailsWith400() throws Exception {
        RegisterRequest request = new RegisterRequest("André Pereira", "not-an-email", "SecurePass123!", Role.ROLE_COURIER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email", notNullValue()));
    }

    @Test
    void registerWithBlankNameFailsWith400() throws Exception {
        RegisterRequest request = new RegisterRequest("", "valid@mober.pt", "SecurePass123!", Role.ROLE_COURIER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name", notNullValue()));
    }

    @Test
    void registerWithShortPasswordFailsWith400() throws Exception {
        RegisterRequest request = new RegisterRequest("André Pereira", "andre@mober.pt", "short", Role.ROLE_COURIER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password", notNullValue()));
    }

    @Test
    void registerDuplicateEmailFailsWith409() throws Exception {
        User existingUser = new User("Existente", "andre@mober.pt", passwordEncoder.encode("Pass12345!"), Role.ROLE_COURIER);
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest("Outro Nome", "andre@mober.pt", "Pass12345!", Role.ROLE_COURIER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginSuccess() throws Exception {
        User user = new User("Carlos", "carlos@mober.pt", passwordEncoder.encode("ValidPass123!"), Role.ROLE_OPERATOR);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("carlos@mober.pt", "ValidPass123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.email", is("carlos@mober.pt")))
                .andExpect(jsonPath("$.role", is("ROLE_OPERATOR")));
    }

    @Test
    void loginWithWrongPasswordFailsWith401() throws Exception {
        User user = new User("Carlos", "carlos@mober.pt", passwordEncoder.encode("ValidPass123!"), Role.ROLE_OPERATOR);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("carlos@mober.pt", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
