package jurisprudence_hub_be.module.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }

    public static TokenResponse accessOnly(String accessToken, long expiresIn) {
        return new TokenResponse(accessToken, null, "Bearer", expiresIn);
    }

    public TokenResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer", 900);
    }
}
