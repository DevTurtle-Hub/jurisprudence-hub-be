package jurisprudence_hub_be.module.exam.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDetailResponse {

    private String id;
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
    private BigDecimal essayScore;
    private BigDecimal totalScore;
    private String essayFeedback;

    private String status;
    private String sha256Digest;

    private List<SubmissionAnswerDetailDto> answers = new ArrayList<>();

    public SubmissionDetailResponse() {
    }

    public SubmissionDetailResponse(String id, String receiptId, String roomId, String roomCode,
                                    String roomTitle, CandidateDto candidate, Instant submittedAt,
                                    int timeSpentSeconds, int mcAnsweredCount, int mcTotalCount,
                                    int mcCorrectCount, BigDecimal mcScore, int essayAnsweredCount,
                                    int essayTotalCount, BigDecimal essayScore, BigDecimal totalScore,
                                    String essayFeedback, String status, String sha256Digest,
                                    List<SubmissionAnswerDetailDto> answers) {
        this.id = id;
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
        this.essayScore = essayScore;
        this.totalScore = totalScore;
        this.essayFeedback = essayFeedback;
        this.status = status;
        this.sha256Digest = sha256Digest;
        this.answers = (answers != null) ? answers : new ArrayList<>();
    }

    public static SubmissionDetailResponseBuilder builder() {
        return new SubmissionDetailResponseBuilder();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSha256Digest() {
        return sha256Digest;
    }

    public void setSha256Digest(String sha256Digest) {
        this.sha256Digest = sha256Digest;
    }

    public List<SubmissionAnswerDetailDto> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        return answers;
    }

    public void setAnswers(List<SubmissionAnswerDetailDto> answers) {
        this.answers = answers;
    }

    public static class SubmissionAnswerDetailDto {
        private String id;
        private String questionType;
        private String questionId;
        private String chosenOption;
        private Boolean isCorrect;
        private String essayContent;
        private BigDecimal essayScore;

        public SubmissionAnswerDetailDto() {
        }

        public SubmissionAnswerDetailDto(String id, String questionType, String questionId,
                                         String chosenOption, Boolean isCorrect,
                                         String essayContent, BigDecimal essayScore) {
            this.id = id;
            this.questionType = questionType;
            this.questionId = questionId;
            this.chosenOption = chosenOption;
            this.isCorrect = isCorrect;
            this.essayContent = essayContent;
            this.essayScore = essayScore;
        }

        public static SubmissionAnswerDetailDtoBuilder builder() {
            return new SubmissionAnswerDetailDtoBuilder();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestionType() {
            return questionType;
        }

        public void setQuestionType(String questionType) {
            this.questionType = questionType;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getChosenOption() {
            return chosenOption;
        }

        public void setChosenOption(String chosenOption) {
            this.chosenOption = chosenOption;
        }

        public Boolean getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

        public String getEssayContent() {
            return essayContent;
        }

        public void setEssayContent(String essayContent) {
            this.essayContent = essayContent;
        }

        public BigDecimal getEssayScore() {
            return essayScore;
        }

        public void setEssayScore(BigDecimal essayScore) {
            this.essayScore = essayScore;
        }

        public static class SubmissionAnswerDetailDtoBuilder {
            private String id;
            private String questionType;
            private String questionId;
            private String chosenOption;
            private Boolean isCorrect;
            private String essayContent;
            private BigDecimal essayScore;

            public SubmissionAnswerDetailDtoBuilder id(String id) {
                this.id = id;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder questionType(String questionType) {
                this.questionType = questionType;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder questionId(String questionId) {
                this.questionId = questionId;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder chosenOption(String chosenOption) {
                this.chosenOption = chosenOption;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder isCorrect(Boolean isCorrect) {
                this.isCorrect = isCorrect;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder essayContent(String essayContent) {
                this.essayContent = essayContent;
                return this;
            }

            public SubmissionAnswerDetailDtoBuilder essayScore(BigDecimal essayScore) {
                this.essayScore = essayScore;
                return this;
            }

            public SubmissionAnswerDetailDto build() {
                return new SubmissionAnswerDetailDto(id, questionType, questionId, chosenOption, isCorrect, essayContent, essayScore);
            }
        }
    }

    public static class SubmissionDetailResponseBuilder {
        private String id;
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
        private BigDecimal essayScore;
        private BigDecimal totalScore;
        private String essayFeedback;
        private String status;
        private String sha256Digest;
        private List<SubmissionAnswerDetailDto> answers = new ArrayList<>();

        public SubmissionDetailResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public SubmissionDetailResponseBuilder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public SubmissionDetailResponseBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public SubmissionDetailResponseBuilder roomCode(String roomCode) {
            this.roomCode = roomCode;
            return this;
        }

        public SubmissionDetailResponseBuilder roomTitle(String roomTitle) {
            this.roomTitle = roomTitle;
            return this;
        }

        public SubmissionDetailResponseBuilder candidate(CandidateDto candidate) {
            this.candidate = candidate;
            return this;
        }

        public SubmissionDetailResponseBuilder submittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public SubmissionDetailResponseBuilder timeSpentSeconds(int timeSpentSeconds) {
            this.timeSpentSeconds = timeSpentSeconds;
            return this;
        }

        public SubmissionDetailResponseBuilder mcAnsweredCount(int mcAnsweredCount) {
            this.mcAnsweredCount = mcAnsweredCount;
            return this;
        }

        public SubmissionDetailResponseBuilder mcTotalCount(int mcTotalCount) {
            this.mcTotalCount = mcTotalCount;
            return this;
        }

        public SubmissionDetailResponseBuilder mcCorrectCount(int mcCorrectCount) {
            this.mcCorrectCount = mcCorrectCount;
            return this;
        }

        public SubmissionDetailResponseBuilder mcScore(BigDecimal mcScore) {
            this.mcScore = mcScore;
            return this;
        }

        public SubmissionDetailResponseBuilder essayAnsweredCount(int essayAnsweredCount) {
            this.essayAnsweredCount = essayAnsweredCount;
            return this;
        }

        public SubmissionDetailResponseBuilder essayTotalCount(int essayTotalCount) {
            this.essayTotalCount = essayTotalCount;
            return this;
        }

        public SubmissionDetailResponseBuilder essayScore(BigDecimal essayScore) {
            this.essayScore = essayScore;
            return this;
        }

        public SubmissionDetailResponseBuilder totalScore(BigDecimal totalScore) {
            this.totalScore = totalScore;
            return this;
        }

        public SubmissionDetailResponseBuilder essayFeedback(String essayFeedback) {
            this.essayFeedback = essayFeedback;
            return this;
        }

        public SubmissionDetailResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SubmissionDetailResponseBuilder sha256Digest(String sha256Digest) {
            this.sha256Digest = sha256Digest;
            return this;
        }

        public SubmissionDetailResponseBuilder answers(List<SubmissionAnswerDetailDto> answers) {
            this.answers = (answers != null) ? answers : new ArrayList<>();
            return this;
        }

        public SubmissionDetailResponse build() {
            return new SubmissionDetailResponse(id, receiptId, roomId, roomCode, roomTitle,
                    candidate, submittedAt, timeSpentSeconds, mcAnsweredCount, mcTotalCount,
                    mcCorrectCount, mcScore, essayAnsweredCount, essayTotalCount, essayScore,
                    totalScore, essayFeedback, status, sha256Digest, answers);
        }
    }
}
