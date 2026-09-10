package jurisprudence_hub_be.module.exam.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExamRoomResponse {

    private String id;
    private String code;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalAttempts;
    private String status;
    private ExamRoomSummaryResponse.ExamRoomPartsSummary partsSummary;
    private Instant createdAt;
    private List<McQuestionDetail> multipleChoiceQuestions = new ArrayList<>();
    private List<EssayQuestionDetail> essayQuestions = new ArrayList<>();

    public ExamRoomResponse() {
    }

    public ExamRoomResponse(String id, String code, String title, String description,
                            int durationMinutes, int totalAttempts, String status,
                            ExamRoomSummaryResponse.ExamRoomPartsSummary partsSummary,
                            Instant createdAt, List<McQuestionDetail> multipleChoiceQuestions,
                            List<EssayQuestionDetail> essayQuestions) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalAttempts = totalAttempts;
        this.status = status;
        this.partsSummary = partsSummary;
        this.createdAt = createdAt;
         this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
        this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
    }

    public static ExamRoomResponseBuilder builder() {
        return new ExamRoomResponseBuilder();
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

    public ExamRoomSummaryResponse.ExamRoomPartsSummary getPartsSummary() {
        return partsSummary;
    }

    public void setPartsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary partsSummary) {
        this.partsSummary = partsSummary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<McQuestionDetail> getMultipleChoiceQuestions() {
        if (multipleChoiceQuestions == null) {
            multipleChoiceQuestions = new ArrayList<>();
        }
        return multipleChoiceQuestions;
    }

    public void setMultipleChoiceQuestions(List<McQuestionDetail> multipleChoiceQuestions) {
        this.multipleChoiceQuestions = multipleChoiceQuestions;
    }

    public List<EssayQuestionDetail> getEssayQuestions() {
        if (essayQuestions == null) {
            essayQuestions = new ArrayList<>();
        }
        return essayQuestions;
    }

    public void setEssayQuestions(List<EssayQuestionDetail> essayQuestions) {
        this.essayQuestions = essayQuestions;
    }

    public static class McQuestionDetail {
        private String id;
        private int order;
        private String question;
        private String context;
        private List<McOptionDetail> options = new ArrayList<>();
        private String correctAnswer;
        private String explanation;
        private String legalReference;

        public McQuestionDetail() {
        }

        public McQuestionDetail(String id, int order, String question, String context, List<McOptionDetail> options,
                                String correctAnswer, String explanation, String legalReference) {
            this.id = id;
            this.order = order;
            this.question = question;
            this.context = context;
            this.options = (options != null) ? options : new ArrayList<>();
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
            this.legalReference = legalReference;
        }

        public McQuestionDetail(String id, int order, String question, List<McOptionDetail> options,
                                String correctAnswer, String explanation, String legalReference) {
            this(id, order, question, null, options, correctAnswer, explanation, legalReference);
        }

        public static McQuestionDetailBuilder builder() {
            return new McQuestionDetailBuilder();
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

        public List<McOptionDetail> getOptions() {
            if (options == null) {
                options = new ArrayList<>();
            }
            return options;
        }

        public void setOptions(List<McOptionDetail> options) {
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

        public static class McQuestionDetailBuilder {
            private String id;
            private int order;
            private String question;
            private String context;
            private List<McOptionDetail> options = new ArrayList<>();
            private String correctAnswer;
            private String explanation;
            private String legalReference;

            public McQuestionDetailBuilder id(String id) {
                this.id = id;
                return this;
            }

            public McQuestionDetailBuilder order(int order) {
                this.order = order;
                return this;
            }

            public McQuestionDetailBuilder question(String question) {
                this.question = question;
                return this;
            }

            public McQuestionDetailBuilder context(String context) {
                this.context = context;
                return this;
            }

            public McQuestionDetailBuilder options(List<McOptionDetail> options) {
                this.options = (options != null) ? options : new ArrayList<>();
                return this;
            }

            public McQuestionDetailBuilder correctAnswer(String correctAnswer) {
                this.correctAnswer = correctAnswer;
                return this;
            }

            public McQuestionDetailBuilder explanation(String explanation) {
                this.explanation = explanation;
                return this;
            }

            public McQuestionDetailBuilder legalReference(String legalReference) {
                this.legalReference = legalReference;
                return this;
            }

            public McQuestionDetail build() {
                return new McQuestionDetail(id, order, question, context, options, correctAnswer, explanation, legalReference);
            }
        }
    }

    public static class McOptionDetail {
        private String id;
        private String label;
        private String text;

        public McOptionDetail() {
        }

        public McOptionDetail(String id, String label, String text) {
            this.id = id;
            this.label = label;
            this.text = text;
        }

        public static McOptionDetailBuilder builder() {
            return new McOptionDetailBuilder();
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

        public static class McOptionDetailBuilder {
            private String id;
            private String label;
            private String text;

            public McOptionDetailBuilder id(String id) {
                this.id = id;
                return this;
            }

            public McOptionDetailBuilder label(String label) {
                this.label = label;
                return this;
            }

            public McOptionDetailBuilder text(String text) {
                this.text = text;
                return this;
            }

            public McOptionDetail build() {
                return new McOptionDetail(id, label, text);
            }
        }
    }

    public static class EssayQuestionDetail {
        private String id;
        private int order;
        private String title;
        private String context;
        private String prompt;
        private BigDecimal maxScore;
        private List<String> rubric = new ArrayList<>();

        public EssayQuestionDetail() {
        }

        public EssayQuestionDetail(String id, int order, String title, String context, String prompt,
                                   BigDecimal maxScore, List<String> rubric) {
            this.id = id;
            this.order = order;
            this.title = title;
            this.context = context;
            this.prompt = prompt;
            this.maxScore = maxScore;
            this.rubric = (rubric != null) ? rubric : new ArrayList<>();
        }

        public static EssayQuestionDetailBuilder builder() {
            return new EssayQuestionDetailBuilder();
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

        public static class EssayQuestionDetailBuilder {
            private String id;
            private int order;
            private String title;
            private String context;
            private String prompt;
            private BigDecimal maxScore;
            private List<String> rubric = new ArrayList<>();

            public EssayQuestionDetailBuilder id(String id) {
                this.id = id;
                return this;
            }

            public EssayQuestionDetailBuilder order(int order) {
                this.order = order;
                return this;
            }

            public EssayQuestionDetailBuilder title(String title) {
                this.title = title;
                return this;
            }

            public EssayQuestionDetailBuilder context(String context) {
                this.context = context;
                return this;
            }

            public EssayQuestionDetailBuilder prompt(String prompt) {
                this.prompt = prompt;
                return this;
            }

            public EssayQuestionDetailBuilder maxScore(BigDecimal maxScore) {
                this.maxScore = maxScore;
                return this;
            }

            public EssayQuestionDetailBuilder rubric(List<String> rubric) {
                this.rubric = (rubric != null) ? rubric : new ArrayList<>();
                return this;
            }

            public EssayQuestionDetail build() {
                return new EssayQuestionDetail(id, order, title, context, prompt, maxScore, rubric);
            }
        }
    }

    public static class ExamRoomResponseBuilder {
        private String id;
        private String code;
        private String title;
        private String description;
        private int durationMinutes;
        private int totalAttempts;
        private String status;
        private ExamRoomSummaryResponse.ExamRoomPartsSummary partsSummary;
        private Instant createdAt;
        private List<McQuestionDetail> multipleChoiceQuestions = new ArrayList<>();
        private List<EssayQuestionDetail> essayQuestions = new ArrayList<>();

        public ExamRoomResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ExamRoomResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExamRoomResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamRoomResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ExamRoomResponseBuilder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public ExamRoomResponseBuilder totalAttempts(int totalAttempts) {
            this.totalAttempts = totalAttempts;
            return this;
        }

        public ExamRoomResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ExamRoomResponseBuilder partsSummary(ExamRoomSummaryResponse.ExamRoomPartsSummary partsSummary) {
            this.partsSummary = partsSummary;
            return this;
        }

        public ExamRoomResponseBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExamRoomResponseBuilder multipleChoiceQuestions(List<McQuestionDetail> multipleChoiceQuestions) {
            this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
            return this;
        }

        public ExamRoomResponseBuilder essayQuestions(List<EssayQuestionDetail> essayQuestions) {
            this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
            return this;
        }

        public ExamRoomResponse build() {
            return new ExamRoomResponse(id, code, title, description, durationMinutes, totalAttempts, status,
                    partsSummary, createdAt, multipleChoiceQuestions, essayQuestions);
        }
    }
}
