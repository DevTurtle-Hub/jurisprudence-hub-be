package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.exam.dto.draft.DraftExamDto;
import jurisprudence_hub_be.module.exam.enums.DraftStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "exam_import_drafts")
@EntityListeners(AuditingEntityListener.class)
public class ExamImportDraft {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DraftStatus status = DraftStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "draft_data", columnDefinition = "jsonb", nullable = false)
    private DraftExamDto draftData;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ExamImportDraft() {
    }

    public ExamImportDraft(String id, String fileName, String title, int totalQuestions,
                           DraftStatus status, DraftExamDto draftData,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.totalQuestions = totalQuestions;
        this.status = (status != null) ? status : DraftStatus.DRAFT;
        this.draftData = draftData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ExamImportDraftBuilder builder() {
        return new ExamImportDraftBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public DraftStatus getStatus() {
        return status;
    }

    public void setStatus(DraftStatus status) {
        this.status = status;
    }

    public DraftExamDto getDraftData() {
        return draftData;
    }

    public void setDraftData(DraftExamDto draftData) {
        this.draftData = draftData;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "draft-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }

    public static class ExamImportDraftBuilder {
        private String id;
        private String fileName;
        private String title;
        private int totalQuestions = 0;
        private DraftStatus status = DraftStatus.DRAFT;
        private DraftExamDto draftData;
        private Instant createdAt;
        private Instant updatedAt;

        public ExamImportDraftBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamImportDraftBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ExamImportDraftBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamImportDraftBuilder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public ExamImportDraftBuilder status(DraftStatus status) {
            this.status = status;
            return this;
        }

        public ExamImportDraftBuilder draftData(DraftExamDto draftData) {
            this.draftData = draftData;
            return this;
        }

        public ExamImportDraftBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExamImportDraftBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ExamImportDraft build() {
            return new ExamImportDraft(id, fileName, title, totalQuestions, status, draftData, createdAt, updatedAt);
        }
    }
}
