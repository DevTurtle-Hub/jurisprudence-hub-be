package jurisprudence_hub_be.module.auth.dto.response;

import java.time.Instant;

public record UserProfileResponse(
        String id,
        String email,
        String name,
        String role,
        String unit,
        String avatarUrl,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public UserProfileResponse(String id, String name, String email, String role, String unit, String avatarUrl, Instant createdAt) {
        this(id, email, name, role, unit, avatarUrl, true, createdAt, createdAt);
    }
}
