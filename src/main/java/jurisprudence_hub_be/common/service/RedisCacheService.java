package jurisprudence_hub_be.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

/**
 * Service tiện ích thao tác với Redis Cache an toàn:
 * - Tự động bọc try-catch để nếu Redis tạm ngắt kết nối thì ứng dụng vẫn hoạt động bình thường qua DB.
 * - Hỗ trợ get/set với kiểu dữ liệu generic và Jackson ObjectMapper conversion.
 */
@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String key, Class<T> targetClass) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            if (targetClass.isInstance(value)) {
                return targetClass.cast(value);
            }
            return objectMapper.convertValue(value, targetClass);
        } catch (Exception e) {
            log.warn("Lỗi khi đọc Redis key [{}]: {}. Fallback dữ liệu DB.", key, e.getMessage());
            return null;
        }
    }

    public void set(String key, Object value, Duration duration) {
        try {
            if (duration != null && !duration.isZero() && !duration.isNegative()) {
                redisTemplate.opsForValue().set(key, value, duration);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
        } catch (Exception e) {
            log.warn("Lỗi khi ghi Redis key [{}]: {}. Bỏ qua ghi cache.", key, e.getMessage());
        }
    }

    public void set(String key, Object value) {
        set(key, value, Duration.ofHours(1));
    }

    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("Lỗi khi xóa Redis key [{}]: {}.", key, e.getMessage());
            return false;
        }
    }

    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Lỗi khi xóa Redis pattern [{}]: {}.", pattern, e.getMessage());
        }
    }

    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("Lỗi khi kiểm tra Redis key [{}]: {}.", key, e.getMessage());
            return false;
        }
    }
}
