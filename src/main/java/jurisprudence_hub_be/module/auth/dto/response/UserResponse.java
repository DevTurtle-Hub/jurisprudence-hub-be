package jurisprudence_hub_be.module.auth.dto.response;

import java.time.Instant;

public record UserResponse(
        String id,
        String name,
        String email,
        String role,
        String unit,
        Instant createdAt,
        Instant loggedInAt
) {
}
