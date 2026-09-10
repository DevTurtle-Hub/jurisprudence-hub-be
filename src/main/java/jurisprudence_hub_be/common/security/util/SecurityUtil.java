package jurisprudence_hub_be.common.security.util;

import jurisprudence_hub_be.common.constant.SecurityConstants;
import jurisprudence_hub_be.common.exception.UnauthorizedException;
import jurisprudence_hub_be.common.security.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<Authentication> getAuthentication() {
        if (SecurityContextHolder.getContext() == null) {
            return Optional.empty();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of(authentication);
    }

    public static Optional<UserPrincipal> getCurrentPrincipal() {
        return getAuthentication().flatMap(authentication -> {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                return Optional.of(userPrincipal);
            }
            return Optional.empty();
        });
    }

    public static Optional<String> getCurrentUserId() {
        return getCurrentPrincipal().map(UserPrincipal::getId);
    }

    public static String requireCurrentUserId() {
        return getCurrentUserId().orElseThrow(UnauthorizedException::new);
    }

    public static Optional<UUID> getCurrentUserIdAsUuid() {
        return getCurrentUserId().flatMap(id -> {
            try {
                return Optional.of(UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
    }

    public static Optional<String> getCurrentUserEmail() {
        return getCurrentPrincipal().map(UserPrincipal::getEmail);
    }

    public static String requireCurrentEmail() {
        return getCurrentUserEmail().orElseThrow(UnauthorizedException::new);
    }

    public static boolean isAuthenticated() {
        return getAuthentication().isPresent();
    }

    public static boolean hasRole(String role) {
        if (role == null || role.isBlank()) {
            return false;
        }

        String targetRole = role.startsWith(SecurityConstants.ROLE_PREFIX)
                ? role
                : SecurityConstants.ROLE_PREFIX + role;

        return getAuthentication()
                .map(authentication -> {
                    if (authentication.getAuthorities() == null) {
                        return false;
                    }
                    return authentication.getAuthorities()
                            .stream()
                            .anyMatch(authority ->
                                    authority != null &&
                                    authority.getAuthority() != null &&
                                    authority.getAuthority().equalsIgnoreCase(targetRole)
                            );
                })
                .orElse(false);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static Optional<String> getCurrentUserRole() {
        return getCurrentPrincipal().map(UserPrincipal::getRole);
    }
}
