package jurisprudence_hub_be.module.questionbank.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionBankRequest {

    @NotBlank(message = "Tiêu đề câu hỏi không được để trống")
    private String title;

    private String description;

    private String category;

    @NotBlank(message = "Loại câu hỏi không được để trống")
    private String questionType; // MC, ESSAY

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String questionText;

    private String correctAnswer;

    private String explanation;

    private String legalReference;

    private String sampleEssay;

    private String tags;

    @JsonProperty("draft")
    @JsonAlias({"draft", "isDraft"})
    private boolean isDraft = true;

    // For MC questions
    private java.util.List<QuestionBankMcOptionRequest> options;

    public QuestionBankRequest() {
    }

    public QuestionBankRequest(String title, String description, String category,
                              String questionType, String questionText, String correctAnswer,
                              String explanation, String legalReference, String sampleEssay,
                              String tags, boolean isDraft,
                              java.util.List<QuestionBankMcOptionRequest> options) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.questionType = questionType;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.legalReference = legalReference;
        this.sampleEssay = sampleEssay;
        this.tags = tags;
        this.isDraft = isDraft;
        this.options = options;
    }

    public static QuestionBankRequestBuilder builder() {
        return new QuestionBankRequestBuilder();
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getLegalReference() { return legalReference; }
    public void setLegalReference(String legalReference) { this.legalReference = legalReference; }

    public String getSampleEssay() { return sampleEssay; }
    public void setSampleEssay(String sampleEssay) { this.sampleEssay = sampleEssay; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { this.isDraft = draft; }

    public java.util.List<QuestionBankMcOptionRequest> getOptions() { return options; }
    public void setOptions(java.util.List<QuestionBankMcOptionRequest> options) { this.options = options; }

    public static class QuestionBankRequestBuilder {
        private String title;
        private String description;
        private String category;
        private String questionType;
        private String questionText;
        private String correctAnswer;
        private String explanation;
        private String legalReference;
        private String sampleEssay;
        private String tags;
        private boolean isDraft = true;
        private java.util.List<QuestionBankMcOptionRequest> options;

        public QuestionBankRequestBuilder title(String title) { this.title = title; return this; }
        public QuestionBankRequestBuilder description(String description) { this.description = description; return this; }
        public QuestionBankRequestBuilder category(String category) { this.category = category; return this; }
        public QuestionBankRequestBuilder questionType(String questionType) { this.questionType = questionType; return this; }
        public QuestionBankRequestBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public QuestionBankRequestBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
        public QuestionBankRequestBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public QuestionBankRequestBuilder legalReference(String legalReference) { this.legalReference = legalReference; return this; }
        public QuestionBankRequestBuilder sampleEssay(String sampleEssay) { this.sampleEssay = sampleEssay; return this; }
        public QuestionBankRequestBuilder tags(String tags) { this.tags = tags; return this; }
        public QuestionBankRequestBuilder isDraft(boolean isDraft) { this.isDraft = isDraft; return this; }
        public QuestionBankRequestBuilder options(java.util.List<QuestionBankMcOptionRequest> options) { this.options = options; return this; }

        public QuestionBankRequest build() {
            return new QuestionBankRequest(title, description, category, questionType,
                    questionText, correctAnswer, explanation, legalReference, sampleEssay, tags, isDraft, options);
        }
    }
}
