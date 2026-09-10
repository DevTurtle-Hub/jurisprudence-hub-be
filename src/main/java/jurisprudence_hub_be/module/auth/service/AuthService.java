package jurisprudence_hub_be.module.auth.service;

import jurisprudence_hub_be.module.auth.dto.request.LoginRequest;
import jurisprudence_hub_be.module.auth.dto.request.RefreshTokenRequest;
import jurisprudence_hub_be.module.auth.dto.request.RegisterRequest;
import jurisprudence_hub_be.module.auth.dto.response.AuthResponse;
import jurisprudence_hub_be.module.auth.dto.response.TokenResponse;
import jurisprudence_hub_be.module.auth.dto.response.UserProfileResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getMe();

    TokenResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
