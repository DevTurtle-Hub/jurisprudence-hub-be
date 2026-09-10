package jurisprudence_hub_be.module.questionbank.dto.response;

public class QuestionBankMcOptionResponse {

    private String id;
    private String label;
    private String text;
    private boolean isCorrect;

    public QuestionBankMcOptionResponse() {
    }

    public QuestionBankMcOptionResponse(String id, String label, String text, boolean isCorrect) {
        this.id = id;
        this.label = label;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public static QuestionBankMcOptionResponseBuilder builder() {
        return new QuestionBankMcOptionResponseBuilder();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public static class QuestionBankMcOptionResponseBuilder {
        private String id;
        private String label;
        private String text;
        private boolean isCorrect = false;

        public QuestionBankMcOptionResponseBuilder id(String id) { this.id = id; return this; }
        public QuestionBankMcOptionResponseBuilder label(String label) { this.label = label; return this; }
        public QuestionBankMcOptionResponseBuilder text(String text) { this.text = text; return this; }
        public QuestionBankMcOptionResponseBuilder isCorrect(boolean isCorrect) { this.isCorrect = isCorrect; return this; }

        public QuestionBankMcOptionResponse build() {
            return new QuestionBankMcOptionResponse(id, label, text, isCorrect);
        }
    }
}