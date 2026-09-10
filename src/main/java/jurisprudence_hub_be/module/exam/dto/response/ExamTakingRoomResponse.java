package jurisprudence_hub_be.module.exam.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO dành riêng cho màn hình làm bài thi.
 * ĐẢM BẢO AN TOÀN TUYỆT ĐỐI: Không chứa correctAnswer, explanation, legalReference.
 */
public class ExamTakingRoomResponse {

    private String id;
    private String code;
    private String title;
    private String description;
    private int durationMinutes;
    private List<SanitizedMcQuestion> multipleChoiceQuestions = new ArrayList<>();
    private List<SanitizedEssayQuestion> essayQuestions = new ArrayList<>();

    public ExamTakingRoomResponse() {
    }

    public ExamTakingRoomResponse(String id, String code, String title, String description,
                                  int durationMinutes, List<SanitizedMcQuestion> multipleChoiceQuestions,
                                  List<SanitizedEssayQuestion> essayQuestions) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
        this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
    }

    public static ExamTakingRoomResponseBuilder builder() {
        return new ExamTakingRoomResponseBuilder();
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

    public List<SanitizedMcQuestion> getMultipleChoiceQuestions() {
        if (multipleChoiceQuestions == null) {
            multipleChoiceQuestions = new ArrayList<>();
        }
        return multipleChoiceQuestions;
    }

    public void setMultipleChoiceQuestions(List<SanitizedMcQuestion> multipleChoiceQuestions) {
        this.multipleChoiceQuestions = multipleChoiceQuestions;
    }

    public List<SanitizedEssayQuestion> getEssayQuestions() {
        if (essayQuestions == null) {
            essayQuestions = new ArrayList<>();
        }
        return essayQuestions;
    }

    public void setEssayQuestions(List<SanitizedEssayQuestion> essayQuestions) {
        this.essayQuestions = essayQuestions;
    }

    public static class SanitizedMcQuestion {
        private String id;
        private int order;
        private String context;
        private String question;
        private List<SanitizedMcOption> options = new ArrayList<>();

        public SanitizedMcQuestion() {
        }

        public SanitizedMcQuestion(String id, int order, String question, List<SanitizedMcOption> options) {
            this(id, order, null, question, options);
        }

        public SanitizedMcQuestion(String id, int order, String context, String question, List<SanitizedMcOption> options) {
            this.id = id;
            this.order = order;
            this.context = context;
            this.question = question;
            this.options = (options != null) ? options : new ArrayList<>();
        }

        public static SanitizedMcQuestionBuilder builder() {
            return new SanitizedMcQuestionBuilder();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<SanitizedMcOption> getOptions() {
            if (options == null) {
                options = new ArrayList<>();
            }
            return options;
        }

        public void setOptions(List<SanitizedMcOption> options) {
            this.options = options;
        }

        public static class SanitizedMcQuestionBuilder {
            private String id;
            private int order;
            private String context;
            private String question;
            private List<SanitizedMcOption> options = new ArrayList<>();

            public SanitizedMcQuestionBuilder id(String id) {
                this.id = id;
                return this;
            }

            public SanitizedMcQuestionBuilder order(int order) {
                this.order = order;
                return this;
            }

            public SanitizedMcQuestionBuilder context(String context) {
                this.context = context;
                return this;
            }

            public SanitizedMcQuestionBuilder question(String question) {
                this.question = question;
                return this;
            }

            public SanitizedMcQuestionBuilder options(List<SanitizedMcOption> options) {
                this.options = (options != null) ? options : new ArrayList<>();
                return this;
            }

            public SanitizedMcQuestion build() {
                return new SanitizedMcQuestion(id, order, context, question, options);
            }
        }
    }

    public static class SanitizedMcOption {
        private String id;
        private String label;
        private String text;

        public SanitizedMcOption() {
        }

        public SanitizedMcOption(String id, String label, String text) {
            this.id = id;
            this.label = label;
            this.text = text;
        }

        public static SanitizedMcOptionBuilder builder() {
            return new SanitizedMcOptionBuilder();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public static class SanitizedMcOptionBuilder {
            private String id;
            private String label;
            private String text;

            public SanitizedMcOptionBuilder id(String id) {
                this.id = id;
                return this;
            }

            public SanitizedMcOptionBuilder label(String label) {
                this.label = label;
                return this;
            }

            public SanitizedMcOptionBuilder text(String text) {
                this.text = text;
                return this;
            }

            public SanitizedMcOption build() {
                return new SanitizedMcOption(id, label, text);
            }
        }
    }

    public static class SanitizedEssayQuestion {
        private String id;
        private int order;
        private String title;
        private String context;
        private String prompt;
        private BigDecimal maxScore;
        private List<String> rubric = new ArrayList<>();

        public SanitizedEssayQuestion() {
        }

        public SanitizedEssayQuestion(String id, int order, String title, String context,
                                      String prompt, BigDecimal maxScore, List<String> rubric) {
            this.id = id;
            this.order = order;
            this.title = title;
            this.context = context;
            this.prompt = prompt;
            this.maxScore = maxScore;
            this.rubric = (rubric != null) ? rubric : new ArrayList<>();
        }

        public static SanitizedEssayQuestionBuilder builder() {
            return new SanitizedEssayQuestionBuilder();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public static class SanitizedEssayQuestionBuilder {
            private String id;
            private int order;
            private String title;
            private String context;
            private String prompt;
            private BigDecimal maxScore;
            private List<String> rubric = new ArrayList<>();

            public SanitizedEssayQuestionBuilder id(String id) {
                this.id = id;
                return this;
            }

            public SanitizedEssayQuestionBuilder order(int order) {
                this.order = order;
                return this;
            }

            public SanitizedEssayQuestionBuilder title(String title) {
                this.title = title;
                return this;
            }

            public SanitizedEssayQuestionBuilder context(String context) {
                this.context = context;
                return this;
            }

            public SanitizedEssayQuestionBuilder prompt(String prompt) {
                this.prompt = prompt;
                return this;
            }

            public SanitizedEssayQuestionBuilder maxScore(BigDecimal maxScore) {
                this.maxScore = maxScore;
                return this;
            }

            public SanitizedEssayQuestionBuilder rubric(List<String> rubric) {
                this.rubric = (rubric != null) ? rubric : new ArrayList<>();
                return this;
            }

            public SanitizedEssayQuestion build() {
                return new SanitizedEssayQuestion(id, order, title, context, prompt, maxScore, rubric);
            }
        }
    }

    public static class ExamTakingRoomResponseBuilder {
        private String id;
        private String code;
        private String title;
        private String description;
        private int durationMinutes;
        private List<SanitizedMcQuestion> multipleChoiceQuestions = new ArrayList<>();
        private List<SanitizedEssayQuestion> essayQuestions = new ArrayList<>();

        public ExamTakingRoomResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamTakingRoomResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExamTakingRoomResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamTakingRoomResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExamTakingRoomResponseBuilder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public ExamTakingRoomResponseBuilder multipleChoiceQuestions(List<SanitizedMcQuestion> multipleChoiceQuestions) {
            this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
            return this;
        }

        public ExamTakingRoomResponseBuilder essayQuestions(List<SanitizedEssayQuestion> essayQuestions) {
            this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
            return this;
        }

        public ExamTakingRoomResponse build() {
            return new ExamTakingRoomResponse(id, code, title, description, durationMinutes, multipleChoiceQuestions, essayQuestions);
        }
    }
}
