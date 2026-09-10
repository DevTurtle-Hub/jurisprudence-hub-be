package jurisprudence_hub_be.module.interaction.dto.response;

import java.time.Instant;

public record AnnotationResponse(
        String id,
        String lessonId,
        String selectedText,
        String kind,
        String color,
        String note,
        Instant createdAt
) {
}
