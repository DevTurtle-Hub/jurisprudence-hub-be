package jurisprudence_hub_be.module.questionbank.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "question_bank_edit_history")
@EntityListeners(AuditingEntityListener.class)
public class QuestionBankEditHistory {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(name = "edited_by", length = 64, nullable = false)
    private String editedBy;

    @Column(name = "edit_reason", columnDefinition = "TEXT")
    private String editReason;

    @Column(name = "old_data", columnDefinition = "JSONB")
    private String oldData;

    @Column(name = "new_data", columnDefinition = "JSONB")
    private String newData;

    @CreatedDate
    @Column(name = "edited_at", nullable = false, updatable = false)
    private Instant editedAt;

    public QuestionBankEditHistory() {
    }

    public QuestionBankEditHistory(String id, QuestionBank question, String editedBy, String editReason,
                                   String oldData, String newData, Instant editedAt) {
        this.id = id;
        this.question = question;
        this.editedBy = editedBy;
        this.editReason = editReason;
        this.oldData = oldData;
        this.newData = newData;
        this.editedAt = editedAt;
    }

    public static QuestionBankEditHistoryBuilder builder() {
        return new QuestionBankEditHistoryBuilder();
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "qbhist-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public QuestionBank getQuestion() { return question; }
    public void setQuestion(QuestionBank question) { this.question = question; }

    public String getEditedBy() { return editedBy; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    public String getEditReason() { return editReason; }
    public void setEditReason(String editReason) { this.editReason = editReason; }

    public String getOldData() { return oldData; }
    public void setOldData(String oldData) { this.oldData = oldData; }

    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }

    public static class QuestionBankEditHistoryBuilder {
        private String id;
        private QuestionBank question;
        private String editedBy;
        private String editReason;
        private String oldData;
        private String newData;
        private Instant editedAt;

        public QuestionBankEditHistoryBuilder id(String id) { this.id = id; return this; }
        public QuestionBankEditHistoryBuilder question(QuestionBank question) { this.question = question; return this; }
        public QuestionBankEditHistoryBuilder editedBy(String editedBy) { this.editedBy = editedBy; return this; }
        public QuestionBankEditHistoryBuilder editReason(String editReason) { this.editReason = editReason; return this; }
        public QuestionBankEditHistoryBuilder oldData(String oldData) { this.oldData = oldData; return this; }
        public QuestionBankEditHistoryBuilder newData(String newData) { this.newData = newData; return this; }
        public QuestionBankEditHistoryBuilder editedAt(Instant editedAt) { this.editedAt = editedAt; return this; }

        public QuestionBankEditHistory build() {
            return new QuestionBankEditHistory(id, question, editedBy, editReason, oldData, newData, editedAt);
        }
    }
}