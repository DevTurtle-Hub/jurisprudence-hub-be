package jurisprudence_hub_be.module.document.dto.response;

public record LessonSummaryResponse(
        String id,
        String chapterId,
        String title,
        int order
) {
}
