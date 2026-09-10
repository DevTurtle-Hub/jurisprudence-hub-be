package jurisprudence_hub_be.module.exam.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GradeEssayRequest {

    @NotEmpty(message = "Danh sách điểm tự luận không được để trống")
    private List<EssayScoreItem> essayScores = new ArrayList<>();

    public GradeEssayRequest() {
    }

    public GradeEssayRequest(List<EssayScoreItem> essayScores) {
        this.essayScores = (essayScores != null) ? essayScores : new ArrayList<>();
    }

    public static GradeEssayRequestBuilder builder() {
        return new GradeEssayRequestBuilder();
    }

    public List<EssayScoreItem> getEssayScores() {
        if (essayScores == null) {
            essayScores = new ArrayList<>();
        }
        return essayScores;
    }

    public void setEssayScores(List<EssayScoreItem> essayScores) {
        this.essayScores = essayScores;
    }

    public static class EssayScoreItem {
        private String questionId;
        private BigDecimal score;
        private String comment;

        public EssayScoreItem() {
        }

        public EssayScoreItem(String questionId, BigDecimal score, String comment) {
            this.questionId = questionId;
            this.score = score;
            this.comment = comment;
        }

        public static EssayScoreItemBuilder builder() {
            return new EssayScoreItemBuilder();
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public static class EssayScoreItemBuilder {
            private String questionId;
            private BigDecimal score;
            private String comment;

            public EssayScoreItemBuilder questionId(String questionId) {
                this.questionId = questionId;
                return this;
            }

            public EssayScoreItemBuilder score(BigDecimal score) {
                this.score = score;
                return this;
            }

            public EssayScoreItemBuilder comment(String comment) {
                this.comment = comment;
                return this;
            }

            public EssayScoreItem build() {
                return new EssayScoreItem(questionId, score, comment);
            }
        }
    }

    public static class GradeEssayRequestBuilder {
        private List<EssayScoreItem> essayScores = new ArrayList<>();

        public GradeEssayRequestBuilder essayScores(List<EssayScoreItem> essayScores) {
            this.essayScores = (essayScores != null) ? essayScores : new ArrayList<>();
            return this;
        }

        public GradeEssayRequest build() {
            return new GradeEssayRequest(essayScores);
        }
    }
}
