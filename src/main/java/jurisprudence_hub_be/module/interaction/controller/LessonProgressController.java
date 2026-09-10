package jurisprudence_hub_be.module.interaction.controller;

import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.interaction.constant.InteractionConstant;
import jurisprudence_hub_be.module.interaction.dto.request.LessonProgressRequest;
import jurisprudence_hub_be.module.interaction.dto.response.LessonProgressResponse;
import jurisprudence_hub_be.module.interaction.service.LessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/lessons/{lessonId}/progress")
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    public LessonProgressController(LessonProgressService lessonProgressService) {
        this.lessonProgressService = lessonProgressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LessonProgressResponse>> updateProgress(
            @PathVariable String lessonId,
            @RequestBody(required = false) LessonProgressRequest request
    ) {
        LessonProgressResponse response = lessonProgressService.updateProgress(lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(InteractionConstant.MSG_PROGRESS_UPDATE_SUCCESS, response));
    }
}
