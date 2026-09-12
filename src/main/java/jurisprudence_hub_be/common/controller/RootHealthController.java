package jurisprudence_hub_be.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controller phản hồi trang chủ gốc (/) với trạng thái UP để hỗ trợ các công cụ monitor (như UptimeRobot)
 * luôn nhận được mã HTTP 200 OK kể cả khi quên thêm đuôi /actuator/health.
 */
@RestController
public class RootHealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "jurisprudence-backend",
                "timestamp", Instant.now().toString()
        ));
    }
}
