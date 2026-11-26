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
import pt.ua.tqs.mober.model.UserStatus;
import pt.ua.tqs.mober.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getAllUsersReturnsList() {
        User user2 = new User("Estafeta", "estafeta@mober.pt", "pass", Role.ROLE_COURIER);
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(sampleUser, user2));

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("sofia@mober.pt", users.get(0).email());
        assertEquals("estafeta@mober.pt", users.get(1).email());
    }

    @Test
    void updateUserStatusSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = userService.updateUserStatus(1L, UserStatus.SUSPENDED);

        assertNotNull(response);
        assertEquals(UserStatus.SUSPENDED, sampleUser.getStatus());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void updateUserStatusNotFoundThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserStatus(99L, UserStatus.SUSPENDED));
        verify(userRepository, never()).save(any(User.class));
    }
}
