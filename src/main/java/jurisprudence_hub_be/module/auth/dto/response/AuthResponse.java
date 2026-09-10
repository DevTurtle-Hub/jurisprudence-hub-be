package jurisprudence_hub_be.module.auth.dto.response;

public record AuthResponse(
        UserResponse user,
        TokenResponse tokens
) {
}
