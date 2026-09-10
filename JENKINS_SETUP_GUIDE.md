# Hướng Dẫn Thiết Lập & Vận Hành Jenkins CI/CD - Jurisprudence Hub Backend

Tài liệu này hướng dẫn chi tiết từng bước cách khởi động Jenkins Server, thiết lập Pipeline CI/CD tự động hóa quy trình **Build -> Đóng gói Docker Image -> Triển khai (Deploy) -> Kiểm tra sức khỏe (Healthcheck)** cho dự án Jurisprudence Hub Backend.

---

## 1. Cấu trúc thư mục CI/CD vừa được thiết lập

```
jurisprudence-hub-be/
├── jenkins/
│   ├── Dockerfile.jenkins            # Image Jenkins LTS tích hợp Docker CLI & Compose
│   ├── docker-compose.jenkins.yml    # File compose chạy Jenkins Server (port 8088)
│   ├── start-jenkins.bat             # Script 1-click khởi chạy Jenkins trên Windows
│   └── start-jenkins.sh              # Script khởi chạy Jenkins trên Linux/macOS
├── scripts/
│   ├── deploy.sh                     # Script triển khai tự động qua Docker Compose
│   ├── deploy.bat                    # Script triển khai thủ công trên Windows
│   ├── healthcheck.sh                # Kiểm tra Actuator /actuator/health trên Linux/Jenkins
│   └── healthcheck.bat               # Kiểm tra Actuator trên Windows
├── docker-compose.prod.yml           # Cấu hình production chuẩn cho Backend & Postgres
├── .env.production.example           # Mẫu biến môi trường bảo mật production
├── Jenkinsfile                       # Kịch bản Declarative Pipeline tự động hóa
└── JENKINS_SETUP_GUIDE.md            # Tài liệu hướng dẫn này
```

---

## 2. Bước 1: Khởi động Jenkins Server qua Docker

Jenkins được đóng gói độc lập trong thư mục `jenkins/`, chạy ở cổng `8088` (tránh xung đột với cổng `8080` của Backend) và mount trực tiếp Docker Socket máy chủ để điều khiển Docker.

### Trên Windows:
Nhấp đúp chuột vào file:
```
jenkins/start-jenkins.bat
```
Hoặc mở terminal PowerShell / CMD tại thư mục `jenkins/`:
```bash
docker compose -f docker-compose.jenkins.yml up -d --build
```

### Trên Linux / macOS:
```bash
cd jenkins
chmod +x start-jenkins.sh
./start-jenkins.sh
```

### Lấy mật khẩu quản trị ban đầu (Initial Admin Password):
Chạy lệnh:
```bash
docker exec jurisprudence-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
Sao chép chuỗi ký tự mật khẩu được in ra màn hình.

---

## 3. Bước 2: Thiết lập cấu hình ban đầu trên giao diện Web

1. Mở trình duyệt và truy cập: **`http://localhost:8088`**
2. Dán mã **Initial Admin Password** vừa lấy ở trên vào ô và nhấn **Continue**.
3. Chọn **"Install suggested plugins"** (Cài đặt các plugin đề xuất). Quá trình này diễn ra trong 2 - 5 phút.
4. Tạo tài khoản **Admin** đầu tiên (Username, Password, Full Name, Email).
5. Xác nhận Jenkins URL: `http://localhost:8088` hoặc IP máy chủ của bạn -> Nhấn **Save and Finish**.

---

## 4. Bước 3: Cài đặt thêm các Plugin cần thiết cho Docker Pipeline

Để Jenkins chạy mượt mà các lệnh Docker và hiển thị sơ đồ Pipeline:
1. Vào **Manage Jenkins** -> **Plugins** -> tab **Available plugins**.
2. Tìm kiếm và tích chọn các plugin sau:
   - **Docker Pipeline**
   - **Pipeline: Stage View**
   - **Credentials Binding Plugin** (thường đã có sẵn)
3. Nhấn **Install** và chọn **Restart Jenkins when installation is complete and no jobs are running**.

---

## 5. Bước 4: Cấu hình Credentials (Bảo mật thông tin đăng nhập)

Để không để lộ mật khẩu cơ sở dữ liệu và JWT Secret trên Git, cấu hình biến bảo mật trong Jenkins:

1. Vào **Manage Jenkins** -> **Credentials** -> **System** -> **Global credentials (unrestricted)** -> **Add Credentials**.
2. **Nếu muốn đẩy Image lên Docker Hub:**
   - **Kind:** `Username with password`
   - **Username:** `<tai_khoan_docker_hub>`
   - **Password:** `<mat_khau_hoac_access_token>`
   - **ID:** `docker-registry-credentials` *(Bắt buộc đặt đúng ID này)*
