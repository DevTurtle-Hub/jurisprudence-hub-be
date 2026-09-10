package jurisprudence_hub_be.module.exam.dto.draft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;

import java.util.ArrayList;
import java.util.List;

public class AddDraftQuestionRequest {
    private Integer questionNumber;

    @NotNull(message = "Loại câu hỏi không được để trống")
    private DraftQuestionType type;

    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;

    private String context;

    private List<DraftOptionDto> options = new ArrayList<>();

    private String correctOptionKey;
    private String answer;

    public AddDraftQuestionRequest() {
    }

    public AddDraftQuestionRequest(Integer questionNumber, DraftQuestionType type, String content,
                                  String context, List<DraftOptionDto> options,
                                  String correctOptionKey, String answer) {
        this.questionNumber = questionNumber;
        this.type = type;
        this.content = content;
        this.context = context;
        this.options = (options != null) ? options : new ArrayList<>();
        this.correctOptionKey = correctOptionKey;
        this.answer = answer;
    }

    public static AddDraftQuestionRequestBuilder builder() {
        return new AddDraftQuestionRequestBuilder();
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
        if (options == null) {
            options = new ArrayList<>();
        }
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

    public static class AddDraftQuestionRequestBuilder {
        private Integer questionNumber;
        private DraftQuestionType type;
        private String content;
        private String context;
        private List<DraftOptionDto> options = new ArrayList<>();
        private String correctOptionKey;
        private String answer;

        public AddDraftQuestionRequestBuilder questionNumber(Integer questionNumber) {
            this.questionNumber = questionNumber;
            return this;
        }

        public AddDraftQuestionRequestBuilder type(DraftQuestionType type) {
            this.type = type;
            return this;
        }

        public AddDraftQuestionRequestBuilder content(String content) {
            this.content = content;
            return this;
        }

        public AddDraftQuestionRequestBuilder context(String context) {
            this.context = context;
            return this;
        }

        public AddDraftQuestionRequestBuilder options(List<DraftOptionDto> options) {
            this.options = (options != null) ? options : new ArrayList<>();
            return this;
        }

        public AddDraftQuestionRequestBuilder correctOptionKey(String correctOptionKey) {
            this.correctOptionKey = correctOptionKey;
            return this;
        }

        public AddDraftQuestionRequestBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public AddDraftQuestionRequest build() {
            return new AddDraftQuestionRequest(questionNumber, type, content, context, options, correctOptionKey, answer);
        }
    }
}
