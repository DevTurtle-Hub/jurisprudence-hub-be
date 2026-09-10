package jurisprudence_hub_be.module.exam.constant;

public final class ExamConstant {

    private ExamConstant() {
    }

    // Success Messages - Parsing & Creation
    public static final String MSG_PARSE_DOCUMENT_SUCCESS = "Bóc tách tệp đề thi thành công";
    public static final String MSG_ROOM_CREATE_SUCCESS = "Khởi tạo phòng thi thành công";
    public static final String MSG_ROOM_UPDATE_SUCCESS = "Cập nhật phòng thi thành công";
    public static final String MSG_ROOM_DELETE_SUCCESS = "Xóa phòng thi thành công";
    public static final String MSG_ROOM_LIST_SUCCESS = "Lấy danh sách phòng thi thành công";
    public static final String MSG_ROOM_DETAIL_SUCCESS = "Lấy chi tiết phòng thi thành công";

    // Success Messages - Candidate & Exam Taking
    public static final String MSG_CANDIDATE_VERIFY_SUCCESS = "Xác minh thí sinh thành công";
    public static final String MSG_SESSION_VALID = "Session token hợp lệ";
    public static final String MSG_EXAM_TAKE_SUCCESS = "Tải đề thi thành công";
    public static final String MSG_EXAM_SUBMIT_SUCCESS = "Nộp bài thi thành công";
    public static final String MSG_RECEIPT_DETAIL_SUCCESS = "Tra cứu biên bản bài thi thành công";
    public static final String MSG_GRADE_ESSAY_SUCCESS = "Chấm điểm bài tự luận thành công";

    // Success Messages - Draft & Import
    public static final String MSG_IMPORT_PREVIEW_SUCCESS = "Bóc tách PDF thành công. Đã tạo bản nháp xem trước (Draft).";
    public static final String MSG_MANUAL_DRAFT_SUCCESS = "Đã tạo bản nháp cho nhập thủ công";
    public static final String MSG_DRAFT_DETAIL_SUCCESS = "Lấy thông tin bản nháp thành công";
    public static final String MSG_DRAFT_UPDATE_QUESTION_SUCCESS = "Cập nhật câu hỏi trong bản nháp thành công";
    public static final String MSG_DRAFT_DELETE_QUESTION_SUCCESS = "Đã xóa câu hỏi khỏi bản nháp";
    public static final String MSG_DRAFT_ADD_QUESTION_SUCCESS = "Thêm câu hỏi mới vào bản nháp thành công";
    public static final String MSG_DRAFT_VALIDATE_SUCCESS = "Kiểm tra tính hợp lệ bản nháp hoàn tất";
    public static final String MSG_IMPORT_CONFIRM_SUCCESS = "Xác nhận import thành công! Đề thi đã được lưu chính thức.";
    public static final String MSG_DRAFT_CANCEL_SUCCESS = "Đã hủy bản nháp đề thi thành công";

    // Error Messages - Room & Candidate
    public static final String MSG_ROOM_CODE_EXISTS = "Mã phòng thi '%s' đã tồn tại trong hệ thống";
    public static final String MSG_ROOM_NOT_FOUND = "Không tìm thấy phòng thi với mã hoặc id: ";
    public static final String MSG_ROOM_NOT_FOUND_SIMPLE = "Không tìm thấy phòng thi: ";
    public static final String MSG_ROOM_NOT_OPEN = "Phòng thi hiện không mở cho thí sinh tham gia (Trạng thái: %s)";
    public static final String MSG_SESSION_TOKEN_REQUIRED = "Yêu cầu cung cấp Authorization Bearer <sessionToken> hoặc sessionToken";
    public static final String MSG_SESSION_TOKEN_REQUIRED_SIMPLE = "Yêu cầu cung cấp sessionToken để truy cập bài thi";
    public static final String MSG_SESSION_TOKEN_MISMATCH = "Session token không thuộc phòng thi này";
    public static final String MSG_SESSION_TOKEN_INVALID_OR_EXPIRED = "SessionToken không hợp lệ hoặc đã hết hạn";
    public static final String MSG_SESSION_EXPIRED = "Phiên làm bài thi của thí sinh đã hết hạn";
    public static final String MSG_CANDIDATE_NOT_REGISTERED = "Thí sinh không được đăng ký vào phòng thi này";
    public static final String MSG_CANDIDATE_NOT_IN_ROOM = "Thí sinh không thuộc phòng thi này";
    public static final String MSG_EXAM_ALREADY_SUBMITTED = "Thí sinh đã nộp bài phòng thi này rồi, không thể nộp lại";
    public static final String MSG_RECEIPT_NOT_FOUND = "Không tìm thấy biên bản bài thi với mã: ";
    public static final String MSG_SUBMISSION_NOT_FOUND = "Không tìm thấy bài thi cần chấm với mã: ";

