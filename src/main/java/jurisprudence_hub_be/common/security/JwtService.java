package jurisprudence_hub_be.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jurisprudence_hub_be.common.constant.ErrorCode;
import jurisprudence_hub_be.common.constant.SecurityConstants;
import jurisprudence_hub_be.common.exception.BusinessException;
import jurisprudence_hub_be.common.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo Access Token cho người dùng
     */
    public String generateAccessToken(UserPrincipal user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.JWT_USER_ID_CLAIM, user.getId());
        claims.put(SecurityConstants.JWT_EMAIL_CLAIM, user.getEmail());
        claims.put(SecurityConstants.CLAIM_NAME, user.getName());
        claims.put(SecurityConstants.CLAIM_ROLE, user.getRole());
        claims.put(SecurityConstants.CLAIM_UNIT, user.getUnit());
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace(SecurityConstants.ROLE_PREFIX, ""))
                .toList();
        claims.put(SecurityConstants.JWT_ROLES_CLAIM, roles);

        return buildToken(claims, user.getUsername(), SecurityConstants.ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * Tạo Refresh Token cho người dùng
     */
    public String generateRefreshToken(UserPrincipal user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.JWT_USER_ID_CLAIM, user.getId());
        claims.put(SecurityConstants.JWT_EMAIL_CLAIM, user.getEmail());
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);

        return buildToken(claims, user.getUsername(), SecurityConstants.REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * Tạo Token tùy biến theo claims, subject và thời gian sống (giây)
     */
    public String buildToken(Map<String, Object> extraClaims, String subject, long expirationSeconds) {
        long nowMillis = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + expirationSeconds * 1000))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Trích xuất username (email) từ token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất userId từ token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.JWT_USER_ID_CLAIM, String.class));
    }

    /**
     * Trích xuất ngày hết hạn của token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất một claim cụ thể
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Trích xuất toàn bộ Claims từ token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra token đã hết hạn chưa
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của token với UserDetails
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của refresh token
     */
    public Claims validateRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException(SecurityConstants.MSG_TOKEN_MISSING);
        }
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class);
            if (!SecurityConstants.TOKEN_TYPE_REFRESH.equalsIgnoreCase(tokenType)) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID, SecurityConstants.MSG_TOKEN_NOT_REFRESH);
            }
            if (claims.getExpiration().before(new Date())) {
                throw new BusinessException(ErrorCode.TOKEN_EXPIRED, SecurityConstants.MSG_REFRESH_TOKEN_EXPIRED);
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, SecurityConstants.MSG_REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, SecurityConstants.MSG_REFRESH_TOKEN_INVALID + e.getMessage());
        }
    }
}
