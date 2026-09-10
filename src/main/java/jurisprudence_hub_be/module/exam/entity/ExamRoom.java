package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_rooms")
@EntityListeners(AuditingEntityListener.class)
public class ExamRoom {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 60;

    @Column(name = "total_attempts", nullable = false)
    private int totalAttempts = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamRoomStatus status = ExamRoomStatus.OPEN;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExamQuestionMc> multipleChoiceQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ExamQuestionEssay> essayQuestions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ExamRoom() {
    }

    public ExamRoom(String id, String code, String title, String description, int durationMinutes,
                    int totalAttempts, ExamRoomStatus status, Instant startAt, Instant endAt,
                    String createdBy, List<ExamQuestionMc> multipleChoiceQuestions,
                    List<ExamQuestionEssay> essayQuestions, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalAttempts = totalAttempts;
        this.status = (status != null) ? status : ExamRoomStatus.OPEN;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdBy = createdBy;
        this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
        this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ExamRoomBuilder builder() {
        return new ExamRoomBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public ExamRoomStatus getStatus() {
        return status;
    }

    public void setStatus(ExamRoomStatus status) {
        this.status = status;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<ExamQuestionMc> getMultipleChoiceQuestions() {
        if (multipleChoiceQuestions == null) {
            multipleChoiceQuestions = new ArrayList<>();
        }
        return multipleChoiceQuestions;
    }

    public void setMultipleChoiceQuestions(List<ExamQuestionMc> multipleChoiceQuestions) {
        this.multipleChoiceQuestions = multipleChoiceQuestions;
    }

    public List<ExamQuestionEssay> getEssayQuestions() {
        if (essayQuestions == null) {
            essayQuestions = new ArrayList<>();
        }
        return essayQuestions;
    }

    public void setEssayQuestions(List<ExamQuestionEssay> essayQuestions) {
        this.essayQuestions = essayQuestions;
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
            this.id = "room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class ExamRoomBuilder {
        private String id;
        private String code;
        private String title;
        private String description;
        private int durationMinutes = 60;
        private int totalAttempts = 0;
        private ExamRoomStatus status = ExamRoomStatus.OPEN;
        private Instant startAt;
        private Instant endAt;
        private String createdBy;
        private List<ExamQuestionMc> multipleChoiceQuestions = new ArrayList<>();
        private List<ExamQuestionEssay> essayQuestions = new ArrayList<>();
        private Instant createdAt;
        private Instant updatedAt;

        public ExamRoomBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamRoomBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExamRoomBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamRoomBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExamRoomBuilder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public ExamRoomBuilder totalAttempts(int totalAttempts) {
            this.totalAttempts = totalAttempts;
            return this;
        }

        public ExamRoomBuilder status(ExamRoomStatus status) {
            this.status = status;
            return this;
        }

        public ExamRoomBuilder startAt(Instant startAt) {
            this.startAt = startAt;
            return this;
        }

        public ExamRoomBuilder endAt(Instant endAt) {
            this.endAt = endAt;
            return this;
        }

        public ExamRoomBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public ExamRoomBuilder multipleChoiceQuestions(List<ExamQuestionMc> multipleChoiceQuestions) {
            this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
            return this;
        }

        public ExamRoomBuilder essayQuestions(List<ExamQuestionEssay> essayQuestions) {
            this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
            return this;
        }

        public ExamRoomBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExamRoomBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ExamRoom build() {
            return new ExamRoom(id, code, title, description, durationMinutes, totalAttempts, status,
                    startAt, endAt, createdBy, multipleChoiceQuestions, essayQuestions, createdAt, updatedAt);
        }
    }
}
