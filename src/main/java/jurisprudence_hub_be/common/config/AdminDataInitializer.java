package jurisprudence_hub_be.common.config;

import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.auth.enums.Role;
import jurisprudence_hub_be.module.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.default-admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${app.default-admin.password:admin123}")
    private String adminPassword;

    @Value("${app.default-admin.name:Quản Trị Viên CAND}")
    private String adminName;

    @Value("${app.default-admin.unit:Cục Đào tạo - Bộ Công An}")
    private String adminUnit;

    @Value("${app.default-admin.enabled:true}")
    private boolean adminInitEnabled;

    @Override
    public void run(String... args) {
        if (!adminInitEnabled) {
            log.info(">>> [AdminDataInitializer] Tính năng khởi tạo admin mặc định đã tắt (app.default-admin.enabled=false). Bỏ qua.");
            return;
        }

        String normalizedEmail = adminEmail.toLowerCase(Locale.ROOT).trim();

        Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isEmpty()) {
            User admin = new User();
            admin.setId("usr-admin-001");
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setName(adminName != null ? adminName.trim() : "Quản Trị Viên CAND");
            admin.setRole(Role.ADMIN);
            admin.setUnit(adminUnit != null ? adminUnit.trim() : "Cục Đào tạo - Bộ Công An");
            admin.setActive(true);
            admin.setCreatedAt(Instant.now());
            admin.setUpdatedAt(Instant.now());
            userRepository.save(admin);
            log.info(">>> [AdminDataInitializer] Khởi tạo tài khoản Admin mặc định thành công: {} (Quyền: {})", normalizedEmail, Role.ADMIN);
        } else {
            User user = existingUser.get();
            boolean needSave = false;

            // Đảm bảo có role ADMIN
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                needSave = true;
            }

            // Đảm bảo tài khoản luôn active
            if (!user.isActive()) {
                user.setActive(true);
                needSave = true;
            }

            // Sửa lại hash BCrypt nếu chuỗi hash cũ không khớp với mật khẩu chỉ định
            if (!passwordEncoder.matches(adminPassword, user.getPasswordHash())) {
                user.setPasswordHash(passwordEncoder.encode(adminPassword));
                needSave = true;
            }

            if (needSave) {
                user.setUpdatedAt(Instant.now());
                userRepository.save(user);
                log.info(">>> [AdminDataInitializer] Đã chuẩn hóa tài khoản Admin: {} (Quyền: ADMIN, Active: true)", normalizedEmail);
            }
        }
    }
}
