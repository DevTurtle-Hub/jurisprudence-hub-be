package jurisprudence_hub_be.module.interaction.controller;

import jakarta.validation.Valid;
import jurisprudence_hub_be.common.dto.ApiResponse;
import jurisprudence_hub_be.module.interaction.constant.InteractionConstant;
import jurisprudence_hub_be.module.interaction.dto.request.AnnotationCreateRequest;
import jurisprudence_hub_be.module.interaction.dto.response.AnnotationResponse;
import jurisprudence_hub_be.module.interaction.service.AnnotationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping("/api/v1/lessons/{lessonId}/annotations")
    public ResponseEntity<ApiResponse<List<AnnotationResponse>>> getLessonAnnotations(@PathVariable String lessonId) {
        List<AnnotationResponse> annotations = annotationService.getLessonAnnotations(lessonId);
        return ResponseEntity.ok(ApiResponse.success(InteractionConstant.MSG_ANNOTATION_LIST_SUCCESS, annotations));
    }

    @PostMapping("/api/v1/lessons/{lessonId}/annotations")
    public ResponseEntity<ApiResponse<AnnotationResponse>> createAnnotation(
            @PathVariable String lessonId,
            @Valid @RequestBody AnnotationCreateRequest request
    ) {
        AnnotationResponse created = annotationService.createAnnotation(lessonId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(InteractionConstant.MSG_ANNOTATION_CREATE_SUCCESS, created));
    }

    @DeleteMapping("/api/v1/annotations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnotation(@PathVariable String id) {
        annotationService.deleteAnnotation(id);
        return ResponseEntity.ok(ApiResponse.success(InteractionConstant.MSG_ANNOTATION_DELETE_SUCCESS, null));
    }
}
