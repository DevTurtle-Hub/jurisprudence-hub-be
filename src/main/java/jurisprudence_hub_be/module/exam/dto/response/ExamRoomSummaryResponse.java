package jurisprudence_hub_be.module.exam.dto.response;

import java.time.Instant;

public class ExamRoomSummaryResponse {
    private String id;
    private String code;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalAttempts;
    private String status;
    private ExamRoomPartsSummary partsSummary;
    private Instant createdAt;

    public ExamRoomSummaryResponse() {
    }

    public ExamRoomSummaryResponse(String id, String code, String title, String description,
                                   int durationMinutes, int totalAttempts, String status,
                                   ExamRoomPartsSummary partsSummary, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalAttempts = totalAttempts;
        this.status = status;
        this.partsSummary = partsSummary;
        this.createdAt = createdAt;
    }

    public static ExamRoomSummaryResponseBuilder builder() {
        return new ExamRoomSummaryResponseBuilder();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExamRoomPartsSummary getPartsSummary() {
        return partsSummary;
    }

    public void setPartsSummary(ExamRoomPartsSummary partsSummary) {
        this.partsSummary = partsSummary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class ExamRoomPartsSummary {
        private int mcCount;
        private int essayCount;

        public ExamRoomPartsSummary() {
        }

        public ExamRoomPartsSummary(int mcCount, int essayCount) {
            this.mcCount = mcCount;
            this.essayCount = essayCount;
        }

        public static ExamRoomPartsSummaryBuilder builder() {
            return new ExamRoomPartsSummaryBuilder();
        }

        public int getMcCount() {
            return mcCount;
        }

        public void setMcCount(int mcCount) {
            this.mcCount = mcCount;
        }

        public int getEssayCount() {
            return essayCount;
        }

        public void setEssayCount(int essayCount) {
            this.essayCount = essayCount;
        }

        public static class ExamRoomPartsSummaryBuilder {
            private int mcCount;
            private int essayCount;

            public ExamRoomPartsSummaryBuilder mcCount(int mcCount) {
                this.mcCount = mcCount;
                return this;
            }

            public ExamRoomPartsSummaryBuilder essayCount(int essayCount) {
                this.essayCount = essayCount;
                return this;
            }

            public ExamRoomPartsSummary build() {
                return new ExamRoomPartsSummary(mcCount, essayCount);
            }
        }
    }

    public static class ExamRoomSummaryResponseBuilder {
        private String id;
        private String code;
        private String title;
        private String description;
        private int durationMinutes;
        private int totalAttempts;
        private String status;
        private ExamRoomPartsSummary partsSummary;
        private Instant createdAt;

        public ExamRoomSummaryResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamRoomSummaryResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExamRoomSummaryResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamRoomSummaryResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExamRoomSummaryResponseBuilder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public ExamRoomSummaryResponseBuilder totalAttempts(int totalAttempts) {
            this.totalAttempts = totalAttempts;
            return this;
        }

        public ExamRoomSummaryResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ExamRoomSummaryResponseBuilder partsSummary(ExamRoomPartsSummary partsSummary) {
            this.partsSummary = partsSummary;
            return this;
        }

        public ExamRoomSummaryResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExamRoomSummaryResponse build() {
            return new ExamRoomSummaryResponse(id, code, title, description, durationMinutes, totalAttempts, status, partsSummary, createdAt);
        }
    }
}
