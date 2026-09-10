package jurisprudence_hub_be.module.document.dto.request;

import jurisprudence_hub_be.module.document.dto.response.ComparisonItem;
import jurisprudence_hub_be.module.document.dto.response.DefinitionItem;

import java.util.Collections;
import java.util.List;

public record LessonContentRequest(
        List<String> objectives,
        List<String> coreKnowledge,
        List<DefinitionItem> definitions,
        List<String> keywords,
        List<ComparisonItem> comparisons,
        List<String> examHotspots,
        List<String> commonTraps,
        List<String> memoryTips
) {
    public static LessonContentRequest empty() {
        return new LessonContentRequest(
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
