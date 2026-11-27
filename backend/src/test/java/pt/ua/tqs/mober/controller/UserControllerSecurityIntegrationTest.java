package pt.ua.tqs.mober.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.repository.UserRepository;
import pt.ua.tqs.mober.security.JwtService;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String courierToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User courierUser = new User("André Estafeta", "estafeta@mober.pt", passwordEncoder.encode("Password123!"), Role.ROLE_COURIER);
        courierUser = userRepository.save(courierUser);
        courierToken = "Bearer " + jwtService.generateToken(courierUser);
    }

    @Test
    void getMeWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMeWithCourierTokenReturnsProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("estafeta@mober.pt")))
                .andExpect(jsonPath("$.role", is("ROLE_COURIER")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getAllUsersWithCourierTokenReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", courierToken))
                .andExpect(status().isForbidden());
    }
}
