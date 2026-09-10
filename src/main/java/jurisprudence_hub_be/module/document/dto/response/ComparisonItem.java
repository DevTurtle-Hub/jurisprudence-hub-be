package jurisprudence_hub_be.module.document.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ComparisonItem(
        @JsonProperty("criteria")
        String criteria,

        @JsonAlias({"itemA", "conceptA"})
        @JsonProperty("conceptA")
        String conceptA,

        @JsonAlias({"itemB", "conceptB"})
        @JsonProperty("conceptB")
        String conceptB
) {
}
