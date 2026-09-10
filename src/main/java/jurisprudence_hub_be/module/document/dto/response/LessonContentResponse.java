package jurisprudence_hub_be.module.document.dto.response;

import java.util.Collections;
import java.util.List;

public record LessonContentResponse(
        List<String> objectives,
        List<String> coreKnowledge,
        List<DefinitionItem> definitions,
        List<String> keywords,
        List<ComparisonItem> comparisons,
        List<String> examHotspots,
        List<String> commonTraps,
        List<String> memoryTips
) {
    public static LessonContentResponse empty() {
        return new LessonContentResponse(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
