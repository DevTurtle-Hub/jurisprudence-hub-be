package jurisprudence_hub_be.module.exam.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.common.exception.UnauthorizedException;
import jurisprudence_hub_be.module.exam.constant.ExamConstant;
import jurisprudence_hub_be.module.exam.dto.request.CreateExamRoomRequest;
import jurisprudence_hub_be.module.exam.dto.request.GradeEssayRequest;
import jurisprudence_hub_be.module.exam.dto.request.SubmitExamRequest;
import jurisprudence_hub_be.module.exam.dto.request.VerifyCandidateRequest;
import jurisprudence_hub_be.module.exam.dto.response.CandidateDto;
import jurisprudence_hub_be.module.exam.dto.response.CandidateVerificationResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomListResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamRoomResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamSubmissionReceiptResponse;
import jurisprudence_hub_be.module.exam.dto.response.ExamTakingRoomResponse;
import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import jurisprudence_hub_be.module.exam.dto.response.SubmissionDetailResponse;
import jurisprudence_hub_be.module.exam.entity.CandidateVerification;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import jurisprudence_hub_be.module.exam.service.CandidateService;
import jurisprudence_hub_be.module.exam.service.DocumentParserService;
import jurisprudence_hub_be.module.exam.service.ExamRoomService;
import jurisprudence_hub_be.module.exam.service.ExamSubmissionService;
import jurisprudence_hub_be.module.exam.service.ExamTakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Slf4j
public class ExamController {

    private final DocumentParserService documentParserService;
    private final ExamRoomService examRoomService;
    private final CandidateService candidateService;
    private final ExamTakingService examTakingService;
    private final ExamSubmissionService examSubmissionService;

