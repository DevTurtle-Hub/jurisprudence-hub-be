package jurisprudence_hub_be.module.exam.dto.draft;

public class DraftOptionDto {
    private String key;
    private String content;
    private boolean isCorrect = false;

    public DraftOptionDto() {
    }

    public DraftOptionDto(String key, String content, boolean isCorrect) {
        this.key = key;
        this.content = content;
        this.isCorrect = isCorrect;
    }

    public static DraftOptionDtoBuilder builder() {
        return new DraftOptionDtoBuilder();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public static class DraftOptionDtoBuilder {
        private String key;
        private String content;
        private boolean isCorrect = false;

        public DraftOptionDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        public DraftOptionDtoBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DraftOptionDtoBuilder isCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public DraftOptionDto build() {
            return new DraftOptionDto(key, content, isCorrect);
        }
    }
}
