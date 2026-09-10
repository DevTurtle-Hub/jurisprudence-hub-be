package jurisprudence_hub_be.module.questionbank.constant;

public final class QuestionBankConstant {

    private QuestionBankConstant() {
        // Private constructor to prevent instantiation
    }

    // Success Messages
    public static final String MSG_PARSE_PREVIEW_SUCCESS = "Bóc tách câu hỏi thành công - đang ở chế độ preview";
    public static final String MSG_SAVE_QUESTION_SUCCESS = "Lưu câu hỏi thành công (đang ở trạng thái draft)";
    public static final String MSG_SAVE_BATCH_SUCCESS = "Lưu nhiều câu hỏi thành công";
    public static final String MSG_GET_QUESTIONS_SUCCESS = "Lấy danh sách câu hỏi thành công";
    public static final String MSG_GET_QUESTION_DETAIL_SUCCESS = "Lấy chi tiết câu hỏi thành công";
    public static final String MSG_UPDATE_QUESTION_SUCCESS = "Cập nhật câu hỏi thành công";
    public static final String MSG_DELETE_QUESTION_SUCCESS = "Xóa câu hỏi thành công";
    public static final String MSG_PUBLISH_QUESTION_SUCCESS = "Publish câu hỏi thành công";
    public static final String MSG_CREATE_MANUAL_SUCCESS = "Tạo câu hỏi tay thành công";

    // Error Messages
    public static final String MSG_QUESTION_NOT_FOUND = "Không tìm thấy câu hỏi với ID: ";
    public static final String MSG_PARSE_FILE_FAILED = "Không thể bóc tách file: ";
    public static final String MSG_REQUEST_REQUIRED = "Dữ liệu yêu cầu câu hỏi không được để trống";
}
