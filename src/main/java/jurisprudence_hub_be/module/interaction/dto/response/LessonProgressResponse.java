package jurisprudence_hub_be.module.interaction.dto.response;

import java.time.Instant;

public record LessonProgressResponse(
        String lessonId,
        boolean isCompleted,
        Instant lastReadAt
) {
}
