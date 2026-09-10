package jurisprudence_hub_be.module.exam.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ParsedDocumentResponse {

    private String extractedTitle;
    private int suggestedDurationMinutes;
    private List<ParsedMcQuestion> multipleChoiceQuestions = new ArrayList<>();
    private List<ParsedEssayQuestion> essayQuestions = new ArrayList<>();

    public ParsedDocumentResponse() {}

    public ParsedDocumentResponse(String extractedTitle, int suggestedDurationMinutes, List<ParsedMcQuestion> multipleChoiceQuestions, List<ParsedEssayQuestion> essayQuestions) {
        this.extractedTitle = extractedTitle;
        this.suggestedDurationMinutes = suggestedDurationMinutes;
        this.multipleChoiceQuestions = multipleChoiceQuestions != null ? multipleChoiceQuestions : new ArrayList<>();
        this.essayQuestions = essayQuestions != null ? essayQuestions : new ArrayList<>();
    }

    public static ParsedDocumentResponseBuilder builder() {
        return new ParsedDocumentResponseBuilder();
    }

    public String getExtractedTitle() { return extractedTitle; }
    public void setExtractedTitle(String extractedTitle) { this.extractedTitle = extractedTitle; }

    public int getSuggestedDurationMinutes() { return suggestedDurationMinutes; }
    public void setSuggestedDurationMinutes(int suggestedDurationMinutes) { this.suggestedDurationMinutes = suggestedDurationMinutes; }

    public List<ParsedMcQuestion> getMultipleChoiceQuestions() { return multipleChoiceQuestions; }
    public void setMultipleChoiceQuestions(List<ParsedMcQuestion> multipleChoiceQuestions) { this.multipleChoiceQuestions = multipleChoiceQuestions; }

    public List<ParsedEssayQuestion> getEssayQuestions() { return essayQuestions; }
    public void setEssayQuestions(List<ParsedEssayQuestion> essayQuestions) { this.essayQuestions = essayQuestions; }

    public static class ParsedDocumentResponseBuilder {
        private String extractedTitle;
        private int suggestedDurationMinutes = 60;
        private List<ParsedMcQuestion> multipleChoiceQuestions = new ArrayList<>();
        private List<ParsedEssayQuestion> essayQuestions = new ArrayList<>();

        public ParsedDocumentResponseBuilder extractedTitle(String extractedTitle) {
            this.extractedTitle = extractedTitle;
            return this;
        }

        public ParsedDocumentResponseBuilder suggestedDurationMinutes(int suggestedDurationMinutes) {
            this.suggestedDurationMinutes = suggestedDurationMinutes;
            return this;
        }

        public ParsedDocumentResponseBuilder multipleChoiceQuestions(List<ParsedMcQuestion> multipleChoiceQuestions) {
            this.multipleChoiceQuestions = multipleChoiceQuestions;
            return this;
        }

        public ParsedDocumentResponseBuilder essayQuestions(List<ParsedEssayQuestion> essayQuestions) {
            this.essayQuestions = essayQuestions;
            return this;
        }

        public ParsedDocumentResponse build() {
            return new ParsedDocumentResponse(extractedTitle, suggestedDurationMinutes, multipleChoiceQuestions, essayQuestions);
        }
    }

    public static class ParsedMcQuestion {
        private String id;
        private int order;
        private String context;
        private String question;
        private List<ParsedOption> options = new ArrayList<>();
        private String correctAnswer;
        private String explanation;
        private String legalReference;

        public ParsedMcQuestion() {}

        public ParsedMcQuestion(String id, int order, String question, List<ParsedOption> options, String correctAnswer, String explanation, String legalReference) {
            this(id, order, null, question, options, correctAnswer, explanation, legalReference);
        }

        public ParsedMcQuestion(String id, int order, String context, String question, List<ParsedOption> options, String correctAnswer, String explanation, String legalReference) {
            this.id = id;
            this.order = order;
            this.context = context;
            this.question = question;
            this.options = options != null ? options : new ArrayList<>();
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
            this.legalReference = legalReference;
        }

        public static ParsedMcQuestionBuilder builder() {
            return new ParsedMcQuestionBuilder();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }

        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public List<ParsedOption> getOptions() { return options; }
        public void setOptions(List<ParsedOption> options) { this.options = options; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }

        public String getLegalReference() { return legalReference; }
        public void setLegalReference(String legalReference) { this.legalReference = legalReference; }

        public static class ParsedMcQuestionBuilder {
            private String id;
            private int order;
            private String context;
            private String question;
            private List<ParsedOption> options = new ArrayList<>();
            private String correctAnswer;
            private String explanation;
            private String legalReference;

            public ParsedMcQuestionBuilder id(String id) { this.id = id; return this; }
            public ParsedMcQuestionBuilder order(int order) { this.order = order; return this; }
            public ParsedMcQuestionBuilder context(String context) { this.context = context; return this; }
            public ParsedMcQuestionBuilder question(String question) { this.question = question; return this; }
            public ParsedMcQuestionBuilder options(List<ParsedOption> options) { this.options = options; return this; }
            public ParsedMcQuestionBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
            public ParsedMcQuestionBuilder explanation(String explanation) { this.explanation = explanation; return this; }
            public ParsedMcQuestionBuilder legalReference(String legalReference) { this.legalReference = legalReference; return this; }

            public ParsedMcQuestion build() {
                return new ParsedMcQuestion(id, order, context, question, options, correctAnswer, explanation, legalReference);
            }
        }
    }

    public static class ParsedOption {
        private String id;
        private String label;
        private String text;

        public ParsedOption() {}

        public ParsedOption(String id, String label, String text) {
            this.id = id;
            this.label = label;
            this.text = text;
        }

        public static ParsedOptionBuilder builder() {
            return new ParsedOptionBuilder();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public static class ParsedOptionBuilder {
            private String id;
            private String label;
            private String text;

            public ParsedOptionBuilder id(String id) { this.id = id; return this; }
            public ParsedOptionBuilder label(String label) { this.label = label; return this; }
            public ParsedOptionBuilder text(String text) { this.text = text; return this; }

            public ParsedOption build() {
                return new ParsedOption(id, label, text);
            }
        }
    }

    public static class ParsedEssayQuestion {
        private String id;
        private int order;
        private String title;
        private String context;
        private String prompt;
        private BigDecimal maxScore;
        private List<String> rubric = new ArrayList<>();

        public ParsedEssayQuestion() {}

        public ParsedEssayQuestion(String id, int order, String title, String context, String prompt, BigDecimal maxScore, List<String> rubric) {
            this.id = id;
            this.order = order;
            this.title = title;
            this.context = context;
            this.prompt = prompt;
            this.maxScore = maxScore;
            this.rubric = rubric != null ? rubric : new ArrayList<>();
        }

        public static ParsedEssayQuestionBuilder builder() {
            return new ParsedEssayQuestionBuilder();
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }

        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }

        public BigDecimal getMaxScore() { return maxScore; }
        public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

        public List<String> getRubric() { return rubric; }
        public void setRubric(List<String> rubric) { this.rubric = rubric; }

        public static class ParsedEssayQuestionBuilder {
            private String id;
            private int order;
            private String title;
            private String context;
            private String prompt;
            private BigDecimal maxScore;
            private List<String> rubric = new ArrayList<>();

            public ParsedEssayQuestionBuilder id(String id) { this.id = id; return this; }
            public ParsedEssayQuestionBuilder order(int order) { this.order = order; return this; }
            public ParsedEssayQuestionBuilder title(String title) { this.title = title; return this; }
            public ParsedEssayQuestionBuilder context(String context) { this.context = context; return this; }
            public ParsedEssayQuestionBuilder prompt(String prompt) { this.prompt = prompt; return this; }
            public ParsedEssayQuestionBuilder maxScore(BigDecimal maxScore) { this.maxScore = maxScore; return this; }
            public ParsedEssayQuestionBuilder rubric(List<String> rubric) { this.rubric = rubric; return this; }

            public ParsedEssayQuestion build() {
                return new ParsedEssayQuestion(id, order, title, context, prompt, maxScore, rubric);
            }
        }
    }
}
