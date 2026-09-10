-- Migration V4: Exam Import Drafts Schema (Lưu trữ bản nháp đề thi import từ PDF)

CREATE TABLE IF NOT EXISTS exam_import_drafts (
    id VARCHAR(64) PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    total_questions INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    draft_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exam_import_drafts_status ON exam_import_drafts(status);
CREATE INDEX IF NOT EXISTS idx_exam_import_drafts_created_at ON exam_import_drafts(created_at);
