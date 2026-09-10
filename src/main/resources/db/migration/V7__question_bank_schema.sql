-- Migration V7: Question Bank Module Schema
-- Ngân hàng câu hỏi để học tập và ôn luyện

-- 1. Bảng question_banks (Ngân hàng câu hỏi chính)
CREATE TABLE IF NOT EXISTS question_banks (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100), -- Môn học: Luật Nhà Nước, Nghiệp vụ CAND, etc.
    difficulty VARCHAR(20) DEFAULT 'MEDIUM', -- EASY, MEDIUM, HARD
    question_type VARCHAR(20) NOT NULL, -- MC, ESSAY
    question_text TEXT NOT NULL,
    correct_answer TEXT, -- Đáp án đúng (A/B/C/D cho MC, nội dung cho Essay)
    explanation TEXT, -- Giải thích
    legal_reference VARCHAR(255), -- Căn cứ pháp lý
    created_by VARCHAR(64), -- User/admin tạo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_draft BOOLEAN DEFAULT TRUE, -- TRUE = draft, FALSE = đã lưu
    verified_by VARCHAR(64), -- Admin xác minh
    verified_at TIMESTAMP -- Thời gian admin xác minh
);

CREATE INDEX IF NOT EXISTS idx_question_banks_category ON question_banks(category);
CREATE INDEX IF NOT EXISTS idx_question_banks_type ON question_banks(question_type);
CREATE INDEX IF NOT EXISTS idx_question_banks_difficulty ON question_banks(difficulty);
CREATE INDEX IF NOT EXISTS idx_question_banks_draft ON question_banks(is_draft);
CREATE INDEX IF NOT EXISTS idx_question_banks_created_by ON question_banks(created_by);

-- 2. Bảng question_bank_mc_options (Options cho câu trắc nghiệm)
CREATE TABLE IF NOT EXISTS question_bank_mc_options (
    id VARCHAR(64) PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL REFERENCES question_banks(id) ON DELETE CASCADE,
    label VARCHAR(10) NOT NULL, -- A, B, C, D
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_question_bank_mc_options_q ON question_bank_mc_options(question_id);

-- 3. Bảng question_bank_edit_history (Lịch sử chỉnh sửa câu hỏi)
CREATE TABLE IF NOT EXISTS question_bank_edit_history (
    id VARCHAR(64) PRIMARY KEY,
    question_id VARCHAR(64) NOT NULL REFERENCES question_banks(id) ON DELETE CASCADE,
    edited_by VARCHAR(64) NOT NULL,
    edit_reason TEXT,
    old_data JSONB, -- Dữ liệu cũ
    new_data JSONB, -- Dữ liệu mới
    edited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_question_bank_edit_history_q ON question_bank_edit_history(question_id);
CREATE INDEX IF NOT EXISTS idx_question_bank_edit_history_by ON question_bank_edit_history(edited_by);