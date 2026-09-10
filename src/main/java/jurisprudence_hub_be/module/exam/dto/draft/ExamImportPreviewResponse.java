package jurisprudence_hub_be.module.exam.dto.draft;

import java.util.ArrayList;
import java.util.List;

public class ExamImportPreviewResponse {
    private String draftId;
    private String fileName;
    private String title;
    private int totalQuestions;
    private int essayCount;
    private int mcCount;
    private int shortAnswerCount;
    private int completedQuestions;
    private int incompleteQuestions;
    private int successCount;
    private int warningCount;
    private int errorCount;
    private List<DraftQuestionDto> questions = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public ExamImportPreviewResponse() {
    }

    public ExamImportPreviewResponse(String draftId, String fileName, String title, int totalQuestions,
                                     int essayCount, int mcCount, int shortAnswerCount,
                                     int completedQuestions, int incompleteQuestions,
                                     int successCount, int warningCount, int errorCount,
                                     List<DraftQuestionDto> questions, List<String> warnings, List<String> errors) {
        this.draftId = draftId;
        this.fileName = fileName;
        this.title = title;
        this.totalQuestions = totalQuestions;
        this.essayCount = essayCount;
        this.mcCount = mcCount;
        this.shortAnswerCount = shortAnswerCount;
        this.completedQuestions = completedQuestions;
        this.incompleteQuestions = incompleteQuestions;
        this.successCount = successCount;
        this.warningCount = warningCount;
        this.errorCount = errorCount;
        this.questions = (questions != null) ? questions : new ArrayList<>();
        this.warnings = (warnings != null) ? warnings : new ArrayList<>();
        this.errors = (errors != null) ? errors : new ArrayList<>();
    }

    public static ExamImportPreviewResponseBuilder builder() {
        return new ExamImportPreviewResponseBuilder();
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getEssayCount() {
        return essayCount;
    }

    public void setEssayCount(int essayCount) {
        this.essayCount = essayCount;
    }

    public int getMcCount() {
        return mcCount;
    }

    public void setMcCount(int mcCount) {
        this.mcCount = mcCount;
    }

    public int getShortAnswerCount() {
        return shortAnswerCount;
    }

    public void setShortAnswerCount(int shortAnswerCount) {
        this.shortAnswerCount = shortAnswerCount;
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

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<DraftQuestionDto> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public void setQuestions(List<DraftQuestionDto> questions) {
        this.questions = questions;
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

    public static class ExamImportPreviewResponseBuilder {
        private String draftId;
        private String fileName;
        private String title;
        private int totalQuestions;
        private int essayCount;
        private int mcCount;
        private int shortAnswerCount;
        private int completedQuestions;
        private int incompleteQuestions;
        private int successCount;
        private int warningCount;
        private int errorCount;
        private List<DraftQuestionDto> questions = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public ExamImportPreviewResponseBuilder draftId(String draftId) {
            this.draftId = draftId;
            return this;
        }

        public ExamImportPreviewResponseBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ExamImportPreviewResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ExamImportPreviewResponseBuilder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public ExamImportPreviewResponseBuilder essayCount(int essayCount) {
            this.essayCount = essayCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder mcCount(int mcCount) {
            this.mcCount = mcCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder shortAnswerCount(int shortAnswerCount) {
            this.shortAnswerCount = shortAnswerCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder completedQuestions(int completedQuestions) {
            this.completedQuestions = completedQuestions;
            return this;
        }

        public ExamImportPreviewResponseBuilder incompleteQuestions(int incompleteQuestions) {
            this.incompleteQuestions = incompleteQuestions;
            return this;
        }

        public ExamImportPreviewResponseBuilder successCount(int successCount) {
            this.successCount = successCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder warningCount(int warningCount) {
            this.warningCount = warningCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder errorCount(int errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public ExamImportPreviewResponseBuilder questions(List<DraftQuestionDto> questions) {
            this.questions = (questions != null) ? questions : new ArrayList<>();
            return this;
        }

        public ExamImportPreviewResponseBuilder warnings(List<String> warnings) {
            this.warnings = (warnings != null) ? warnings : new ArrayList<>();
            return this;
        }

        public ExamImportPreviewResponseBuilder errors(List<String> errors) {
            this.errors = (errors != null) ? errors : new ArrayList<>();
            return this;
        }

        public ExamImportPreviewResponse build() {
            return new ExamImportPreviewResponse(draftId, fileName, title, totalQuestions,
                    essayCount, mcCount, shortAnswerCount, completedQuestions, incompleteQuestions,
                    successCount, warningCount, errorCount, questions, warnings, errors);
        }
    }
}