    // Error Messages - File & Parser
    public static final String MSG_FILE_EMPTY = "Tệp đề thi tải lên không được để trống";
    public static final String MSG_FILE_UNSUPPORTED = "Định dạng tệp không được hỗ trợ. Vui lòng tải lên tệp PDF (.pdf)";
    public static final String MSG_FILE_TOO_LARGE = "Kích thước tệp PDF vượt quá giới hạn cho phép (tối đa 20MB)";
    public static final String MSG_FILE_READ_ERROR = "Không thể đọc định dạng tệp: ";
    public static final String MSG_FILE_NO_TEXT = "Không tìm thấy nội dung văn bản trong tệp tải lên";
    public static final String MSG_PDF_READ_ERROR = "Không thể đọc tệp PDF. Tệp có thể bị hỏng hoặc định dạng không hợp lệ: ";
    public static final String MSG_PDF_EXTRACT_ERROR = "Lỗi trích xuất PDF: ";
    public static final String MSG_PDF_PASSWORD_PROTECTED = "Tệp PDF có cài đặt mật khẩu bảo vệ, vui lòng gỡ mật khẩu trước khi tải lên";
    public static final String MSG_PDF_NO_TEXT_LAYER = "Tệp PDF là tài liệu scan hoặc dạng hình ảnh không chứa lớp văn bản (text layer). "
            + "Hệ thống yêu cầu tệp PDF có chứa văn bản kỹ thuật số (xuất từ Word/Docs) hoặc đã qua nhận dạng ký tự quang học (OCR).";
    public static final String MSG_PDF_INSUFFICIENT_TEXT = "Tệp PDF không đủ dữ liệu văn bản (có thể là tài liệu scan ảnh hoặc tài liệu rỗng). "
            + "Vui lòng tải lên tệp PDF có chứa nội dung văn bản đầy đủ.";

    // Error Messages - Draft
    public static final String MSG_DRAFT_NOT_FOUND = "Không tìm thấy bản nháp đề thi với id: ";
    public static final String MSG_DRAFT_QUESTION_NOT_FOUND = "Không tìm thấy câu hỏi %s trong bản nháp";
    public static final String MSG_DRAFT_QUESTION_DELETE_NOT_FOUND = "Không tìm thấy câu hỏi %s để xóa";
    public static final String MSG_DRAFT_HAS_CRITICAL_ERRORS = "Không thể xác nhận import khi bản nháp còn lỗi nghiêm trọng: ";

    // Validation Messages - Draft Questions
    public static final String VAL_QUESTION_CONTENT_EMPTY = "Nội dung câu hỏi bị rỗng.";
    public static final String VAL_NO_OPTIONS_FOUND = "Không tìm thấy phương án A/B/C/D.";
    public static final String VAL_NO_MC_OPTIONS = "Không tìm thấy bất kỳ phương án lựa chọn A/B/C/D nào.";
    public static final String VAL_FEWER_OPTIONS_WARNING = "Câu trắc nghiệm chỉ có %d phương án (tiêu chuẩn là 4 phương án A, B, C, D).";
    public static final String VAL_NO_CORRECT_ANSWER = "BẮT BUỘC: Chưa chọn đáp án đúng cho câu hỏi.";
    public static final String VAL_MULTIPLE_CORRECT_ANSWERS = "Có nhiều hơn 1 đáp án đúng.";
    public static final String VAL_ESSAY_NO_SAMPLE_ANSWER = "BẮT BUỘC: Chưa có đáp án mẫu cho câu tự luận.";
    public static final String VAL_SHORT_ANSWER_NO_SAMPLE = "BẮT BUỘC: Chưa có đáp án mẫu cho câu trả lời ngắn.";
}
