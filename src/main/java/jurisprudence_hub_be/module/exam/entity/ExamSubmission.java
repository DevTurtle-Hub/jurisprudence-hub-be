package jurisprudence_hub_be.module.exam.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jurisprudence_hub_be.module.exam.enums.SubmissionStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_submissions")
@EntityListeners(AuditingEntityListener.class)
public class ExamSubmission {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "receipt_id", length = 64, nullable = false, unique = true)
    private String receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ExamRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_verification_id", nullable = false)
    private CandidateVerification candidateVerification;

    @Column(name = "time_spent_seconds", nullable = false)
    private int timeSpentSeconds;

    @Column(name = "mc_answered_count", nullable = false)
    private int mcAnsweredCount;

    @Column(name = "mc_total_count", nullable = false)
    private int mcTotalCount;

    @Column(name = "mc_correct_count", nullable = false)
    private int mcCorrectCount;

    @Column(name = "mc_score", precision = 4, scale = 1, nullable = false)
    private BigDecimal mcScore;

    @Column(name = "essay_answered_count", nullable = false)
    private int essayAnsweredCount;

    @Column(name = "essay_total_count", nullable = false)
    private int essayTotalCount;

    @Column(name = "essay_score", precision = 4, scale = 1)
    private BigDecimal essayScore;

    @Column(name = "total_score", precision = 5, scale = 1)
    private BigDecimal totalScore;

    @Column(name = "essay_feedback", columnDefinition = "TEXT")
    private String essayFeedback;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private SubmissionStatus status = SubmissionStatus.PENDING_ESSAY_GRADING;

    @Column(name = "sha256_digest", length = 128, nullable = false)
    private String sha256Digest;

    @CreatedDate
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionAnswer> answers = new ArrayList<>();

    public ExamSubmission() {
    }

    public ExamSubmission(String id, String receiptId, ExamRoom room, CandidateVerification candidateVerification,
                          int timeSpentSeconds, int mcAnsweredCount, int mcTotalCount, int mcCorrectCount,
                          BigDecimal mcScore, int essayAnsweredCount, int essayTotalCount,
                          BigDecimal essayScore, BigDecimal totalScore, String essayFeedback,
                          SubmissionStatus status, String sha256Digest, Instant submittedAt,
                          List<SubmissionAnswer> answers) {
        this.id = id;
        this.receiptId = receiptId;
        this.room = room;
        this.candidateVerification = candidateVerification;
        this.timeSpentSeconds = timeSpentSeconds;
        this.mcAnsweredCount = mcAnsweredCount;
        this.mcTotalCount = mcTotalCount;
        this.mcCorrectCount = mcCorrectCount;
        this.mcScore = mcScore;
        this.essayAnsweredCount = essayAnsweredCount;
        this.essayTotalCount = essayTotalCount;
        this.essayScore = essayScore;
        this.totalScore = totalScore;
        this.essayFeedback = essayFeedback;
        this.status = (status != null) ? status : SubmissionStatus.PENDING_ESSAY_GRADING;
        this.sha256Digest = sha256Digest;
        this.submittedAt = submittedAt;
        this.answers = (answers != null) ? answers : new ArrayList<>();
    }

    public static ExamSubmissionBuilder builder() {
        return new ExamSubmissionBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
    }

    public CandidateVerification getCandidateVerification() {
        return candidateVerification;
    }

    public void setCandidateVerification(CandidateVerification candidateVerification) {
        this.candidateVerification = candidateVerification;
    }

    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(int timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public int getMcAnsweredCount() {
        return mcAnsweredCount;
    }

    public void setMcAnsweredCount(int mcAnsweredCount) {
        this.mcAnsweredCount = mcAnsweredCount;
    }

    public int getMcTotalCount() {
        return mcTotalCount;
    }

    public void setMcTotalCount(int mcTotalCount) {
        this.mcTotalCount = mcTotalCount;
    }

    public int getMcCorrectCount() {
        return mcCorrectCount;
    }

    public void setMcCorrectCount(int mcCorrectCount) {
        this.mcCorrectCount = mcCorrectCount;
    }

    public BigDecimal getMcScore() {
        return mcScore;
    }

    public void setMcScore(BigDecimal mcScore) {
        this.mcScore = mcScore;
    }

    public int getEssayAnsweredCount() {
        return essayAnsweredCount;
    }

    public void setEssayAnsweredCount(int essayAnsweredCount) {
        this.essayAnsweredCount = essayAnsweredCount;
    }

    public int getEssayTotalCount() {
        return essayTotalCount;
    }

    public void setEssayTotalCount(int essayTotalCount) {
        this.essayTotalCount = essayTotalCount;
    }

    public BigDecimal getEssayScore() {
        return essayScore;
    }

    public void setEssayScore(BigDecimal essayScore) {
        this.essayScore = essayScore;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getEssayFeedback() {
        return essayFeedback;
    }

    public void setEssayFeedback(String essayFeedback) {
        this.essayFeedback = essayFeedback;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getSha256Digest() {
        return sha256Digest;
    }

    public void setSha256Digest(String sha256Digest) {
        this.sha256Digest = sha256Digest;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<SubmissionAnswer> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        return answers;
    }

    public void setAnswers(List<SubmissionAnswer> answers) {
        this.answers = answers;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "sub-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    public static class ExamSubmissionBuilder {
        private String id;
        private String receiptId;
        private ExamRoom room;
        private CandidateVerification candidateVerification;
        private int timeSpentSeconds;
        private int mcAnsweredCount;
        private int mcTotalCount;
        private int mcCorrectCount;
        private BigDecimal mcScore;
        private int essayAnsweredCount;
        private int essayTotalCount;
        private BigDecimal essayScore;
        private BigDecimal totalScore;
        private String essayFeedback;
        private SubmissionStatus status = SubmissionStatus.PENDING_ESSAY_GRADING;
        private String sha256Digest;
        private Instant submittedAt;
        private List<SubmissionAnswer> answers = new ArrayList<>();

        public ExamSubmissionBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamSubmissionBuilder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public ExamSubmissionBuilder room(ExamRoom room) {
            this.room = room;
            return this;
        }

        public ExamSubmissionBuilder candidateVerification(CandidateVerification candidateVerification) {
            this.candidateVerification = candidateVerification;
            return this;
        }

        public ExamSubmissionBuilder timeSpentSeconds(int timeSpentSeconds) {
            this.timeSpentSeconds = timeSpentSeconds;
            return this;
        }

        public ExamSubmissionBuilder mcAnsweredCount(int mcAnsweredCount) {
            this.mcAnsweredCount = mcAnsweredCount;
            return this;
        }

        public ExamSubmissionBuilder mcTotalCount(int mcTotalCount) {
            this.mcTotalCount = mcTotalCount;
            return this;
        }

        public ExamSubmissionBuilder mcCorrectCount(int mcCorrectCount) {
            this.mcCorrectCount = mcCorrectCount;
            return this;
        }

        public ExamSubmissionBuilder mcScore(BigDecimal mcScore) {
            this.mcScore = mcScore;
            return this;
        }

        public ExamSubmissionBuilder essayAnsweredCount(int essayAnsweredCount) {
            this.essayAnsweredCount = essayAnsweredCount;
            return this;
        }

        public ExamSubmissionBuilder essayTotalCount(int essayTotalCount) {
            this.essayTotalCount = essayTotalCount;
            return this;
        }

        public ExamSubmissionBuilder essayScore(BigDecimal essayScore) {
            this.essayScore = essayScore;
            return this;
        }

        public ExamSubmissionBuilder totalScore(BigDecimal totalScore) {
            this.totalScore = totalScore;
            return this;
        }

        public ExamSubmissionBuilder essayFeedback(String essayFeedback) {
            this.essayFeedback = essayFeedback;
            return this;
        }

        public ExamSubmissionBuilder status(SubmissionStatus status) {
            this.status = status;
            return this;
        }

        public ExamSubmissionBuilder sha256Digest(String sha256Digest) {
            this.sha256Digest = sha256Digest;
            return this;
        }

        public ExamSubmissionBuilder submittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public ExamSubmissionBuilder answers(List<SubmissionAnswer> answers) {
            this.answers = (answers != null) ? answers : new ArrayList<>();
            return this;
        }

        public ExamSubmission build() {
            return new ExamSubmission(id, receiptId, room, candidateVerification, timeSpentSeconds,
                    mcAnsweredCount, mcTotalCount, mcCorrectCount, mcScore, essayAnsweredCount,
                    essayTotalCount, essayScore, totalScore, essayFeedback, status, sha256Digest,
                    submittedAt, answers);
        }
    }
}
