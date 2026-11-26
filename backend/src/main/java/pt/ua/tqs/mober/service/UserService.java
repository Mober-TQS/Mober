package pt.ua.tqs.mober.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ua.tqs.mober.dto.UserResponse;
import pt.ua.tqs.mober.exception.ResourceNotFoundException;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.model.UserStatus;
import pt.ua.tqs.mober.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return UserResponse.fromUser(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, UserStatus newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(newStatus);
        User updated = userRepository.save(user);
        return UserResponse.fromUser(updated);
    }
}
