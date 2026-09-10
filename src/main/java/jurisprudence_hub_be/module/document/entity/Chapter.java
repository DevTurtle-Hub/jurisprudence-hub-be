package jurisprudence_hub_be.module.document.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chapters")
@EntityListeners(AuditingEntityListener.class)
public class Chapter {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "\"order\"", nullable = false)
    private int order = 1;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Chapter() {
    }

    public Chapter(String id, String title, int order, String description, List<Lesson> lessons, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.order = order;
        this.description = description;
        this.lessons = lessons != null ? lessons : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "ch-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private int order = 1;
        private String description;
        private List<Lesson> lessons = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder order(int order) { this.order = order; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder lessons(List<Lesson> lessons) { this.lessons = lessons; return this; }

        public Chapter build() {
            Chapter c = new Chapter();
            c.id = this.id;
            c.title = this.title;
            c.order = this.order;
            c.description = this.description;
            c.lessons = this.lessons != null ? this.lessons : new ArrayList<>();
            return c;
        }
    }
}
