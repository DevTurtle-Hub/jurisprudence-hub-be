package jurisprudence_hub_be.module.questionbank.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.questionbank.constant.QuestionBankConstant;
import jurisprudence_hub_be.module.questionbank.dto.request.QuestionBankRequest;
import jurisprudence_hub_be.module.questionbank.dto.response.QuestionBankResponse;
import jurisprudence_hub_be.module.questionbank.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question-bank")
@RequiredArgsConstructor
@Slf4j
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    /**
     * 1. API Import và preview câu hỏi từ file (dùng lại logic import từ exam)
     */
    @PostMapping(value = "/parse-preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<QuestionBankResponse>>> parseAndPreviewQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetType", defaultValue = "FULL") String targetType
    ) {
        List<QuestionBankResponse> data = questionBankService.parseAndPreviewQuestions(file, targetType);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_PARSE_PREVIEW_SUCCESS, data));
    }

    /**
     * 2. API Lưu câu hỏi sau khi preview và chỉnh sửa
     */
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> saveQuestion(
            @Valid @RequestBody QuestionBankRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication != null ? authentication.getName() : "admin";
        QuestionBankResponse data = questionBankService.saveQuestion(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(QuestionBankConstant.MSG_SAVE_QUESTION_SUCCESS, data));
    }

    /**
     * 3. API Lưu nhiều câu hỏi cùng lúc
     */
    @PostMapping("/questions/batch")
    public ResponseEntity<ApiResponse<List<QuestionBankResponse>>> saveMultipleQuestions(
            @Valid @RequestBody List<QuestionBankRequest> requests,
            Authentication authentication
    ) {
        String createdBy = authentication != null ? authentication.getName() : "admin";
        List<QuestionBankResponse> data = questionBankService.saveMultipleQuestions(requests, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(QuestionBankConstant.MSG_SAVE_BATCH_SUCCESS, data));
    }

    /**
     * 4. API Lấy danh sách câu hỏi có phân trang
     */
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<Page<QuestionBankResponse>>> getQuestions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "questionType", required = false) String questionType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "isDraft", defaultValue = "true") boolean isDraft
    ) {
        int pageIndex = Math.max(0, page - 1);
        int pageSize = Math.max(1, limit);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("createdAt").descending());
        Page<QuestionBankResponse> data = questionBankService.getQuestions(
                category, questionType, keyword, isDraft, pageable);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_GET_QUESTIONS_SUCCESS, data));
    }

    /**
     * 5. API Lấy chi tiết câu hỏi
     */
    @GetMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> getQuestionById(
            @PathVariable("id") String id
    ) {
        QuestionBankResponse data = questionBankService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_GET_QUESTION_DETAIL_SUCCESS, data));
    }

    /**
     * 6. API Admin sửa câu hỏi đã lưu
     */
    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> updateQuestion(
            @PathVariable("id") String id,
            @Valid @RequestBody QuestionBankRequest request,
            @RequestParam(value = "editReason", required = false) String editReason,
            Authentication authentication
    ) {
        String editedBy = authentication != null ? authentication.getName() : "admin";
        QuestionBankResponse data = questionBankService.updateQuestion(id, request, editedBy, editReason);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_UPDATE_QUESTION_SUCCESS, data));
    }

    /**
     * 7. API Xóa câu hỏi
     */
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable("id") String id,
            Authentication authentication
    ) {
        String deletedBy = authentication != null ? authentication.getName() : "admin";
        questionBankService.deleteQuestion(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_DELETE_QUESTION_SUCCESS));
    }

    /**
     * 8. API Publish draft câu hỏi
     */
    @PatchMapping("/questions/{id}/publish")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> publishQuestion(
            @PathVariable("id") String id,
            Authentication authentication
    ) {
        String verifiedBy = authentication != null ? authentication.getName() : "admin";
        QuestionBankResponse data = questionBankService.publishQuestion(id, verifiedBy);
        return ResponseEntity.ok(ApiResponse.success(QuestionBankConstant.MSG_PUBLISH_QUESTION_SUCCESS, data));
    }

    /**
     * 9. API Tạo câu hỏi tay (không import từ file)
     */
    @PostMapping("/questions/manual")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> createManualQuestion(
            @Valid @RequestBody QuestionBankRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication != null ? authentication.getName() : "admin";
        QuestionBankResponse data = questionBankService.createManualQuestion(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(QuestionBankConstant.MSG_CREATE_MANUAL_SUCCESS, data));
    }
}