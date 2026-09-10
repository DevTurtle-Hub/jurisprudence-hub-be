package jurisprudence_hub_be.module.document.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.document.constant.DocumentConstant;
import jurisprudence_hub_be.module.document.dto.request.ChapterCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.ChapterUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.ChapterResponse;
import jurisprudence_hub_be.module.document.service.ChapterService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getAllChapters(
            @RequestParam(name = "includeLessons", defaultValue = "true", required = false) Boolean includeLessons,
            @RequestParam(name = "search", required = false) String search
    ) {
        List<ChapterResponse> chapters = chapterService.getAllChapters(includeLessons, search);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_CHAPTER_LIST_SUCCESS, chapters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChapterResponse>> getChapterById(@PathVariable String id) {
        ChapterResponse chapter = chapterService.getChapterById(id);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_CHAPTER_DETAIL_SUCCESS, chapter));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(@Valid @RequestBody ChapterCreateRequest request) {
        ChapterResponse created = chapterService.createChapter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(DocumentConstant.MSG_CHAPTER_CREATE_SUCCESS, created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(
            @PathVariable String id,
            @Valid @RequestBody ChapterUpdateRequest request
    ) {
        ChapterResponse updated = chapterService.updateChapter(id, request);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_CHAPTER_UPDATE_SUCCESS, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(@PathVariable String id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.ok(ApiResponse.success(DocumentConstant.MSG_CHAPTER_DELETE_SUCCESS, null));
    }
}
