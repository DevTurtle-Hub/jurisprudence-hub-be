package jurisprudence_hub_be.module.exam.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public class ExamSubmissionReceiptResponse {

    private String receiptId;
    private String roomId;
    private String roomCode;
    private String roomTitle;
    private CandidateDto candidate;
    private Instant submittedAt;
    private int timeSpentSeconds;
    private int mcAnsweredCount;
    private int mcTotalCount;
    private int mcCorrectCount;
    private BigDecimal mcScore;
    private int essayAnsweredCount;
    private int essayTotalCount;
    private String sha256Digest;

    public ExamSubmissionReceiptResponse() {
    }

    public ExamSubmissionReceiptResponse(String receiptId, String roomId, String roomCode, String roomTitle,
                                        CandidateDto candidate, Instant submittedAt, int timeSpentSeconds,
                                        int mcAnsweredCount, int mcTotalCount, int mcCorrectCount,
                                        BigDecimal mcScore, int essayAnsweredCount, int essayTotalCount,
                                        String sha256Digest) {
        this.receiptId = receiptId;
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomTitle = roomTitle;
        this.candidate = candidate;
        this.submittedAt = submittedAt;
        this.timeSpentSeconds = timeSpentSeconds;
        this.mcAnsweredCount = mcAnsweredCount;
        this.mcTotalCount = mcTotalCount;
        this.mcCorrectCount = mcCorrectCount;
        this.mcScore = mcScore;
        this.essayAnsweredCount = essayAnsweredCount;
        this.essayTotalCount = essayTotalCount;
        this.sha256Digest = sha256Digest;
    }

    public static ExamSubmissionReceiptResponseBuilder builder() {
        return new ExamSubmissionReceiptResponseBuilder();
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public CandidateDto getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateDto candidate) {
        this.candidate = candidate;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
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

    public String getSha256Digest() {
        return sha256Digest;
    }

    public void setSha256Digest(String sha256Digest) {
        this.sha256Digest = sha256Digest;
    }

    public static class ExamSubmissionReceiptResponseBuilder {
        private String receiptId;
        private String roomId;
        private String roomCode;
        private String roomTitle;
        private CandidateDto candidate;
        private Instant submittedAt;
        private int timeSpentSeconds;
        private int mcAnsweredCount;
        private int mcTotalCount;
        private int mcCorrectCount;
        private BigDecimal mcScore;
        private int essayAnsweredCount;
        private int essayTotalCount;
        private String sha256Digest;

        public ExamSubmissionReceiptResponseBuilder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder roomCode(String roomCode) {
            this.roomCode = roomCode;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder roomTitle(String roomTitle) {
            this.roomTitle = roomTitle;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder candidate(CandidateDto candidate) {
            this.candidate = candidate;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder submittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder timeSpentSeconds(int timeSpentSeconds) {
            this.timeSpentSeconds = timeSpentSeconds;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder mcAnsweredCount(int mcAnsweredCount) {
            this.mcAnsweredCount = mcAnsweredCount;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder mcTotalCount(int mcTotalCount) {
            this.mcTotalCount = mcTotalCount;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder mcCorrectCount(int mcCorrectCount) {
            this.mcCorrectCount = mcCorrectCount;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder mcScore(BigDecimal mcScore) {
            this.mcScore = mcScore;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder essayAnsweredCount(int essayAnsweredCount) {
            this.essayAnsweredCount = essayAnsweredCount;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder essayTotalCount(int essayTotalCount) {
            this.essayTotalCount = essayTotalCount;
            return this;
        }

        public ExamSubmissionReceiptResponseBuilder sha256Digest(String sha256Digest) {
            this.sha256Digest = sha256Digest;
            return this;
        }

        public ExamSubmissionReceiptResponse build() {
            return new ExamSubmissionReceiptResponse(receiptId, roomId, roomCode, roomTitle, candidate,
                    submittedAt, timeSpentSeconds, mcAnsweredCount, mcTotalCount, mcCorrectCount,
                    mcScore, essayAnsweredCount, essayTotalCount, sha256Digest);
        }
    }
}
