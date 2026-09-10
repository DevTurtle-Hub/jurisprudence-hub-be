package jurisprudence_hub_be.module.questionbank.entity;

import jakarta.persistence.*;
import jurisprudence_hub_be.module.questionbank.enums.QuestionBankDifficulty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question_banks")
@EntityListeners(AuditingEntityListener.class)
public class QuestionBank {

    @Id
    @Column(length = 64)
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String category;

    @Column(name = "question_type", length = 20, nullable = false)
    private String questionType;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "legal_reference", columnDefinition = "TEXT")
    private String legalReference;

    @Column(columnDefinition = "TEXT")
    private String sampleEssay;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_draft", nullable = false)
    private boolean isDraft = true;

    @Column(name = "verified_by", length = 64)
    private String verifiedBy;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionBankMcOption> options = new ArrayList<>();

    public QuestionBank() {
    }

    public QuestionBank(String id, String title, String description, String category,
                        String questionType, String questionText, String correctAnswer, String explanation,
                        String legalReference, String sampleEssay, String createdBy, boolean isDraft,
                        String verifiedBy, Instant verifiedAt) {
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
        this.isDraft = isDraft;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
    }

    public static QuestionBankBuilder builder() {
        return new QuestionBankBuilder();
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "qb-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
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

    public List<QuestionBankMcOption> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void setOptions(List<QuestionBankMcOption> options) {
        this.options = (options != null) ? options : new ArrayList<>();
    }

    public static class QuestionBankBuilder {
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
        private boolean isDraft = true;
        private String verifiedBy;
        private Instant verifiedAt;
        private List<QuestionBankMcOption> options = new ArrayList<>();

        public QuestionBankBuilder id(String id) { this.id = id; return this; }
        public QuestionBankBuilder title(String title) { this.title = title; return this; }
        public QuestionBankBuilder description(String description) { this.description = description; return this; }
        public QuestionBankBuilder category(String category) { this.category = category; return this; }
        public QuestionBankBuilder questionType(String questionType) { this.questionType = questionType; return this; }
        public QuestionBankBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public QuestionBankBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
        public QuestionBankBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public QuestionBankBuilder legalReference(String legalReference) { this.legalReference = legalReference; return this; }
        public QuestionBankBuilder sampleEssay(String sampleEssay) { this.sampleEssay = sampleEssay; return this; }
        public QuestionBankBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public QuestionBankBuilder isDraft(boolean isDraft) { this.isDraft = isDraft; return this; }
        public QuestionBankBuilder verifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public QuestionBankBuilder verifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public QuestionBankBuilder options(List<QuestionBankMcOption> options) {
            this.options = (options != null) ? options : new ArrayList<>();
            return this;
        }

        public QuestionBank build() {
            return new QuestionBank(id, title, description, category, questionType,
                    questionText, correctAnswer, explanation, legalReference, sampleEssay, createdBy,
                    isDraft, verifiedBy, verifiedAt);
        }
    }
}