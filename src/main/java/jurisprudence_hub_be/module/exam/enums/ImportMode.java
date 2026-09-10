package jurisprudence_hub_be.module.exam.enums;

public enum ImportMode {
    /**
     * Import đầy đủ cả tự luận và trắc nghiệm từ PDF
     */
    FULL,
    
    /**
     * Chỉ import câu hỏi trắc nghiệm từ PDF
     */
    MCQ_ONLY,
    
    /**
     * Chỉ import câu hỏi tự luận từ PDF
     */
    ESSAY_ONLY,
    
    /**
     * Nhập thủ công câu hỏi (không dùng PDF)
     */
    MANUAL
}
