-- Migration V3: Exam Assessment Module Schema & Seed Data (CAND Examination Room)

-- 1. Bảng exam_rooms (Danh mục phòng thi)
CREATE TABLE IF NOT EXISTS exam_rooms (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 60,
    total_attempts INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exam_rooms_code ON exam_rooms(code);
CREATE INDEX IF NOT EXISTS idx_exam_rooms_status ON exam_rooms(status);

-- 2. Bảng exam_questions_mc (Câu hỏi trắc nghiệm)
CREATE TABLE IF NOT EXISTS exam_questions_mc (
    id VARCHAR(64) PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL REFERENCES exam_rooms(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    explanation TEXT,
    legal_reference VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_exam_questions_mc_room ON exam_questions_mc(room_id);

-- 3. Bảng exam_question_mc_options (Phương án lựa chọn A, B, C, D)
CREATE TABLE IF NOT EXISTS exam_question_mc_options (
    id VARCHAR(64) PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL REFERENCES exam_questions_mc(id) ON DELETE CASCADE,
    label CHAR(1) NOT NULL,
    option_text TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_exam_question_mc_options_q ON exam_question_mc_options(question_id);

-- 4. Bảng exam_questions_essay (Câu hỏi tự luận)
CREATE TABLE IF NOT EXISTS exam_questions_essay (
    id VARCHAR(64) PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL REFERENCES exam_rooms(id) ON DELETE CASCADE,
    order_index INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    context TEXT,
    prompt TEXT NOT NULL,
    max_score DECIMAL(4,1) NOT NULL DEFAULT 30.0,
    rubrics JSONB DEFAULT '[]'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_exam_questions_essay_room ON exam_questions_essay(room_id);

-- 5. Bảng candidate_verifications (Định danh thí sinh)
CREATE TABLE IF NOT EXISTS candidate_verifications (
    id VARCHAR(64) PRIMARY KEY,
    room_id VARCHAR(64) NOT NULL REFERENCES exam_rooms(id) ON DELETE CASCADE,
    cccd VARCHAR(12) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    candidate_id VARCHAR(32) NOT NULL,
    session_token TEXT UNIQUE NOT NULL,
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_candidate_verifications_room ON candidate_verifications(room_id);
CREATE INDEX IF NOT EXISTS idx_candidate_verifications_session ON candidate_verifications(session_token);
CREATE INDEX IF NOT EXISTS idx_candidate_verifications_cccd ON candidate_verifications(cccd);

-- 6. Bảng exam_submissions (Biên bản nộp bài & Bảng điểm)
CREATE TABLE IF NOT EXISTS exam_submissions (
    id VARCHAR(64) PRIMARY KEY,
    receipt_id VARCHAR(64) UNIQUE NOT NULL,
    room_id VARCHAR(64) NOT NULL REFERENCES exam_rooms(id) ON DELETE CASCADE,
    candidate_verification_id VARCHAR(64) NOT NULL REFERENCES candidate_verifications(id) ON DELETE CASCADE,
    time_spent_seconds INT NOT NULL,
    mc_answered_count INT NOT NULL,
    mc_total_count INT NOT NULL,
    mc_correct_count INT NOT NULL,
    mc_score DECIMAL(4,1) NOT NULL,
    essay_answered_count INT NOT NULL,
    essay_total_count INT NOT NULL,
    essay_score DECIMAL(4,1),
    total_score DECIMAL(5,1),
    essay_feedback TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_ESSAY_GRADING',
    sha256_digest VARCHAR(128) NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exam_submissions_receipt ON exam_submissions(receipt_id);
CREATE INDEX IF NOT EXISTS idx_exam_submissions_room ON exam_submissions(room_id);
CREATE INDEX IF NOT EXISTS idx_exam_submissions_candidate ON exam_submissions(candidate_verification_id);

-- 7. Bảng submission_answers (Chi tiết câu trả lời của thí sinh)
CREATE TABLE IF NOT EXISTS submission_answers (
    id VARCHAR(64) PRIMARY KEY,
    submission_id VARCHAR(64) NOT NULL REFERENCES exam_submissions(id) ON DELETE CASCADE,
    question_type VARCHAR(10) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    chosen_option CHAR(1),
    is_correct BOOLEAN,
    essay_content TEXT,
    essay_score DECIMAL(4,1)
);

CREATE INDEX IF NOT EXISTS idx_submission_answers_sub ON submission_answers(submission_id);

-- ==========================================
-- SEED DATA: Phòng thi mẫu CAND-CA4-01
-- ==========================================

INSERT INTO exam_rooms (id, code, title, description, duration_minutes, total_attempts, status, created_at, updated_at)
VALUES (
    'room-ca4-01',
    'CAND-CA4-01',
    'Đề Sát Hạch Chuẩn CA4 - Lý Luận Nhà Nước & Pháp Luật',
    'Đề thi sát hạch lý thuyết và xử lý tình huống nghiệp vụ CAND. Yêu cầu thí sinh tuân thủ nghiêm ngặt quy chế phòng thi.',
    60,
    128,
    'OPEN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO NOTHING;

-- Câu trắc nghiệm 1
INSERT INTO exam_questions_mc (id, room_id, order_index, question_text, correct_answer, explanation, legal_reference)
VALUES (
    'mc-01',
    'room-ca4-01',
    1,
    'Theo Hiến pháp 2013, cơ quan nào là cơ quan đại biểu cao nhất của Nhân dân, cơ quan quyền lực nhà nước cao nhất của nước CHXHCN Việt Nam?',
    'B',
    'Căn cứ theo Điều 69 Hiến pháp 2013, Quốc hội là cơ quan đại biểu cao nhất của Nhân dân, cơ quan quyền lực nhà nước cao nhất.',
    'Điều 69 Hiến pháp 2013'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO exam_question_mc_options (id, question_id, label, option_text) VALUES
('opt-01-A', 'mc-01', 'A', 'Chính phủ nước Cộng hòa xã hội chủ nghĩa Việt Nam.'),
('opt-01-B', 'mc-01', 'B', 'Quốc hội nước Cộng hòa xã hội chủ nghĩa Việt Nam.'),
('opt-01-C', 'mc-01', 'C', 'Tòa án nhân dân tối cao.'),
('opt-01-D', 'mc-01', 'D', 'Viện kiểm sát nhân dân tối cao.')
ON CONFLICT (id) DO NOTHING;

-- Câu trắc nghiệm 2
INSERT INTO exam_questions_mc (id, room_id, order_index, question_text, correct_answer, explanation, legal_reference)
VALUES (
    'mc-02',
    'room-ca4-01',
    2,
    'Trong Nhà nước pháp quyền xã hội chủ nghĩa Việt Nam, quyền lực nhà nước được tổ chức theo nguyên tắc nào?',
    'A',
    'Quyền lực nhà nước là thống nhất, có sự phân công, phối hợp, kiểm soát giữa các cơ quan nhà nước trong việc thực hiện các quyền lập pháp, hành pháp, tư pháp.',
    'Điều 2 Hiến pháp 2013'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO exam_question_mc_options (id, question_id, label, option_text) VALUES
('opt-02-A', 'mc-02', 'A', 'Thống nhất, có sự phân công, phối hợp, kiểm soát giữa các cơ quan nhà nước trong việc thực hiện các quyền lập pháp, hành pháp, tư pháp.'),
('opt-02-B', 'mc-02', 'B', 'Tam quyền phân lập độc lập hoàn toàn tuyệt đối.'),
('opt-02-C', 'mc-02', 'C', 'Tập quyền tuyệt đối vào cơ quan hành pháp.'),
('opt-02-D', 'mc-02', 'D', 'Phân chia quyền lực theo từng địa phương tự trị.')
ON CONFLICT (id) DO NOTHING;

-- Câu trắc nghiệm 3
INSERT INTO exam_questions_mc (id, room_id, order_index, question_text, correct_answer, explanation, legal_reference)
VALUES (
    'mc-03',
    'room-ca4-01',
    3,
    'Theo quy định của Bộ luật Tố tụng hình sự 2015, trường hợp nào sau đây được coi là bắt người phạm tội quả tang?',
    'C',
    'Người đang thực hiện tội phạm hoặc ngay sau khi thực hiện tội phạm thì bị phát hiện hoặc bị đuổi bắt.',
    'Điều 111 Bộ luật Tố tụng hình sự 2015'
) ON CONFLICT (id) DO NOTHING;

INSERT INTO exam_question_mc_options (id, question_id, label, option_text) VALUES
('opt-03-A', 'mc-03', 'A', 'Người có hành vi vi phạm hành chính thông thường.'),
('opt-03-B', 'mc-03', 'B', 'Người đang bị truy nã đã trốn thoát khỏi địa bàn quá 1 tháng.'),
('opt-03-C', 'mc-03', 'C', 'Người đang thực hiện tội phạm hoặc ngay sau khi thực hiện tội phạm thì bị phát hiện hoặc bị đuổi bắt.'),
('opt-03-D', 'mc-03', 'D', 'Người có dấu hiệu nghi ngờ nhưng chưa có chứng cứ cụ thể.')
ON CONFLICT (id) DO NOTHING;

-- Câu tự luận 1
INSERT INTO exam_questions_essay (id, room_id, order_index, title, context, prompt, max_score, rubrics)
VALUES (
    'essay-01',
    'room-ca4-01',
    1,
    'Tình huống 01: Xử lý hành vi tàng trữ vũ khí quân dụng trái phép khi tuần tra',
    'Hồi 23h30 ngày 15/08/2026, Tổ tuần tra kiểm soát Công an phường X phối hợp lực lượng Cảnh sát trật tự tuần tra trên tuyến phố Hoàng Hoa Thám thì phát hiện 02 nam thanh niên điều khiển xe mô tô có biểu hiện nghi vấn. Khi dừng xe kiểm tra hành chính, phát hiện đối tượng ngồi sau cất giấu trong áo khoác 01 khẩu súng ngắn K54 và 06 viên đạn đã nạp trong hộp tiếp đạn.',
    'Dựa trên các quy định của Bộ luật Tố tụng hình sự và quy trình công tác nghiệp vụ CAND, đồng chí hãy:\n1. Xác định căn cứ pháp lý và trình tự thủ tục bắt giữ đối tượng trong tình huống trên.\n2. Trình bày các bước lập biên bản thu giữ tang vật, bảo quản phương tiện vũ khí để phục vụ công tác giám định và điều tra.',
    30.0,
    '["Xác định đúng căn cứ bắt người phạm tội quả tang theo Điều 111 BLTTHS (10 điểm)", "Quy trình lập biên bản bắt giữ, niêm phong tang vật theo đúng mẫu nghiệp vụ CAND (12 điểm)", "Biện pháp đảm bảo an toàn tuyệt đối cho lực lượng tuần tra và bảo quản mẫu vật phục vụ giám định kỹ thuật hình sự (8 điểm)"]'::jsonb
) ON CONFLICT (id) DO NOTHING;
