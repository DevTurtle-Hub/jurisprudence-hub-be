package jurisprudence_hub_be.module.exam.dto.draft;

import java.time.Instant;

public class ConfirmImportResponse {
    private String examId;
    private String examCode;
    private String title;
    private int totalQuestionsImported;
    private int essayCount;
    private int mcCount;
    private int shortAnswerCount;
    private String status;
    private Instant confirmedAt;

    public ConfirmImportResponse() {
    }

    public ConfirmImportResponse(String examId, String examCode, String title, int totalQuestionsImported,
                                 int essayCount, int mcCount, int shortAnswerCount, String status, Instant confirmedAt) {
        this.examId = examId;
        this.examCode = examCode;
        this.title = title;
        this.totalQuestionsImported = totalQuestionsImported;
        this.essayCount = essayCount;
        this.mcCount = mcCount;
        this.shortAnswerCount = shortAnswerCount;
        this.status = status;
        this.confirmedAt = confirmedAt;
    }

    public static ConfirmImportResponseBuilder builder() {
        return new ConfirmImportResponseBuilder();
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalQuestionsImported() {
        return totalQuestionsImported;
    }

    public void setTotalQuestionsImported(int totalQuestionsImported) {
        this.totalQuestionsImported = totalQuestionsImported;
    }

    public int getEssayCount() {
        return essayCount;
    }

    public void setEssayCount(int essayCount) {
        this.essayCount = essayCount;
    }

    public int getMcCount() {
        return mcCount;
    }

    public void setMcCount(int mcCount) {
        this.mcCount = mcCount;
    }

    public int getShortAnswerCount() {
        return shortAnswerCount;
    }

    public void setShortAnswerCount(int shortAnswerCount) {
        this.shortAnswerCount = shortAnswerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public static class ConfirmImportResponseBuilder {
        private String examId;
        private String examCode;
        private String title;
        private int totalQuestionsImported;
        private int essayCount;
        private int mcCount;
        private int shortAnswerCount;
        private String status;
        private Instant confirmedAt;

        public ConfirmImportResponseBuilder examId(String examId) {
            this.examId = examId;
            return this;
        }

        public ConfirmImportResponseBuilder examCode(String examCode) {
            this.examCode = examCode;
            return this;
        }

        public ConfirmImportResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ConfirmImportResponseBuilder totalQuestionsImported(int totalQuestionsImported) {
            this.totalQuestionsImported = totalQuestionsImported;
            return this;
        }

        public ConfirmImportResponseBuilder essayCount(int essayCount) {
            this.essayCount = essayCount;
            return this;
        }

        public ConfirmImportResponseBuilder mcCount(int mcCount) {
            this.mcCount = mcCount;
            return this;
        }

        public ConfirmImportResponseBuilder shortAnswerCount(int shortAnswerCount) {
            this.shortAnswerCount = shortAnswerCount;
            return this;
        }

        public ConfirmImportResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ConfirmImportResponseBuilder confirmedAt(Instant confirmedAt) {
            this.confirmedAt = confirmedAt;
            return this;
        }

        public ConfirmImportResponse build() {
            return new ConfirmImportResponse(examId, examCode, title, totalQuestionsImported, essayCount, mcCount, shortAnswerCount, status, confirmedAt);
        }
    }
}
