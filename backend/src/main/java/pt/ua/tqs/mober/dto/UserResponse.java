package pt.ua.tqs.mober.dto;

import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;
import pt.ua.tqs.mober.model.UserStatus;

import java.time.Instant;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    UserStatus status,
    Instant createdAt
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
