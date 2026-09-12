package jurisprudence_hub_be.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cấu hình Redis toàn diện cho hệ thống Jurisprudence Hub:
 * - Hỗ trợ RedisTemplate với Jackson Serializer (hỗ trợ Java 21 & Instant/LocalDateTime).
 * - Cấu hình RedisCacheManager với TTL riêng biệt cho từng nghiệp vụ (Exam Draft, Exam Room, Taking, Question Bank, Lessons).
 * - Tích hợp SafeRedisCacheErrorHandler: Nếu Redis gặp sự cố, hệ thống tự động fallback truy vấn trực tiếp từ PostgreSQL,
 *   tuyệt đối không làm gián đoạn request người dùng (Zero 500 downtime).
 */
@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // Thiết lập TTL tối ưu hóa cho từng nghiệp vụ
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        // 1. Thí sinh làm bài thi: Đề thi đã khử đáp án - cache 30 phút phục vụ tải cao
        cacheConfigurations.put("exam_taking", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        // 2. Chi tiết phòng thi & danh sách câu hỏi: cache 2 giờ
        cacheConfigurations.put("exam_rooms", defaultCacheConfig.entryTtl(Duration.ofHours(2)));
        // 3. Bản nháp import PDF: lưu 24 giờ cho admin chỉnh sửa hoàn thiện
        cacheConfigurations.put("exam_drafts", defaultCacheConfig.entryTtl(Duration.ofHours(24)));
        // 4. Ngân hàng câu hỏi: cache 2 giờ
        cacheConfigurations.put("question_bank", defaultCacheConfig.entryTtl(Duration.ofHours(2)));
        // 5. Bài giảng tài liệu: cache 2 giờ
        cacheConfigurations.put("lessons", defaultCacheConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SafeRedisCacheErrorHandler();
    }

    /**
     * CacheErrorHandler an toàn: Bắt toàn bộ ngoại lệ kết nối / timeout Redis để tự động fallback đọc DB.
     */
    public static class SafeRedisCacheErrorHandler implements CacheErrorHandler {

        @Override
        public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Redis GET gặp sự cố tại cache [{}] với key [{}]: {}. Hệ thống tự động fallback trực tiếp từ Database.",
                    cache != null ? cache.getName() : "unknown", key, exception.getMessage());
        }

        @Override
        public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
            log.warn("Redis PUT gặp sự cố tại cache [{}] với key [{}]: {}. Bỏ qua ghi cache.",
                    cache != null ? cache.getName() : "unknown", key, exception.getMessage());
        }

        @Override
        public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
            log.warn("Redis EVICT gặp sự cố tại cache [{}] với key [{}]: {}. Bỏ qua xóa cache.",
                    cache != null ? cache.getName() : "unknown", key, exception.getMessage());
        }

        @Override
        public void handleCacheClearError(RuntimeException exception, Cache cache) {
            log.warn("Redis CLEAR gặp sự cố tại cache [{}]: {}. Bỏ qua dọn dẹp cache.",
                    cache != null ? cache.getName() : "unknown", exception.getMessage());
        }
    }
}
