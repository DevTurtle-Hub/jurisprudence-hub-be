package jurisprudence_hub_be.module.questionbank.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_bank_mc_options")
public class QuestionBankMcOption {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(length = 10, nullable = false)
    private String label;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    public QuestionBankMcOption() {
    }

    public QuestionBankMcOption(String id, QuestionBank question, String label, String optionText, boolean isCorrect) {
        this.id = id;
        this.question = question;
        this.label = label;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public static QuestionBankMcOptionBuilder builder() {
        return new QuestionBankMcOptionBuilder();
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "qbopt-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public QuestionBank getQuestion() { return question; }
    public void setQuestion(QuestionBank question) { this.question = question; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public static class QuestionBankMcOptionBuilder {
        private String id;
        private QuestionBank question;
        private String label;
        private String optionText;
        private boolean isCorrect = false;

        public QuestionBankMcOptionBuilder id(String id) { this.id = id; return this; }
        public QuestionBankMcOptionBuilder question(QuestionBank question) { this.question = question; return this; }
        public QuestionBankMcOptionBuilder label(String label) { this.label = label; return this; }
        public QuestionBankMcOptionBuilder optionText(String optionText) { this.optionText = optionText; return this; }
        public QuestionBankMcOptionBuilder isCorrect(boolean isCorrect) { this.isCorrect = isCorrect; return this; }

        public QuestionBankMcOption build() {
            return new QuestionBankMcOption(id, question, label, optionText, isCorrect);
        }
    }
}