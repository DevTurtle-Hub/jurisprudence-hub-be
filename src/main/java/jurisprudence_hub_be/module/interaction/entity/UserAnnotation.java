package jurisprudence_hub_be.module.interaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.document.entity.Lesson;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "user_annotations")
@EntityListeners(AuditingEntityListener.class)
public class UserAnnotation {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "selected_text", nullable = false, columnDefinition = "TEXT")
    private String selectedText;

    @Column(nullable = false, length = 50)
    private String kind;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserAnnotation() {
    }

    public UserAnnotation(String id, User user, Lesson lesson, String selectedText, String kind, String color, String note, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.lesson = lesson;
        this.selectedText = selectedText;
        this.kind = kind;
        this.color = color;
        this.note = note;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "ant-" + System.currentTimeMillis();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public String getSelectedText() { return selectedText; }
    public void setSelectedText(String selectedText) { this.selectedText = selectedText; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private User user;
        private Lesson lesson;
        private String selectedText;
        private String kind;
        private String color;
        private String note;

        public Builder id(String id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder lesson(Lesson lesson) { this.lesson = lesson; return this; }
        public Builder selectedText(String selectedText) { this.selectedText = selectedText; return this; }
        public Builder kind(String kind) { this.kind = kind; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder note(String note) { this.note = note; return this; }

        public UserAnnotation build() {
            UserAnnotation a = new UserAnnotation();
            a.id = this.id;
            a.user = this.user;
            a.lesson = this.lesson;
            a.selectedText = this.selectedText;
            a.kind = this.kind;
            a.color = this.color;
            a.note = this.note;
            return a;
        }
    }
}
