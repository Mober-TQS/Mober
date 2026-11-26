package pt.ua.tqs.mober.dto;

import pt.ua.tqs.mober.model.Role;

public record AuthResponse(
    String token,
    String tokenType,
    Long id,
    String name,
    String email,
    Role role
) {
    public AuthResponse(String token, Long id, String name, String email, Role role) {
        this(token, "Bearer", id, name, email, role);
    }
}
