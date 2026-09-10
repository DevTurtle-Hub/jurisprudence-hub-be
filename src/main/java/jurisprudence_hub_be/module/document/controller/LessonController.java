package jurisprudence_hub_be.module.document.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.dto.request.LessonCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.LessonDetailResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonSummaryResponse;
import jurisprudence_hub_be.module.document.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    
    @GetMapping
    public ResponseEntity<ApiResponse<List<LessonSummaryResponse>>> getAllLessons(
            @RequestParam(name = "chapterId", required = false) String chapterId
    ) {
        List<LessonSummaryResponse> lessons = lessonService.getAllLessons(chapterId);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_LESSON_LIST_SUCCESS, lessons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDetailResponse>> getLessonDetail(@PathVariable String id) {
        LessonDetailResponse detail = lessonService.getLessonDetail(id);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_LESSON_DETAIL_SUCCESS, detail));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LessonSummaryResponse>> createLesson(@Valid @RequestBody LessonCreateRequest request) {
        LessonSummaryResponse created = lessonService.createLesson(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(DocumentConstant.MSG_LESSON_CREATE_SUCCESS, created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LessonSummaryResponse>> updateLesson(
            @PathVariable String id,
            @Valid @RequestBody LessonUpdateRequest request
    ) {
        LessonSummaryResponse updated = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_LESSON_UPDATE_SUCCESS, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable String id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_LESSON_DELETE_SUCCESS, null));
    }
}
