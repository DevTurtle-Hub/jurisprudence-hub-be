package jurisprudence_hub_be.module.document.dto.response;

import java.time.Instant;
import java.util.List;

public record ChapterResponse(
        String id,
        String title,
        int order,
        String description,
        List<LessonSummaryResponse> lessons,
        Instant createdAt,
        Instant updatedAt
) {
}
