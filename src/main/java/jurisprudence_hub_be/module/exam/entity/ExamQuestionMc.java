package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_questions_mc")
public class ExamQuestionMc {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ExamRoom room;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(name = "correct_answer", length = 10, nullable = false)
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "legal_reference", length = 255)
    private String legalReference;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("label ASC")
    private List<ExamQuestionMcOption> options = new ArrayList<>();

    public ExamQuestionMc() {
    }

    public ExamQuestionMc(String id, ExamRoom room, int orderIndex, String questionText, String context,
                          String correctAnswer, String explanation, String legalReference,
                          List<ExamQuestionMcOption> options) {
        this.id = id;
        this.room = room;
        this.orderIndex = orderIndex;
        this.questionText = questionText;
        this.context = context;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.legalReference = legalReference;
        this.options = (options != null) ? options : new ArrayList<>();
    }

    public static ExamQuestionMcBuilder builder() {
        return new ExamQuestionMcBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getLegalReference() {
        return legalReference;
    }

    public void setLegalReference(String legalReference) {
        this.legalReference = legalReference;
    }

    public List<ExamQuestionMcOption> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void setOptions(List<ExamQuestionMcOption> options) {
        this.options = options;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "mc-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class ExamQuestionMcBuilder {
        private String id;
        private ExamRoom room;
        private int orderIndex;
        private String questionText;
        private String context;
        private String correctAnswer;
        private String explanation;
        private String legalReference;
        private List<ExamQuestionMcOption> options = new ArrayList<>();

        public ExamQuestionMcBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamQuestionMcBuilder room(ExamRoom room) {
            this.room = room;
            return this;
        }

        public ExamQuestionMcBuilder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public ExamQuestionMcBuilder questionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        public ExamQuestionMcBuilder context(String context) {
            this.context = context;
            return this;
        }

        public ExamQuestionMcBuilder correctAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
            return this;
        }

        public ExamQuestionMcBuilder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public ExamQuestionMcBuilder legalReference(String legalReference) {
            this.legalReference = legalReference;
            return this;
        }

        public ExamQuestionMcBuilder options(List<ExamQuestionMcOption> options) {
            this.options = (options != null) ? options : new ArrayList<>();
            return this;
        }

        public ExamQuestionMc build() {
            return new ExamQuestionMc(id, room, orderIndex, questionText, context, correctAnswer, explanation, legalReference, options);
        }
    }
}
