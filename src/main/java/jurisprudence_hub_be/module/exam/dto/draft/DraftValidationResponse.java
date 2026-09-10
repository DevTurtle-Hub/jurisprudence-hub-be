package jurisprudence_hub_be.module.exam.dto.draft;

import java.util.ArrayList;
import java.util.List;

public class DraftValidationResponse {
    private String draftId;
    private boolean valid;
    private int totalQuestions;
    private int completedQuestions;
    private int incompleteQuestions;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private List<QuestionValidationDetail> questionDetails = new ArrayList<>();

    public DraftValidationResponse() {
    }

    public DraftValidationResponse(String draftId, boolean valid, int totalQuestions,
                                   int completedQuestions, int incompleteQuestions,
                                   List<String> errors, List<String> warnings,
                                   List<QuestionValidationDetail> questionDetails) {
        this.draftId = draftId;
        this.valid = valid;
        this.totalQuestions = totalQuestions;
        this.completedQuestions = completedQuestions;
        this.incompleteQuestions = incompleteQuestions;
        this.errors = (errors != null) ? errors : new ArrayList<>();
        this.warnings = (warnings != null) ? warnings : new ArrayList<>();
        this.questionDetails = (questionDetails != null) ? questionDetails : new ArrayList<>();
    }

    public static DraftValidationResponseBuilder builder() {
        return new DraftValidationResponseBuilder();
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCompletedQuestions() {
        return completedQuestions;
    }

    public void setCompletedQuestions(int completedQuestions) {
        this.completedQuestions = completedQuestions;
    }

    public int getIncompleteQuestions() {
        return incompleteQuestions;
    }

    public void setIncompleteQuestions(int incompleteQuestions) {
        this.incompleteQuestions = incompleteQuestions;
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

    public List<String> getWarnings() {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<QuestionValidationDetail> getQuestionDetails() {
        if (questionDetails == null) {
            questionDetails = new ArrayList<>();
        }
        return questionDetails;
    }

    public void setQuestionDetails(List<QuestionValidationDetail> questionDetails) {
        this.questionDetails = questionDetails;
    }

    public static class QuestionValidationDetail {
        private String temporaryId;
        private int questionNumber;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public QuestionValidationDetail() {
        }

        public QuestionValidationDetail(String temporaryId, int questionNumber, List<String> errors, List<String> warnings) {
            this.temporaryId = temporaryId;
            this.questionNumber = questionNumber;
            this.errors = (errors != null) ? errors : new ArrayList<>();
            this.warnings = (warnings != null) ? warnings : new ArrayList<>();
        }

        public static QuestionValidationDetailBuilder builder() {
            return new QuestionValidationDetailBuilder();
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

        public List<String> getErrors() {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
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

        public static class QuestionValidationDetailBuilder {
            private String temporaryId;
            private int questionNumber;
            private List<String> errors = new ArrayList<>();
            private List<String> warnings = new ArrayList<>();

            public QuestionValidationDetailBuilder temporaryId(String temporaryId) {
                this.temporaryId = temporaryId;
                return this;
            }

            public QuestionValidationDetailBuilder questionNumber(int questionNumber) {
                this.questionNumber = questionNumber;
                return this;
            }

            public QuestionValidationDetailBuilder errors(List<String> errors) {
                this.errors = (errors != null) ? errors : new ArrayList<>();
                return this;
            }

            public QuestionValidationDetailBuilder warnings(List<String> warnings) {
                this.warnings = (warnings != null) ? warnings : new ArrayList<>();
                return this;
            }

            public QuestionValidationDetail build() {
                return new QuestionValidationDetail(temporaryId, questionNumber, errors, warnings);
            }
        }
    }

    public static class DraftValidationResponseBuilder {
        private String draftId;
        private boolean valid;
        private int totalQuestions;
        private int completedQuestions;
        private int incompleteQuestions;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private List<QuestionValidationDetail> questionDetails = new ArrayList<>();

        public DraftValidationResponseBuilder draftId(String draftId) {
            this.draftId = draftId;
            return this;
        }

        public DraftValidationResponseBuilder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public DraftValidationResponseBuilder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public DraftValidationResponseBuilder completedQuestions(int completedQuestions) {
            this.completedQuestions = completedQuestions;
            return this;
        }

        public DraftValidationResponseBuilder incompleteQuestions(int incompleteQuestions) {
            this.incompleteQuestions = incompleteQuestions;
            return this;
        }

        public DraftValidationResponseBuilder errors(List<String> errors) {
            this.errors = (errors != null) ? errors : new ArrayList<>();
            return this;
        }

        public DraftValidationResponseBuilder warnings(List<String> warnings) {
            this.warnings = (warnings != null) ? warnings : new ArrayList<>();
            return this;
        }

        public DraftValidationResponseBuilder questionDetails(List<QuestionValidationDetail> questionDetails) {
            this.questionDetails = (questionDetails != null) ? questionDetails : new ArrayList<>();
            return this;
        }

        public DraftValidationResponse build() {
            return new DraftValidationResponse(draftId, valid, totalQuestions, completedQuestions, incompleteQuestions,
                    errors, warnings, questionDetails);
        }
    }
}