3. **Cấu hình file biến môi trường Production:**
   - Tạo file `.env.production` tại thư mục gốc dự án dựa theo mẫu [.env.production.example](file:///.env.production.example).
   - Điền các giá trị mật khẩu thật cho `DB_PASSWORD`, `JWT_SECRET` (>= 32 ký tự) và `EXAM_HMAC_SECRET`.

---

## 6. Bước 5: Tạo Pipeline Job trên Jenkins

1. Tại trang chủ Jenkins, nhấn **New Item**.
2. Nhập tên: `jurisprudence-hub-backend-pipeline`.
3. Chọn loại: **Pipeline** -> Nhấn **OK**.
4. Tại mục **General**:
   - Tích chọn **This project is parameterized** (Jenkinsfile đã có sẵn các tham số cấu hình: `ENVIRONMENT`, `RUN_TESTS`, `PUSH_REGISTRY`, `DOCKER_REGISTRY`, `APP_PORT`).
5. Cuộn xuống mục **Pipeline**:
   - **Definition:** Chọn **Pipeline script from SCM**.
   - **SCM:** Chọn **Git**.
   - **Repository URL:** Điền đường dẫn Git repo của bạn (ví dụ: `https://github.com/your-repo/jurisprudence-hub-be.git`).
   - **Credentials:** Chọn tài khoản GitHub/GitLab nếu repo ở chế độ Private.
   - **Branches to build:** `*/main` hoặc `*/master`.
   - **Script Path:** `Jenkinsfile`.
6. Nhấn **Save**.

---

## 7. Bước 6: Thiết lập Webhook tự động kích hoạt (Tùy chọn)

### Dành cho GitHub:
1. Vào GitHub Repo -> **Settings** -> **Webhooks** -> **Add webhook**.
2. **Payload URL:** `http://<dia-chi-ip-hoac-domain-jenkins>:8088/github-webhook/`
3. **Content type:** `application/json`
4. **Which events would you like to trigger this webhook?**: Chọn `Just the push event`.
5. Nhấn **Add webhook**.

### Dành cho GitLab:
1. Vào GitLab Project -> **Settings** -> **Webhooks**.
2. Điền URL: `http://<dia-chi-ip-hoac-domain-jenkins>:8088/project/jurisprudence-hub-backend-pipeline`
3. Chọn Trigger: **Push events**.

---

## 8. Bước 7: Kích hoạt & Kiểm tra quá trình Triển khai

1. Trong Job vừa tạo, nhấn **Build with Parameters**.
2. Xem các tùy chọn:
   - `ENVIRONMENT`: `production` hoặc `staging`
   - `RUN_TESTS`: `false` (mặc định) hoặc `true` khi đã viết xong test
   - `PUSH_REGISTRY`: `true` nếu muốn lưu trữ image lên Docker Hub
   - `APP_PORT`: `8080`
3. Nhấn nút **Build**.
4. Theo dõi trực tiếp qua **Stage View** và **Console Output**:
   - **Pre-flight:** Kiểm tra Docker engine.
   - **Build Docker Image:** Đóng gói ứng dụng Spring Boot thành Docker image.
   - **Deploy (Docker Compose):** Khởi động Postgres và Backend container.
   - **Verify Healthcheck:** Gọi `http://localhost:8080/actuator/health` xác nhận trạng thái `UP`.
5. Sau khi build báo xanh (SUCCESS), ứng dụng đã chạy hoàn chỉnh tại:
   - Backend API: `http://localhost:8080/api/...`
   - Healthcheck: `http://localhost:8080/actuator/health`

---

## 9. Xử lý các sự cố thường gặp (Troubleshooting)

| Lỗi | Nguyên nhân | Cách khắc phục |
| :--- | :--- | :--- |
| `permission denied while trying to connect to the Docker daemon socket` | Container Jenkins không có quyền ghi/đọc socket docker của host | `docker-compose.jenkins.yml` đã được cấu hình sẵn `user: root`. Nếu chạy trực tiếp hãy đảm bảo thêm quyền vào `/var/run/docker.sock`. |
| `Port 8080 is already allocated` | Ứng dụng Backend cũ hoặc dịch vụ khác đang chiếm port 8080 | Đổi tham số `APP_PORT` trong Job Jenkins (ví dụ 8081) hoặc chạy `docker ps` để stop container cũ. |
| `Healthcheck timeout after 90s` | Database chưa kịp sẵn sàng hoặc Flyway migration bị treo | Chạy `docker logs -f jurisprudence-backend` để kiểm tra log chi tiết ngoại lệ của Spring Boot. |
| `Docker socket not found on Windows` | Docker Desktop chưa bật hoặc chưa chia sẻ socket | Bật Docker Desktop -> Settings -> General -> Tích chọn "Expose daemon on tcp://localhost:2375 without TLS" (nếu cần) hoặc kiểm tra WSL 2 integration. |
