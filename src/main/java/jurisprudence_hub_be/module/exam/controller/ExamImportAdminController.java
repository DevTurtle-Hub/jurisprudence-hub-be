package jurisprudence_hub_be.module.exam.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.draft.AddDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.dto.draft.ConfirmImportResponse;
import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftQuestionDto;
import jurisprudence_hub_be.module.exam.dto.draft.DraftValidationResponse;
import jurisprudence_hub_be.module.exam.dto.draft.ExamImportPreviewResponse;
import jurisprudence_hub_be.module.exam.dto.draft.UpdateDraftQuestionRequest;
import jurisprudence_hub_be.module.exam.enums.ImportMode;
import jurisprudence_hub_be.module.exam.service.ExamImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/exams/import")
@RequiredArgsConstructor
@Slf4j
public class ExamImportAdminController {

    private final ExamImportService examImportService;

    /**
     * 1. Upload file PDF và tạo bản nháp Preview.
     * TUYỆT ĐỐI KHÔNG GHI VÀO CƠ SỞ DỮ LIỆU CHÍNH THỨC Ở BƯỚC NÀY.
     * @param mode Chế độ import: FULL (mặc định), MCQ_ONLY, ESSAY_ONLY
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ExamImportPreviewResponse>> previewImport(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "FULL") ImportMode mode
    ) {
        ExamImportPreviewResponse response = examImportService.previewImport(file, mode);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_IMPORT_PREVIEW_SUCCESS, response));
    }

    /**
     * 1b. Tạo bản nháp mới cho nhập thủ công (không cần file PDF)
     */
    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<ExamImportPreviewResponse>> createManualDraft(
            @RequestParam(value = "title", required = false) String title
    ) {
        ExamImportPreviewResponse response = examImportService.createManualDraft(title);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_MANUAL_DRAFT_SUCCESS, response));
    }

    /**
     * 2. Lấy dữ liệu bản nháp theo draftId
     */
    @GetMapping("/drafts/{draftId}")
    public ResponseEntity<ApiResponse<DraftExamDto>> getDraft(
            @PathVariable("draftId") String draftId
    ) {
        DraftExamDto response = examImportService.getDraft(draftId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_DRAFT_DETAIL_SUCCESS, response));
    }

    /**
     * 3. Sửa câu hỏi trong bản nháp (nội dung, phương án, đáp án đúng)
     */
    @PutMapping("/drafts/{draftId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<DraftQuestionDto>> updateQuestion(
            @PathVariable("draftId") String draftId,
            @PathVariable("questionId") String questionId,
            @RequestBody UpdateDraftQuestionRequest request
    ) {
        DraftQuestionDto response = examImportService.updateQuestion(draftId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_DRAFT_UPDATE_QUESTION_SUCCESS, response));
    }

    /**
     * 4. Xóa câu hỏi khỏi bản nháp
     */
    @DeleteMapping("/drafts/{draftId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<DraftExamDto>> deleteQuestion(
            @PathVariable("draftId") String draftId,
            @PathVariable("questionId") String questionId
    ) {
        DraftExamDto response = examImportService.deleteQuestion(draftId, questionId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_DRAFT_DELETE_QUESTION_SUCCESS, response));
    }

    /**
     * 5. Thêm câu hỏi thủ công vào bản nháp
     */
    @PostMapping("/drafts/{draftId}/questions")
    public ResponseEntity<ApiResponse<DraftQuestionDto>> addQuestion(
            @PathVariable("draftId") String draftId,
            @Valid @RequestBody AddDraftQuestionRequest request
    ) {
        DraftQuestionDto response = examImportService.addQuestion(draftId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ExamConstant.MSG_DRAFT_ADD_QUESTION_SUCCESS, response));
    }

    /**
     * 6. Kiểm tra hợp lệ (Validate) toàn bộ bản nháp trước khi Confirm
     */
    @PostMapping("/drafts/{draftId}/validate")
    public ResponseEntity<ApiResponse<DraftValidationResponse>> validateDraft(
            @PathVariable("draftId") String draftId
    ) {
        DraftValidationResponse response = examImportService.validateDraft(draftId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_DRAFT_VALIDATE_SUCCESS, response));
    }

    /**
     * 7. Xác nhận Import chính thức vào PostgreSQL (@Transactional).
     * Chỉ lưu khi không có lỗi nghiêm trọng.
     */
    @PostMapping("/drafts/{draftId}/confirm")
    public ResponseEntity<ApiResponse<ConfirmImportResponse>> confirmImport(
            @PathVariable("draftId") String draftId
    ) {
        ConfirmImportResponse response = examImportService.confirmImport(draftId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_IMPORT_CONFIRM_SUCCESS, response));
    }

    /**
     * 8. Hủy bản nháp
     */
    @DeleteMapping("/drafts/{draftId}")
    public ResponseEntity<ApiResponse<Void>> cancelDraft(
            @PathVariable("draftId") String draftId
    ) {
        examImportService.cancelDraft(draftId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_DRAFT_CANCEL_SUCCESS));
    }
}
