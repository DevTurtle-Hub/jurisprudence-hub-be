package jurisprudence_hub_be.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Cấu hình DataSource linh hoạt hỗ trợ cả môi trường Local, Docker và Cloud (Render, Railway, Supabase).
 * Tự động chuyển đổi các URL dạng cloud 'postgres://' hoặc 'postgresql://' sang chuẩn JDBC 'jdbc:postgresql://'
 * và tự động trích xuất thông tin username/password nếu có trong URL.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String rawUrl;

    @Value("${spring.datasource.username:postgres}")
    private String defaultUsername;

    @Value("${spring.datasource.password:postgres}")
    private String defaultPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minIdle;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String jdbcUrl = rawUrl != null ? rawUrl.strip() : "";
        String username = defaultUsername != null ? defaultUsername.strip() : "";
        String password = defaultPassword != null ? defaultPassword.strip() : "";

        // Nếu chuỗi kết nối nhận được từ Render / Heroku có dạng: postgres://user:password@host:port/dbname
        // hoặc jdbc:postgresql://user:password@host:port/dbname
        if (jdbcUrl.startsWith("postgres://") || jdbcUrl.startsWith("postgresql://") || (jdbcUrl.startsWith("jdbc:postgresql://") && jdbcUrl.contains("@"))) {
            try {
                // Thay thế tiền tố thành http:// để java.net.URI parse userInfo, host, port chuẩn xác
                String parseableUri = jdbcUrl.replaceFirst("^(jdbc:)?postgres(ql)?://", "http://");
                URI uri = new URI(parseableUri);

                if (uri.getUserInfo() != null) {
                    String[] userParts = uri.getUserInfo().split(":", 2);
                    username = userParts[0].strip();
                    if (userParts.length > 1) {
                        password = userParts[1].strip();
                    }
                }

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath(); // /dbname
                String query = uri.getQuery();

                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + (path != null ? path : "");
                if (query != null && !query.isBlank()) {
                    jdbcUrl += "?" + query;
                }

                log.info("Da tu dong chuyen doi Cloud Database URL sang chuan JDBC: {}", jdbcUrl);
            } catch (Exception e) {
                log.warn("Khong the parse URI cloud postgres, giu nguyen URL: {}", e.getMessage());
            }
        }

        // Tự động bổ sung sslmode=require nếu kết nối tới Neon hoặc cloud Postgres
        if (jdbcUrl.contains("neon.tech") && !jdbcUrl.contains("sslmode=")) {
            jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
        }

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(20000);
        config.setValidationTimeout(5000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setKeepaliveTime(30000);
        config.setLeakDetectionThreshold(60000);

        return new HikariDataSource(config);
    }
}
