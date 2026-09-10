package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.exam.enums.QuestionType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "submission_answers")
public class SubmissionAnswer {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private ExamSubmission submission;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 10, nullable = false)
    private QuestionType questionType;

    @Column(name = "question_id", length = 64, nullable = false)
    private String questionId;

    @Column(name = "chosen_option", length = 1)
    private String chosenOption;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "essay_content", columnDefinition = "TEXT")
    private String essayContent;

    @Column(name = "essay_score", precision = 4, scale = 1)
    private BigDecimal essayScore;

    public SubmissionAnswer() {
    }

    public SubmissionAnswer(String id, ExamSubmission submission, QuestionType questionType,
                            String questionId, String chosenOption, Boolean isCorrect,
                            String essayContent, BigDecimal essayScore) {
        this.id = id;
        this.submission = submission;
        this.questionType = questionType;
        this.questionId = questionId;
        this.chosenOption = chosenOption;
        this.isCorrect = isCorrect;
        this.essayContent = essayContent;
        this.essayScore = essayScore;
    }

    public static SubmissionAnswerBuilder builder() {
        return new SubmissionAnswerBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExamSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(ExamSubmission submission) {
        this.submission = submission;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getChosenOption() {
        return chosenOption;
    }

    public void setChosenOption(String chosenOption) {
        this.chosenOption = chosenOption;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getEssayContent() {
        return essayContent;
    }

    public void setEssayContent(String essayContent) {
        this.essayContent = essayContent;
    }

    public BigDecimal getEssayScore() {
        return essayScore;
    }

    public void setEssayScore(BigDecimal essayScore) {
        this.essayScore = essayScore;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "ans-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class SubmissionAnswerBuilder {
        private String id;
        private ExamSubmission submission;
        private QuestionType questionType;
        private String questionId;
        private String chosenOption;
        private Boolean isCorrect;
        private String essayContent;
        private BigDecimal essayScore;

        public SubmissionAnswerBuilder id(String id) {
            this.id = id;
            return this;
        }

        public SubmissionAnswerBuilder submission(ExamSubmission submission) {
            this.submission = submission;
            return this;
        }

        public SubmissionAnswerBuilder questionType(QuestionType questionType) {
            this.questionType = questionType;
            return this;
        }

        public SubmissionAnswerBuilder questionId(String questionId) {
            this.questionId = questionId;
            return this;
        }

        public SubmissionAnswerBuilder chosenOption(String chosenOption) {
            this.chosenOption = chosenOption;
            return this;
        }

        public SubmissionAnswerBuilder isCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public SubmissionAnswerBuilder essayContent(String essayContent) {
            this.essayContent = essayContent;
            return this;
        }

        public SubmissionAnswerBuilder essayScore(BigDecimal essayScore) {
            this.essayScore = essayScore;
            return this;
        }

        public SubmissionAnswer build() {
            return new SubmissionAnswer(id, submission, questionType, questionId, chosenOption, isCorrect, essayContent, essayScore);
        }
    }
}
