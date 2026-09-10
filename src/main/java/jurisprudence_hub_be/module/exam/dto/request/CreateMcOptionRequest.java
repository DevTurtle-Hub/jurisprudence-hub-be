package jurisprudence_hub_be.module.exam.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CreateMcOptionRequest {

    private String id;
    private String label;

    @JsonAlias({"optionText", "text"})
    private String text;

    public CreateMcOptionRequest() {
    }

    public CreateMcOptionRequest(String id, String label, String text) {
        this.id = id;
        this.label = label;
        this.text = text;
    }

    public static CreateMcOptionRequestBuilder builder() {
        return new CreateMcOptionRequestBuilder();
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

    public static class CreateMcOptionRequestBuilder {
        private String id;
        private String label;
        private String text;

        public CreateMcOptionRequestBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CreateMcOptionRequestBuilder label(String label) {
            this.label = label;
            return this;
        }

        public CreateMcOptionRequestBuilder text(String text) {
            this.text = text;
            return this;
        }

        public CreateMcOptionRequest build() {
            return new CreateMcOptionRequest(id, label, text);
        }
    }
}
