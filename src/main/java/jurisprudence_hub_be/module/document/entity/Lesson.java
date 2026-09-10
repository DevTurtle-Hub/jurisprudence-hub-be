package jurisprudence_hub_be.module.document.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@EntityListeners(AuditingEntityListener.class)
public class Lesson {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "\"order\"", nullable = false)
    private int order = 1;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LessonContent content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Lesson() {
    }

    public Lesson(String id, Chapter chapter, String title, int order, String createdBy, LessonContent content, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.chapter = chapter;
        this.title = title;
        this.order = order;
        this.createdBy = createdBy;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "lesson-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Chapter getChapter() { return chapter; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LessonContent getContent() { return content; }
    public void setContent(LessonContent content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Chapter chapter;
        private String title;
        private int order = 1;
        private String createdBy;
        private LessonContent content;

        public Builder id(String id) { this.id = id; return this; }
        public Builder chapter(Chapter chapter) { this.chapter = chapter; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder order(int order) { this.order = order; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder content(LessonContent content) { this.content = content; return this; }

        public Lesson build() {
            Lesson l = new Lesson();
            l.id = this.id;
            l.chapter = this.chapter;
            l.title = this.title;
            l.order = this.order;
            l.createdBy = this.createdBy;
            l.content = this.content;
            return l;
        }
    }
}
