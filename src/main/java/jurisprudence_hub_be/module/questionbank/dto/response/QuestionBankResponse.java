package jurisprudence_hub_be.module.questionbank.dto.response;

import java.time.Instant;
import java.util.List;

public class QuestionBankResponse {

    private String id;
    private String title;
    private String description;
    private String category;
    private String questionType;
    private String questionText;
    private String correctAnswer;
    private String explanation;
    private String legalReference;
    private String sampleEssay;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDraft;
    private String verifiedBy;
    private Instant verifiedAt;
    private List<QuestionBankMcOptionResponse> options;

    public QuestionBankResponse() {
    }

    public QuestionBankResponse(String id, String title, String description, String category,
                               String questionType, String questionText, String correctAnswer, String explanation,
                               String legalReference, String sampleEssay, String createdBy, Instant createdAt,
                               Instant updatedAt, boolean isDraft, String verifiedBy, Instant verifiedAt,
                               List<QuestionBankMcOptionResponse> options) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.questionType = questionType;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.legalReference = legalReference;
        this.sampleEssay = sampleEssay;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDraft = isDraft;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.options = options;
    }

    public static QuestionBankResponseBuilder builder() {
        return new QuestionBankResponseBuilder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isDraft() { return isDraft; }
    public void setDraft(boolean draft) { isDraft = draft; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }

    public List<QuestionBankMcOptionResponse> getOptions() { return options; }
    public void setOptions(List<QuestionBankMcOptionResponse> options) { this.options = options; }

    public static class QuestionBankResponseBuilder {
        private String id;
        private String title;
        private String description;
        private String category;
        private String questionType;
        private String questionText;
        private String correctAnswer;
        private String explanation;
        private String legalReference;
        private String sampleEssay;
        private String createdBy;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean isDraft;
        private String verifiedBy;
        private Instant verifiedAt;
        private List<QuestionBankMcOptionResponse> options;

        public QuestionBankResponseBuilder id(String id) { this.id = id; return this; }
        public QuestionBankResponseBuilder title(String title) { this.title = title; return this; }
        public QuestionBankResponseBuilder description(String description) { this.description = description; return this; }
        public QuestionBankResponseBuilder category(String category) { this.category = category; return this; }
        public QuestionBankResponseBuilder questionType(String questionType) { this.questionType = questionType; return this; }
        public QuestionBankResponseBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public QuestionBankResponseBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
        public QuestionBankResponseBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public QuestionBankResponseBuilder legalReference(String legalReference) { this.legalReference = legalReference; return this; }
        public QuestionBankResponseBuilder sampleEssay(String sampleEssay) { this.sampleEssay = sampleEssay; return this; }
        public QuestionBankResponseBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public QuestionBankResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public QuestionBankResponseBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public QuestionBankResponseBuilder isDraft(boolean isDraft) { this.isDraft = isDraft; return this; }
        public QuestionBankResponseBuilder verifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public QuestionBankResponseBuilder verifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public QuestionBankResponseBuilder options(List<QuestionBankMcOptionResponse> options) { this.options = options; return this; }

        public QuestionBankResponse build() {
            return new QuestionBankResponse(id, title, description, category, questionType,
                    questionText, correctAnswer, explanation, legalReference, sampleEssay, createdBy,
                    createdAt, updatedAt, isDraft, verifiedBy, verifiedAt, options);
        }
    }
}