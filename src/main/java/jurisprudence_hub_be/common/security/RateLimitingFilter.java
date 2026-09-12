package jurisprudence_hub_be.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jurisprudence_hub_be.common.constant.ErrorCode;
import jurisprudence_hub_be.common.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filter giới hạn tần suất yêu cầu (Rate Limiting) nhằm bảo vệ hệ thống khỏi brute-force và spam DoS:
 * - Ưu tiên sử dụng Redis Atomic Increment phân tán.
 * - Tự động fallback sang In-Memory Counter nếu Redis tạm ngắt kết nối.
 * - Áp dụng riêng cho các endpoint nhạy cảm: /api/v1/auth/login, /api/v1/auth/register, /api/admin/exams/import.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int AUTH_LIMIT_PER_MINUTE = 15;
    private static final int IMPORT_LIMIT_PER_MINUTE = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // Bộ nhớ RAM dự phòng khi Redis không khả dụng
    private final Map<String, RequestCounter> fallbackMemoryCounters = new ConcurrentHashMap<>();

    public RateLimitingFilter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        int limit = resolveLimit(path);

        if (limit <= 0) {
            // Endpoint không yêu cầu rate limiting
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        String rateLimitKey = "ratelimit:" + resolveCategory(path) + ":" + clientIp;

        boolean allowed = checkRateLimit(rateLimitKey, limit);

        if (!allowed) {
            log.warn("Rate limit vượt ngưỡng cho IP [{}] tại endpoint [{}]", clientIp, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ErrorResponse errorResponse = ErrorResponse.of(
                    ErrorCode.RATE_LIMIT_EXCEEDED.getCode(),
                    "Hệ thống phát hiện quá nhiều yêu cầu từ địa chỉ IP của bạn. Vui lòng thử lại sau 1 phút.",
                    path,
                    request.getHeader("X-Request-ID")
            );

            objectMapper.writeValue(response.getOutputStream(), errorResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkRateLimit(String key, int limit) {
        try {
            Long current = redisTemplate.opsForValue().increment(key, 1);
            if (current != null && current == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }
            return current != null && current <= limit;
        } catch (Exception e) {
            // Fallback sang in-memory rate limiting nếu Redis gặp sự cố
            return checkFallbackMemoryLimit(key, limit);
        }
    }

    private boolean checkFallbackMemoryLimit(String key, int limit) {
        long currentWindow = System.currentTimeMillis() / 60000;
        String windowKey = key + ":" + currentWindow;

        RequestCounter counter = fallbackMemoryCounters.computeIfAbsent(windowKey, k -> new RequestCounter());
        int current = counter.count.incrementAndGet();

        // Dọn dẹp các window cũ định kỳ
        if (fallbackMemoryCounters.size() > 500) {
            fallbackMemoryCounters.entrySet().removeIf(entry -> entry.getValue().timestamp < (System.currentTimeMillis() - 120000));
        }

        return current <= limit;
    }

    private int resolveLimit(String path) {
        if (path.contains("/api/v1/auth/login") || path.contains("/api/v1/auth/register")) {
            return AUTH_LIMIT_PER_MINUTE;
        }
        if (path.contains("/api/admin/exams/import")) {
            return IMPORT_LIMIT_PER_MINUTE;
        }
        return 0;
    }

    private String resolveCategory(String path) {
        if (path.contains("/api/v1/auth")) {
            return "auth";
        }
        if (path.contains("/api/admin/exams/import")) {
            return "import";
        }
        return "general";
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    private static class RequestCounter {
        final long timestamp = System.currentTimeMillis();
        final AtomicInteger count = new AtomicInteger(0);
    }
}
