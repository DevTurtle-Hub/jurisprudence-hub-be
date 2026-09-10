package jurisprudence_hub_be.module.document.dto.response;

public record LessonDetailResponse(
        String id,
        String chapterId,
        String chapterTitle,
        String title,
        int order,
        LessonContentResponse content,
        LessonNavigationResponse navigation
) {
}
