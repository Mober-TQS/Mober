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
import pt.ua.tqs.mober.dto.UpdateStatusRequest;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.model.UserStatus;
import pt.ua.tqs.mober.repository.UserRepository;
import pt.ua.tqs.mober.security.JwtService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User courierUser;
    private User adminUser;
    private String courierToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        courierUser = new User("André Estafeta", "estafeta@mober.pt", passwordEncoder.encode("Password123!"), Role.ROLE_COURIER);
        courierUser = userRepository.save(courierUser);
        courierToken = "Bearer " + jwtService.generateToken(courierUser);

        adminUser = new User("Sofia Admin", "admin@mober.pt", passwordEncoder.encode("Password123!"), Role.ROLE_ADMIN);
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtService.generateToken(adminUser);
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

    @Test
    void getAllUsersWithAdminTokenReturnsUserList() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", hasItems("estafeta@mober.pt", "admin@mober.pt")));
    }

    @Test
    void updateUserStatusWithCourierTokenReturnsForbidden() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(UserStatus.SUSPENDED);

        mockMvc.perform(patch("/api/users/" + courierUser.getId() + "/status")
                        .header("Authorization", courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserStatusWithAdminTokenSuccess() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(UserStatus.SUSPENDED);

        mockMvc.perform(patch("/api/users/" + courierUser.getId() + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(courierUser.getId().intValue())))
                .andExpect(jsonPath("$.status", is("SUSPENDED")));
    }
}
