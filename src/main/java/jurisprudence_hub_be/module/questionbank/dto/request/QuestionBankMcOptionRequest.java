package jurisprudence_hub_be.module.questionbank.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionBankMcOptionRequest {

    private String id;

    @NotBlank(message = "Label không được để trống")
    private String label;

    @NotBlank(message = "Nội dung phương án không được để trống")
    @JsonProperty("optionText")
    @JsonAlias({"optionText", "text"})
    private String optionText;

    @JsonProperty("correct")
    @JsonAlias({"correct", "isCorrect"})
    private boolean isCorrect = false;

    public QuestionBankMcOptionRequest() {
    }

    public QuestionBankMcOptionRequest(String id, String label, String optionText, boolean isCorrect) {
        this.id = id;
        this.label = label;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public static QuestionBankMcOptionRequestBuilder builder() {
        return new QuestionBankMcOptionRequestBuilder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { this.isCorrect = correct; }

    public static class QuestionBankMcOptionRequestBuilder {
        private String id;
        private String label;
        private String optionText;
        private boolean isCorrect = false;

        public QuestionBankMcOptionRequestBuilder id(String id) { this.id = id; return this; }
        public QuestionBankMcOptionRequestBuilder label(String label) { this.label = label; return this; }
        public QuestionBankMcOptionRequestBuilder optionText(String optionText) { this.optionText = optionText; return this; }
        public QuestionBankMcOptionRequestBuilder isCorrect(boolean isCorrect) { this.isCorrect = isCorrect; return this; }

        public QuestionBankMcOptionRequest build() {
            return new QuestionBankMcOptionRequest(id, label, optionText, isCorrect);
        }
    }
}
