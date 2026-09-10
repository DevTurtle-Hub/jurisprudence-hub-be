package jurisprudence_hub_be.module.document.dto.response;

public record LessonNavigationResponse(
        String prevLessonId,
        String nextLessonId
) {
}
