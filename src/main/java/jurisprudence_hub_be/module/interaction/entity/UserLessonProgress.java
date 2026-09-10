package jurisprudence_hub_be.module.interaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.document.entity.Lesson;

import java.time.Instant;

@Entity
@Table(
        name = "user_lesson_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
)
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt = Instant.now();

    public UserLessonProgress() {
    }

    public UserLessonProgress(Long id, User user, Lesson lesson, boolean completed, Instant lastReadAt) {
        this.id = id;
        this.user = user;
        this.lesson = lesson;
        this.completed = completed;
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Instant getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private Lesson lesson;
        private boolean completed = false;
        private Instant lastReadAt = Instant.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder lesson(Lesson lesson) { this.lesson = lesson; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder lastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; return this; }

        public UserLessonProgress build() {
            UserLessonProgress p = new UserLessonProgress();
            p.id = this.id;
            p.user = this.user;
            p.lesson = this.lesson;
            p.completed = this.completed;
            p.lastReadAt = this.lastReadAt != null ? this.lastReadAt : Instant.now();
            return p;
        }
    }
}
