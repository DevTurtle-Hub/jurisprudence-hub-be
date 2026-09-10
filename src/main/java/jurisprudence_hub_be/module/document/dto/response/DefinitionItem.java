package jurisprudence_hub_be.module.document.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DefinitionItem(
        @JsonProperty("term")
        String term,

        @JsonAlias({"meaning", "definition"})
        @JsonProperty("definition")
        String definition
) {
}
