package jurisprudence_hub_be.module.exam.dto.draft;

import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;

import java.util.List;

public class UpdateDraftQuestionRequest {
    private Integer questionNumber;
    private DraftQuestionType type;
    private String content;
    private String context;
    private List<DraftOptionDto> options;
    private String correctOptionKey;
    private String answer;

    public UpdateDraftQuestionRequest() {
    }

    public UpdateDraftQuestionRequest(Integer questionNumber, DraftQuestionType type, String content,
                                      String context, List<DraftOptionDto> options,
                                      String correctOptionKey, String answer) {
        this.questionNumber = questionNumber;
        this.type = type;
        this.content = content;
        this.context = context;
        this.options = options;
        this.correctOptionKey = correctOptionKey;
        this.answer = answer;
    }

    public static UpdateDraftQuestionRequestBuilder builder() {
        return new UpdateDraftQuestionRequestBuilder();
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public DraftQuestionType getType() {
        return type;
    }

    public void setType(DraftQuestionType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<DraftOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<DraftOptionDto> options) {
        this.options = options;
    }

    public String getCorrectOptionKey() {
        return correctOptionKey;
    }

    public void setCorrectOptionKey(String correctOptionKey) {
        this.correctOptionKey = correctOptionKey;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static class UpdateDraftQuestionRequestBuilder {
        private Integer questionNumber;
        private DraftQuestionType type;
        private String content;
        private String context;
        private List<DraftOptionDto> options;
        private String correctOptionKey;
        private String answer;

        public UpdateDraftQuestionRequestBuilder questionNumber(Integer questionNumber) {
            this.questionNumber = questionNumber;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder type(DraftQuestionType type) {
            this.type = type;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder content(String content) {
            this.content = content;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder context(String context) {
            this.context = context;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder options(List<DraftOptionDto> options) {
            this.options = options;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder correctOptionKey(String correctOptionKey) {
            this.correctOptionKey = correctOptionKey;
            return this;
        }

        public UpdateDraftQuestionRequestBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public UpdateDraftQuestionRequest build() {
            return new UpdateDraftQuestionRequest(questionNumber, type, content, context, options, correctOptionKey, answer);
        }
    }
}
