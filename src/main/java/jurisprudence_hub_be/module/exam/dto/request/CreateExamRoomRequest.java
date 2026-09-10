package jurisprudence_hub_be.module.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jurisprudence_hub_be.module.exam.enums.ExamRoomStatus;

import java.util.ArrayList;
import java.util.List;

public class CreateExamRoomRequest {

    @NotBlank(message = "Mã phòng thi không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,32}$", message = "Mã phòng thi không hợp lệ (chỉ gồm 3-32 ký tự chữ, số, gạch nối)")
    private String code;

    @NotBlank(message = "Tên phòng thi không được để trống")
    private String title;

    private String description;

    private int durationMinutes = 60;

    private ExamRoomStatus status = ExamRoomStatus.OPEN;

    private List<CreateMcQuestionRequest> multipleChoiceQuestions = new ArrayList<>();

    private List<CreateEssayQuestionRequest> essayQuestions = new ArrayList<>();

    public CreateExamRoomRequest() {
    }

    public CreateExamRoomRequest(String code, String title, String description, int durationMinutes,
                                 ExamRoomStatus status, List<CreateMcQuestionRequest> multipleChoiceQuestions,
                                 List<CreateEssayQuestionRequest> essayQuestions) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.status = (status != null) ? status : ExamRoomStatus.OPEN;
        this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
        this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
    }

    public static CreateExamRoomRequestBuilder builder() {
        return new CreateExamRoomRequestBuilder();
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

    public ExamRoomStatus getStatus() {
        return status;
    }

    public void setStatus(ExamRoomStatus status) {
        this.status = status;
    }

    public List<CreateMcQuestionRequest> getMultipleChoiceQuestions() {
        if (multipleChoiceQuestions == null) {
            multipleChoiceQuestions = new ArrayList<>();
        }
        return multipleChoiceQuestions;
    }

    public void setMultipleChoiceQuestions(List<CreateMcQuestionRequest> multipleChoiceQuestions) {
        this.multipleChoiceQuestions = multipleChoiceQuestions;
    }

    public List<CreateEssayQuestionRequest> getEssayQuestions() {
        if (essayQuestions == null) {
            essayQuestions = new ArrayList<>();
        }
        return essayQuestions;
    }

    public void setEssayQuestions(List<CreateEssayQuestionRequest> essayQuestions) {
        this.essayQuestions = essayQuestions;
    }

    public static class CreateExamRoomRequestBuilder {
        private String code;
        private String title;
        private String description;
        private int durationMinutes = 60;
        private ExamRoomStatus status = ExamRoomStatus.OPEN;
        private List<CreateMcQuestionRequest> multipleChoiceQuestions = new ArrayList<>();
        private List<CreateEssayQuestionRequest> essayQuestions = new ArrayList<>();

        public CreateExamRoomRequestBuilder code(String code) {
            this.code = code;
            return this;
        }

        public CreateExamRoomRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CreateExamRoomRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CreateExamRoomRequestBuilder durationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public CreateExamRoomRequestBuilder status(ExamRoomStatus status) {
            this.status = status;
            return this;
        }

        public CreateExamRoomRequestBuilder multipleChoiceQuestions(List<CreateMcQuestionRequest> multipleChoiceQuestions) {
            this.multipleChoiceQuestions = (multipleChoiceQuestions != null) ? multipleChoiceQuestions : new ArrayList<>();
            return this;
        }

        public CreateExamRoomRequestBuilder essayQuestions(List<CreateEssayQuestionRequest> essayQuestions) {
            this.essayQuestions = (essayQuestions != null) ? essayQuestions : new ArrayList<>();
            return this;
        }

        public CreateExamRoomRequest build() {
            return new CreateExamRoomRequest(code, title, description, durationMinutes, status, multipleChoiceQuestions, essayQuestions);
        }
    }
}
