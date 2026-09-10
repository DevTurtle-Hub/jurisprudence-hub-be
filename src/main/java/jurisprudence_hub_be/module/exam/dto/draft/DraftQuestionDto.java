package jurisprudence_hub_be.module.exam.dto.draft;

import jurisprudence_hub_be.module.exam.enums.DraftQuestionType;
import jurisprudence_hub_be.module.exam.enums.ParsingStatus;

import java.util.ArrayList;
import java.util.List;

public class DraftQuestionDto {
    private String temporaryId;
    private int questionNumber;
    private DraftQuestionType type;
    private String section;
    private String context;
    private String content;
    private boolean hasAnswer;
    private List<DraftOptionDto> options = new ArrayList<>();
    private String answer;
    private ParsingStatus parsingStatus = ParsingStatus.SUCCESS;
    private List<String> warnings = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public DraftQuestionDto() {
    }

    public DraftQuestionDto(String temporaryId, int questionNumber, DraftQuestionType type,
                            String section, String context, String content, boolean hasAnswer,
                            List<DraftOptionDto> options, String answer, ParsingStatus parsingStatus,
                            List<String> warnings, List<String> errors) {
        this.temporaryId = temporaryId;
        this.questionNumber = questionNumber;
        this.type = type;
        this.section = section;
        this.context = context;
        this.content = content;
        this.hasAnswer = hasAnswer;
        this.options = (options != null) ? options : new ArrayList<>();
        this.answer = answer;
        this.parsingStatus = (parsingStatus != null) ? parsingStatus : ParsingStatus.SUCCESS;
        this.warnings = (warnings != null) ? warnings : new ArrayList<>();
        this.errors = (errors != null) ? errors : new ArrayList<>();
    }

    public static DraftQuestionDtoBuilder builder() {
        return new DraftQuestionDtoBuilder();
    }

    public String getTemporaryId() {
        return temporaryId;
    }

    public void setTemporaryId(String temporaryId) {
        this.temporaryId = temporaryId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public DraftQuestionType getType() {
        return type;
    }

    public void setType(DraftQuestionType type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ParsingStatus getParsingStatus() {
        return parsingStatus;
    }

    public void setParsingStatus(ParsingStatus parsingStatus) {
        this.parsingStatus = parsingStatus;
    }

    public List<String> getWarnings() {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public static class DraftQuestionDtoBuilder {
        private String temporaryId;
        private int questionNumber;
        private DraftQuestionType type;
        private String section;
        private String context;
        private String content;
        private boolean hasAnswer;
        private List<DraftOptionDto> options = new ArrayList<>();
        private String answer;
        private ParsingStatus parsingStatus = ParsingStatus.SUCCESS;
        private List<String> warnings = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public DraftQuestionDtoBuilder temporaryId(String temporaryId) {
            this.temporaryId = temporaryId;
            return this;
        }

        public DraftQuestionDtoBuilder questionNumber(int questionNumber) {
            this.questionNumber = questionNumber;
            return this;
        }

        public DraftQuestionDtoBuilder type(DraftQuestionType type) {
            this.type = type;
            return this;
        }

        public DraftQuestionDtoBuilder section(String section) {
            this.section = section;
            return this;
        }

        public DraftQuestionDtoBuilder context(String context) {
            this.context = context;
            return this;
        }

        public DraftQuestionDtoBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DraftQuestionDtoBuilder hasAnswer(boolean hasAnswer) {
            this.hasAnswer = hasAnswer;
            return this;
        }

        public DraftQuestionDtoBuilder options(List<DraftOptionDto> options) {
            this.options = (options != null) ? options : new ArrayList<>();
            return this;
        }

        public DraftQuestionDtoBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public DraftQuestionDtoBuilder parsingStatus(ParsingStatus parsingStatus) {
            this.parsingStatus = parsingStatus;
            return this;
        }

        public DraftQuestionDtoBuilder warnings(List<String> warnings) {
            this.warnings = (warnings != null) ? warnings : new ArrayList<>();
            return this;
        }

        public DraftQuestionDtoBuilder errors(List<String> errors) {
            this.errors = (errors != null) ? errors : new ArrayList<>();
            return this;
        }

        public DraftQuestionDto build() {
            return new DraftQuestionDto(temporaryId, questionNumber, type, section, context, content,
                    hasAnswer, options, answer, parsingStatus, warnings, errors);
        }
    }
}