    /**
     * 3.1. API Bóc tách tệp đề thi (PDF / DOCX / EXCEL / TXT)
     */
    @PostMapping(value = "/parse-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ParsedDocumentResponse>> parseDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetType", defaultValue = "FULL") String targetType
    ) {
        ParsedDocumentResponse data = documentParserService.parseDocument(file, targetType);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_PARSE_DOCUMENT_SUCCESS, data));
    }

    /**
     * 3.2. API Tạo phòng thi mới
     */
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ExamRoomResponse>> createRoom(
            @Valid @RequestBody CreateExamRoomRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication != null ? authentication.getName() : "admin";
        ExamRoomResponse data = examRoomService.createRoom(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ExamConstant.MSG_ROOM_CREATE_SUCCESS, data));
    }

    /**
     * API Cập nhật / Chỉnh sửa phòng thi
     */
    @PutMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<ExamRoomResponse>> updateRoom(
            @PathVariable("id") String id,
            @Valid @RequestBody CreateExamRoomRequest request
    ) {
        ExamRoomResponse data = examRoomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_ROOM_UPDATE_SUCCESS, data));
    }

    @PatchMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<ExamRoomResponse>> patchRoom(
            @PathVariable("id") String id,
            @RequestBody CreateExamRoomRequest request
    ) {
        ExamRoomResponse data = examRoomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_ROOM_UPDATE_SUCCESS, data));
    }

    /**
     * API Xóa phòng thi
     */
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable("id") String id
    ) {
        examRoomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_ROOM_DELETE_SUCCESS));
    }

    /**
     * 3.3. API Lấy danh sách phòng thi (phân trang, tìm kiếm, lọc status)
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<ExamRoomListResponse>> getRooms(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status
    ) {
        ExamRoomStatus roomStatus = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                roomStatus = ExamRoomStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        ExamRoomListResponse data = examRoomService.getRooms(search, roomStatus, page, limit);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_ROOM_LIST_SUCCESS, data));
    }

    /**
     * Lấy chi tiết phòng thi theo id hoặc code
     */
    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<ExamRoomResponse>> getRoomDetail(
            @PathVariable("id") String id
    ) {
        ExamRoomResponse data = examRoomService.getRoomDetail(id);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_ROOM_DETAIL_SUCCESS, data));
    }

    /**
     * 3.4. API Xác minh danh tính thí sinh (Candidate Verification)
     */
    @PostMapping("/rooms/{roomId}/verify-candidate")
    public ResponseEntity<ApiResponse<CandidateVerificationResponse>> verifyCandidate(
            @PathVariable("roomId") String roomId,
            @Valid @RequestBody VerifyCandidateRequest request
    ) {
        CandidateVerificationResponse data = candidateService.verifyCandidate(roomId, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_CANDIDATE_VERIFY_SUCCESS, data));
    }

    /**
     * 3.4.1. API Kiểm tra session token hợp lệ (để FE tránh xác minh lại)
     */
    @GetMapping("/rooms/{roomId}/check-session")
    public ResponseEntity<ApiResponse<CandidateVerificationResponse>> checkSessionValidity(
            @PathVariable("roomId") String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "sessionToken", required = false) String sessionTokenParam
    ) {
        String token = resolveToken(authHeader, sessionTokenParam);
        CandidateVerification verification = candidateService.getValidSession(token);

        // Kiểm tra xem session này có thuộc phòng thi đúng không (null-safe)
        boolean matchesRoom = verification.getRoom() != null && (
                (verification.getRoom().getId() != null && verification.getRoom().getId().equals(roomId)) ||
                (verification.getRoom().getCode() != null && verification.getRoom().getCode().equalsIgnoreCase(roomId))
        );
        if (!matchesRoom) {
            throw new UnauthorizedException(ExamConstant.MSG_SESSION_TOKEN_MISMATCH);
        }

        // Build response với thông tin candidate hiện tại
        CandidateDto candidateDto = CandidateDto.builder()
                .cccd(verification.getCccd())
                .fullName(verification.getFullName())
                .phone(verification.getPhone())
                .email(verification.getEmail())
                .address(verification.getAddress())
                .candidateId(verification.getCandidateId())
                .verifiedAt(verification.getVerifiedAt())
                .isVerified(true)
                .build();

        CandidateVerificationResponse response = CandidateVerificationResponse.builder()
                .sessionToken(verification.getSessionToken())
                .candidate(candidateDto)
                .build();

        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_SESSION_VALID, response));
    }

    /**
     * 3.5. API Lấy đề thi để làm bài (BẢO MẬT TUYỆT ĐỐI - Khử đáp án đúng)
     */
    @GetMapping("/rooms/{roomId}/take")
    public ResponseEntity<ApiResponse<ExamTakingRoomResponse>> getExamForTaking(
            @PathVariable("roomId") String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "sessionToken", required = false) String sessionTokenParam
    ) {
        String token = resolveToken(authHeader, sessionTokenParam);
        ExamTakingRoomResponse data = examTakingService.getExamForTaking(roomId, token);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_EXAM_TAKE_SUCCESS, data));
    }

    /**
     * 3.6. API Nộp bài thi & Tự động chấm Trắc nghiệm (Submit Exam)
     */
    @PostMapping("/rooms/{roomId}/submit")
    public ResponseEntity<ApiResponse<ExamSubmissionReceiptResponse>> submitExam(
            @PathVariable("roomId") String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "sessionToken", required = false) String sessionTokenParam,
            @RequestBody SubmitExamRequest request
    ) {
        String token = resolveToken(authHeader, sessionTokenParam);
        ExamSubmissionReceiptResponse data = examSubmissionService.submitExam(roomId, token, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_EXAM_SUBMIT_SUCCESS, data));
    }

    /**
     * 3.7. API Tra cứu biên bản nộp bài (Xem lại / In biên bản)
     */
    @GetMapping("/submissions/{receiptId}")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getReceipt(
            @PathVariable("receiptId") String receiptId
    ) {
        SubmissionDetailResponse data = examSubmissionService.getReceipt(receiptId);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_RECEIPT_DETAIL_SUCCESS, data));
    }

    /**
     * 3.8. API Dành cho Giám khảo chấm điểm Tự luận
     */
    @PatchMapping("/submissions/{submissionId}/grade-essay")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> gradeEssay(
            @PathVariable("submissionId") String submissionId,
            @Valid @RequestBody GradeEssayRequest request
    ) {
        SubmissionDetailResponse data = examSubmissionService.gradeEssay(submissionId, request);
        return ResponseEntity.ok(ApiResponse.success(ExamConstant.MSG_GRADE_ESSAY_SUCCESS, data));
    }

    private String resolveToken(String authHeader, String sessionTokenParam) {
        if (authHeader != null && !authHeader.isBlank()) {
            return authHeader;
        }
        if (sessionTokenParam != null && !sessionTokenParam.isBlank()) {
            return sessionTokenParam;
        }
        throw new UnauthorizedException(ExamConstant.MSG_SESSION_TOKEN_REQUIRED);
    }
}
