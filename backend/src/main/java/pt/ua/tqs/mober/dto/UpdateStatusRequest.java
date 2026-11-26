package pt.ua.tqs.mober.dto;

import jakarta.validation.constraints.NotNull;
import pt.ua.tqs.mober.model.UserStatus;

public record UpdateStatusRequest(
    @NotNull(message = "Status is required")
    UserStatus status
) {}
