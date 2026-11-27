package pt.ua.tqs.mober.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.tqs.mober.dto.UserResponse;
import pt.ua.tqs.mober.exception.ResourceNotFoundException;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Sofia Admin", "sofia@mober.pt", "secret", Role.ROLE_ADMIN);
        sampleUser.setId(1L);
    }

    @Test
    void getProfileSuccess() {
        when(userRepository.findByEmail("sofia@mober.pt")).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getProfile("sofia@mober.pt");

        assertNotNull(response);
        assertEquals("sofia@mober.pt", response.email());
        assertEquals(Role.ROLE_ADMIN, response.role());
    }

    @Test
    void getProfileNotFoundThrowsException() {
        when(userRepository.findByEmail("notfound@mober.pt")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile("notfound@mober.pt"));
    }
}
