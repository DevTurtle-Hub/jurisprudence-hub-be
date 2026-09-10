package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.draft.AddDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.dto.draft.ConfirmImportResponse;
import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftQuestionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftValidationResponse;
import jurisprudence_hub_be.module.exam.dto.draft.ExamImportPreviewResponse;
import jurisprudence_hub_be.module.exam.dto.draft.UpdateDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.enums.ImportMode;
import org.springframework.web.multipart.MultipartFile;

public interface ExamImportService {

    /**
     * Upload và parse PDF thành bản nháp (DRAFT).
     * TUYỆT ĐỐI KHÔNG LƯU VÀO BẢNG CHÍNH THỨC Ở BƯỚC NÀY.
     * @param mode Chế độ import: FULL (mặc định), MCQ_ONLY, ESSAY_ONLY
     */
    ExamImportPreviewResponse previewImport(MultipartFile file, ImportMode mode);

    /**
     * Tạo bản nháp mới cho nhập thủ công (không cần file PDF)
     */
    ExamImportPreviewResponse createManualDraft(String title);

    /**
     * Lấy chi tiết bản nháp theo draftId
     */
    DraftExamDto getDraft(String draftId);

    /**
     * Sửa câu hỏi trong bản nháp
     */
    DraftQuestionDto updateQuestion(String draftId, String questionId, UpdateDraftQuestionRequest request);

    /**
     * Xóa câu hỏi khỏi bản nháp
     */
    DraftExamDto deleteQuestion(String draftId, String questionId);

    /**
     * Thêm câu hỏi thủ công vào bản nháp
     */
    DraftQuestionDto addQuestion(String draftId, AddDraftQuestionRequest request);

    /**
     * Kiểm tra tính hợp lệ toàn diện của bản nháp
     */
    DraftValidationResponse validateDraft(String draftId);

    /**
     * Xác nhận import: Lưu chính thức vào PostgreSQL trong 1 Transaction duy nhất (@Transactional).
     * Rollback toàn bộ nếu có bất kỳ lỗi nào xảy ra.
     */
    ConfirmImportResponse confirmImport(String draftId);

    /**
     * Hủy bỏ bản nháp
     */
    void cancelDraft(String draftId);
}
