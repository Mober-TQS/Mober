package pt.ua.tqs.mober.service;

import org.springframework.stereotype.Service;
import pt.ua.tqs.mober.dto.UserResponse;
import pt.ua.tqs.mober.exception.ResourceNotFoundException;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.repository.UserRepository;

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
}
