import http from 'k6/http';
import { check, sleep } from 'k6';

// Cấu hình kịch bản kiểm thử tải với các giai đoạn tăng dần (Ramp-up)
export const options = {
    stages: [
        { duration: '30s', target: 10 },  // Giai đoạn khởi động: 10 VUs trong 30 giây
        { duration: '1m', target: 50 },   // Tăng tải: 50 VUs trong 1 phút
        { duration: '2m', target: 100 },  // Đỉnh tải: 100 VUs duy trì trong 2 phút
        { duration: '30s', target: 0 },   // Hạ nhiệt: giảm về 0 VUs
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],        // Tỷ lệ lỗi phải nhỏ hơn 1%
        http_req_duration: ['p(95)<500'],       // 95% request phải hoàn tất dưới 500ms
    },
};

const BASE_URL = __ENV.TARGET_URL || 'http://localhost:8080';

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Accept-Encoding': 'gzip',
        },
    };

    // 1. Kiểm tra Health Endpoint
    const healthRes = http.get(`${BASE_URL}/actuator/health`, params);
    check(healthRes, {
        'Health status is 200': (r) => r.status === 200,
    });

    sleep(1);

    // 2. Kiểm tra danh mục chương học (Chapters)
    const chaptersRes = http.get(`${BASE_URL}/api/v1/chapters`, params);
    check(chaptersRes, {
        'Chapters status is 200': (r) => r.status === 200,
    });

    sleep(1);

    // 3. Kiểm tra danh sách bài học (Lessons)
    const lessonsRes = http.get(`${BASE_URL}/api/v1/lessons`, params);
    check(lessonsRes, {
        'Lessons status is 200': (r) => r.status === 200,
    });

    sleep(1);

    // 4. Kiểm tra danh sách phòng thi sát hạch (Exam Rooms)
    const examsRes = http.get(`${BASE_URL}/api/v1/exams/rooms`, params);
    check(examsRes, {
        'Exams status is 200': (r) => r.status === 200,
    });

    sleep(2);
}
