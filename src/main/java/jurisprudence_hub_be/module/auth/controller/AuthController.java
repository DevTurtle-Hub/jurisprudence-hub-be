package jurisprudence_hub_be.module.auth.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.auth.constant.AuthConstant;
import jurisprudence_hub_be.module.auth.dto.request.LoginRequest;
import jurisprudence_hub_be.module.auth.dto.request.RefreshTokenRequest;
import jurisprudence_hub_be.module.auth.dto.request.RegisterRequest;
import jurisprudence_hub_be.module.auth.dto.response.AuthResponse;
import jurisprudence_hub_be.module.auth.dto.response.TokenResponse;
import jurisprudence_hub_be.module.auth.dto.response.UserProfileResponse;
import jurisprudence_hub_be.module.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(AuthConstant.MSG_REGISTER_SUCCESS, response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(AuthConstant.MSG_LOGIN_SUCCESS, response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        UserProfileResponse profile = authService.getMe();
        return ResponseEntity.ok(ApiResponse.success(AuthConstant.MSG_GET_ME_SUCCESS, profile));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokenResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(AuthConstant.MSG_REFRESH_TOKEN_SUCCESS, tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(AuthConstant.MSG_LOGOUT_SUCCESS, null));
    }
}
