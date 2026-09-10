package jurisprudence_hub_be.module.document.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.document.dto.response.ComparisonItem;
import jurisprudence_hub_be.module.document.dto.response.DefinitionItem;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson_contents")
public class LessonContent {

    @Id
    @Column(name = "lesson_id", length = 50)
    private String lessonId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "objectives", columnDefinition = "jsonb")
    private List<String> objectives = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "core_knowledge", columnDefinition = "jsonb")
    private List<String> coreKnowledge = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "definitions", columnDefinition = "jsonb")
    private List<DefinitionItem> definitions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "keywords", columnDefinition = "jsonb")
    private List<String> keywords = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "comparisons", columnDefinition = "jsonb")
    private List<ComparisonItem> comparisons = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exam_hotspots", columnDefinition = "jsonb")
    private List<String> examHotspots = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "common_traps", columnDefinition = "jsonb")
    private List<String> commonTraps = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "memory_tips", columnDefinition = "jsonb")
    private List<String> memoryTips = new ArrayList<>();

    public LessonContent() {
    }

    public LessonContent(String lessonId, Lesson lesson, List<String> objectives, List<String> coreKnowledge, List<DefinitionItem> definitions, List<String> keywords, List<ComparisonItem> comparisons, List<String> examHotspots, List<String> commonTraps, List<String> memoryTips) {
        this.lessonId = lessonId;
        this.lesson = lesson;
        this.objectives = objectives != null ? objectives : new ArrayList<>();
        this.coreKnowledge = coreKnowledge != null ? coreKnowledge : new ArrayList<>();
        this.definitions = definitions != null ? definitions : new ArrayList<>();
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.comparisons = comparisons != null ? comparisons : new ArrayList<>();
        this.examHotspots = examHotspots != null ? examHotspots : new ArrayList<>();
        this.commonTraps = commonTraps != null ? commonTraps : new ArrayList<>();
        this.memoryTips = memoryTips != null ? memoryTips : new ArrayList<>();
    }

    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public List<String> getObjectives() { return objectives; }
    public void setObjectives(List<String> objectives) { this.objectives = objectives; }

    public List<String> getCoreKnowledge() { return coreKnowledge; }
    public void setCoreKnowledge(List<String> coreKnowledge) { this.coreKnowledge = coreKnowledge; }

    public List<DefinitionItem> getDefinitions() { return definitions; }
    public void setDefinitions(List<DefinitionItem> definitions) { this.definitions = definitions; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public List<ComparisonItem> getComparisons() { return comparisons; }
    public void setComparisons(List<ComparisonItem> comparisons) { this.comparisons = comparisons; }

    public List<String> getExamHotspots() { return examHotspots; }
    public void setExamHotspots(List<String> examHotspots) { this.examHotspots = examHotspots; }

    public List<String> getCommonTraps() { return commonTraps; }
    public void setCommonTraps(List<String> commonTraps) { this.commonTraps = commonTraps; }

    public List<String> getMemoryTips() { return memoryTips; }
    public void setMemoryTips(List<String> memoryTips) { this.memoryTips = memoryTips; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String lessonId;
        private Lesson lesson;
        private List<String> objectives = new ArrayList<>();
        private List<String> coreKnowledge = new ArrayList<>();
        private List<DefinitionItem> definitions = new ArrayList<>();
        private List<String> keywords = new ArrayList<>();
        private List<ComparisonItem> comparisons = new ArrayList<>();
        private List<String> examHotspots = new ArrayList<>();
        private List<String> commonTraps = new ArrayList<>();
        private List<String> memoryTips = new ArrayList<>();

        public Builder lessonId(String lessonId) { this.lessonId = lessonId; return this; }
        public Builder lesson(Lesson lesson) { this.lesson = lesson; return this; }
        public Builder objectives(List<String> objectives) { this.objectives = objectives; return this; }
        public Builder coreKnowledge(List<String> coreKnowledge) { this.coreKnowledge = coreKnowledge; return this; }
        public Builder definitions(List<DefinitionItem> definitions) { this.definitions = definitions; return this; }
        public Builder keywords(List<String> keywords) { this.keywords = keywords; return this; }
        public Builder comparisons(List<ComparisonItem> comparisons) { this.comparisons = comparisons; return this; }
        public Builder examHotspots(List<String> examHotspots) { this.examHotspots = examHotspots; return this; }
        public Builder commonTraps(List<String> commonTraps) { this.commonTraps = commonTraps; return this; }
        public Builder memoryTips(List<String> memoryTips) { this.memoryTips = memoryTips; return this; }

        public LessonContent build() {
            LessonContent c = new LessonContent();
            c.lessonId = this.lessonId;
            c.lesson = this.lesson;
            c.objectives = this.objectives != null ? this.objectives : new ArrayList<>();
            c.coreKnowledge = this.coreKnowledge != null ? this.coreKnowledge : new ArrayList<>();
            c.definitions = this.definitions != null ? this.definitions : new ArrayList<>();
            c.keywords = this.keywords != null ? this.keywords : new ArrayList<>();
            c.comparisons = this.comparisons != null ? this.comparisons : new ArrayList<>();
            c.examHotspots = this.examHotspots != null ? this.examHotspots : new ArrayList<>();
            c.commonTraps = this.commonTraps != null ? this.commonTraps : new ArrayList<>();
            c.memoryTips = this.memoryTips != null ? this.memoryTips : new ArrayList<>();
            return c;
        }
    }
}
