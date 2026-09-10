package jurisprudence_hub_be.module.exam.dto.request;

import java.util.HashMap;
import java.util.Map;

public class SubmitExamRequest {

    private int timeSpentSeconds;
    private ExamAnswersPayload answers = new ExamAnswersPayload();

    public SubmitExamRequest() {
    }

    public SubmitExamRequest(int timeSpentSeconds, ExamAnswersPayload answers) {
        this.timeSpentSeconds = timeSpentSeconds;
        this.answers = (answers != null) ? answers : new ExamAnswersPayload();
    }

    public static SubmitExamRequestBuilder builder() {
        return new SubmitExamRequestBuilder();
    }

    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(int timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public ExamAnswersPayload getAnswers() {
        if (answers == null) {
            answers = new ExamAnswersPayload();
        }
        return answers;
    }

    public void setAnswers(ExamAnswersPayload answers) {
        this.answers = answers;
    }

    public static class ExamAnswersPayload {
        private Map<String, String> multipleChoice = new HashMap<>();
        private Map<String, String> essay = new HashMap<>();

        public ExamAnswersPayload() {
        }

        public ExamAnswersPayload(Map<String, String> multipleChoice, Map<String, String> essay) {
            this.multipleChoice = (multipleChoice != null) ? multipleChoice : new HashMap<>();
            this.essay = (essay != null) ? essay : new HashMap<>();
        }

        public static ExamAnswersPayloadBuilder builder() {
            return new ExamAnswersPayloadBuilder();
        }

        public Map<String, String> getMultipleChoice() {
            if (multipleChoice == null) {
                multipleChoice = new HashMap<>();
            }
            return multipleChoice;
        }

        public void setMultipleChoice(Map<String, String> multipleChoice) {
            this.multipleChoice = multipleChoice;
        }

        public Map<String, String> getEssay() {
            if (essay == null) {
                essay = new HashMap<>();
            }
            return essay;
        }

        public void setEssay(Map<String, String> essay) {
            this.essay = essay;
        }

        public static class ExamAnswersPayloadBuilder {
            private Map<String, String> multipleChoice = new HashMap<>();
            private Map<String, String> essay = new HashMap<>();

            public ExamAnswersPayloadBuilder multipleChoice(Map<String, String> multipleChoice) {
                this.multipleChoice = (multipleChoice != null) ? multipleChoice : new HashMap<>();
                return this;
            }

            public ExamAnswersPayloadBuilder essay(Map<String, String> essay) {
                this.essay = (essay != null) ? essay : new HashMap<>();
                return this;
            }

            public ExamAnswersPayload build() {
                return new ExamAnswersPayload(multipleChoice, essay);
            }
        }
    }

    public static class SubmitExamRequestBuilder {
        private int timeSpentSeconds;
        private ExamAnswersPayload answers = new ExamAnswersPayload();

        public SubmitExamRequestBuilder timeSpentSeconds(int timeSpentSeconds) {
            this.timeSpentSeconds = timeSpentSeconds;
            return this;
        }

        public SubmitExamRequestBuilder answers(ExamAnswersPayload answers) {
            this.answers = (answers != null) ? answers : new ExamAnswersPayload();
            return this;
        }

        public SubmitExamRequest build() {
            return new SubmitExamRequest(timeSpentSeconds, answers);
        }
    }
}
