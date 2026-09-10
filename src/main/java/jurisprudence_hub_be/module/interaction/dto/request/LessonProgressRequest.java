package jurisprudence_hub_be.module.interaction.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LessonProgressRequest(
        @JsonAlias({"completed", "isCompleted"})
        @JsonProperty("isCompleted")
        Boolean isCompleted
) {
    public boolean completed() {
        return isCompleted != null && isCompleted;
    }
}
