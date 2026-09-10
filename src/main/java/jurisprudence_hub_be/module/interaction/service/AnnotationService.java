package jurisprudence_hub_be.module.interaction.service;

import jurisprudence_hub_be.module.interaction.dto.request.AnnotationCreateRequest;
import jurisprudence_hub_be.module.interaction.dto.response.AnnotationResponse;

import java.util.List;

public interface AnnotationService {

    List<AnnotationResponse> getLessonAnnotations(String lessonId);

    AnnotationResponse createAnnotation(String lessonId, AnnotationCreateRequest request);

    void deleteAnnotation(String id);
}
