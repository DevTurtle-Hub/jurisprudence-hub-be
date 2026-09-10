package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_questions_essay")
public class ExamQuestionEssay {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ExamRoom room;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(name = "max_score", precision = 4, scale = 1, nullable = false)
    private BigDecimal maxScore = BigDecimal.valueOf(30.0);

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> rubrics = new ArrayList<>();

    public ExamQuestionEssay() {
    }

    public ExamQuestionEssay(String id, ExamRoom room, int orderIndex, String title,
                             String context, String prompt, BigDecimal maxScore, List<String> rubrics) {
        this.id = id;
        this.room = room;
        this.orderIndex = orderIndex;
        this.title = title;
        this.context = context;
        this.prompt = prompt;
        this.maxScore = (maxScore != null) ? maxScore : BigDecimal.valueOf(30.0);
        this.rubrics = (rubrics != null) ? rubrics : new ArrayList<>();
    }

    public static ExamQuestionEssayBuilder builder() {
        return new ExamQuestionEssayBuilder();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public List<String> getRubrics() {
        if (rubrics == null) {
            rubrics = new ArrayList<>();
        }
        return rubrics;
    }

    public void setRubrics(List<String> rubrics) {
        this.rubrics = rubrics;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "essay-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class ExamQuestionEssayBuilder {
        private String id;
        private ExamRoom room;
        private int orderIndex;
        private String title;
        private String context;
        private String prompt;
        private BigDecimal maxScore = BigDecimal.valueOf(30.0);
        private List<String> rubrics = new ArrayList<>();

        public ExamQuestionEssayBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamQuestionEssayBuilder room(ExamRoom room) {
            this.room = room;
            return this;
        }

        public ExamQuestionEssayBuilder orderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public ExamQuestionEssayBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamQuestionEssayBuilder context(String context) {
            this.context = context;
            return this;
        }

        public ExamQuestionEssayBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public ExamQuestionEssayBuilder maxScore(BigDecimal maxScore) {
            this.maxScore = (maxScore != null) ? maxScore : BigDecimal.valueOf(30.0);
            return this;
        }

        public ExamQuestionEssayBuilder rubrics(List<String> rubrics) {
            this.rubrics = (rubrics != null) ? rubrics : new ArrayList<>();
            return this;
        }

        public ExamQuestionEssay build() {
            return new ExamQuestionEssay(id, room, orderIndex, title, context, prompt, maxScore, rubrics);
        }
    }
}
