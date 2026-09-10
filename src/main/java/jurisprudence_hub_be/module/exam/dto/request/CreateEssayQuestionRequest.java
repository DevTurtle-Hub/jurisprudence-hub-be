package jurisprudence_hub_be.module.exam.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateEssayQuestionRequest {

    @JsonAlias({"orderIndex", "order"})
    private int order;

    private String title;

    private String context;

    private String prompt;

    private BigDecimal maxScore = BigDecimal.valueOf(30.0);

    @JsonAlias({"rubrics", "rubric"})
    private List<String> rubric = new ArrayList<>();

    public CreateEssayQuestionRequest() {
    }

    public CreateEssayQuestionRequest(int order, String title, String context, String prompt,
                                     BigDecimal maxScore, List<String> rubric) {
        this.order = order;
        this.title = title;
        this.context = context;
        this.prompt = prompt;
        this.maxScore = (maxScore != null) ? maxScore : BigDecimal.valueOf(30.0);
        this.rubric = (rubric != null) ? rubric : new ArrayList<>();
    }

    public static CreateEssayQuestionRequestBuilder builder() {
        return new CreateEssayQuestionRequestBuilder();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public List<String> getRubric() {
        if (rubric == null) {
            rubric = new ArrayList<>();
        }
        return rubric;
    }

    public void setRubric(List<String> rubric) {
        this.rubric = rubric;
    }

    public static class CreateEssayQuestionRequestBuilder {
        private int order;
        private String title;
        private String context;
        private String prompt;
        private BigDecimal maxScore = BigDecimal.valueOf(30.0);
        private List<String> rubric = new ArrayList<>();

        public CreateEssayQuestionRequestBuilder order(int order) {
            this.order = order;
            return this;
        }

        public CreateEssayQuestionRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CreateEssayQuestionRequestBuilder context(String context) {
            this.context = context;
            return this;
        }

        public CreateEssayQuestionRequestBuilder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public CreateEssayQuestionRequestBuilder maxScore(BigDecimal maxScore) {
            this.maxScore = (maxScore != null) ? maxScore : BigDecimal.valueOf(30.0);
            return this;
        }

        public CreateEssayQuestionRequestBuilder rubric(List<String> rubric) {
            this.rubric = (rubric != null) ? rubric : new ArrayList<>();
            return this;
        }

        public CreateEssayQuestionRequest build() {
            return new CreateEssayQuestionRequest(order, title, context, prompt, maxScore, rubric);
        }
    }
}
