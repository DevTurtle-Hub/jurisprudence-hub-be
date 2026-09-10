package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "exam_question_mc_options")
public class ExamQuestionMcOption {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private ExamQuestionMc question;

    @Column(length = 10, nullable = false)
    private String label;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    public ExamQuestionMcOption() {
    }

    public ExamQuestionMcOption(String id, ExamQuestionMc question, String label, String optionText) {
        this.id = id;
        this.question = question;
        this.label = label;
        this.optionText = optionText;
    }

    public static ExamQuestionMcOptionBuilder builder() {
        return new ExamQuestionMcOptionBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExamQuestionMc getQuestion() {
        return question;
    }

    public void setQuestion(ExamQuestionMc question) {
        this.question = question;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "opt-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class ExamQuestionMcOptionBuilder {
        private String id;
        private ExamQuestionMc question;
        private String label;
        private String optionText;

        public ExamQuestionMcOptionBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamQuestionMcOptionBuilder question(ExamQuestionMc question) {
            this.question = question;
            return this;
        }

        public ExamQuestionMcOptionBuilder label(String label) {
            this.label = label;
            return this;
        }

        public ExamQuestionMcOptionBuilder optionText(String optionText) {
            this.optionText = optionText;
            return this;
        }

        public ExamQuestionMcOption build() {
            return new ExamQuestionMcOption(id, question, label, optionText);
        }
    }
}
