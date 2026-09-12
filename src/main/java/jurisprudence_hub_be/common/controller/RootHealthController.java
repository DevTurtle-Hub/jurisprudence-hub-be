package jurisprudence_hub_be.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controller phản hồi trang chủ gốc (/) với trạng thái UP để hỗ trợ các công cụ monitor (như UptimeRobot, Render Health Check)
 * luôn nhận được mã HTTP 200 OK kể cả khi gửi GET hoặc HEAD request.
 */
@RestController
public class RootHealthController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "jurisprudence-backend",
                "timestamp", Instant.now().toString()
        ));
    }
}
