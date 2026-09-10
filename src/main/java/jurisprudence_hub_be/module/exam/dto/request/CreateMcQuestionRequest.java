package jurisprudence_hub_be.module.exam.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

public class CreateMcQuestionRequest {

    @JsonAlias({"orderIndex", "order"})
    private int order;

    @JsonAlias({"questionText", "question"})
    private String question;

    @JsonAlias({"context", "scenario", "passage"})
    private String context;

    private List<CreateMcOptionRequest> options = new ArrayList<>();

    private String correctAnswer;

    private String explanation;

    private String legalReference;

    public CreateMcQuestionRequest() {
    }

    public CreateMcQuestionRequest(int order, String question, String context, List<CreateMcOptionRequest> options,
                                   String correctAnswer, String explanation, String legalReference) {
        this.order = order;
        this.question = question;
        this.context = context;
        this.options = (options != null) ? options : new ArrayList<>();
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.legalReference = legalReference;
    }

    public CreateMcQuestionRequest(int order, String question, List<CreateMcOptionRequest> options,
                                   String correctAnswer, String explanation, String legalReference) {
        this(order, question, null, options, correctAnswer, explanation, legalReference);
    }

    public static CreateMcQuestionRequestBuilder builder() {
        return new CreateMcQuestionRequestBuilder();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<CreateMcOptionRequest> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void setOptions(List<CreateMcOptionRequest> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getLegalReference() {
        return legalReference;
    }

    public void setLegalReference(String legalReference) {
        this.legalReference = legalReference;
    }

    public static class CreateMcQuestionRequestBuilder {
        private int order;
        private String question;
        private String context;
        private List<CreateMcOptionRequest> options = new ArrayList<>();
        private String correctAnswer;
        private String explanation;
        private String legalReference;

        public CreateMcQuestionRequestBuilder order(int order) {
            this.order = order;
            return this;
        }

        public CreateMcQuestionRequestBuilder question(String question) {
            this.question = question;
            return this;
        }

        public CreateMcQuestionRequestBuilder context(String context) {
            this.context = context;
            return this;
        }

        public CreateMcQuestionRequestBuilder options(List<CreateMcOptionRequest> options) {
            this.options = (options != null) ? options : new ArrayList<>();
            return this;
        }

        public CreateMcQuestionRequestBuilder correctAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
            return this;
        }

        public CreateMcQuestionRequestBuilder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public CreateMcQuestionRequestBuilder legalReference(String legalReference) {
            this.legalReference = legalReference;
            return this;
        }

        public CreateMcQuestionRequest build() {
            return new CreateMcQuestionRequest(order, question, context, options, correctAnswer, explanation, legalReference);
        }
    }
}
