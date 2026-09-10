package jurisprudence_hub_be.module.document.constant;

public final class DocumentConstant {

    private DocumentConstant() {
    }

    public static final String CHAPTER_ID_PREFIX = "ch-";
    public static final String LESSON_ID_PREFIX = "lesson-";
    public static final int DEFAULT_ID_RANDOM_LENGTH = 8;

    // Response Messages - Chapter
    public static final String MSG_CHAPTER_LIST_SUCCESS = "Lấy danh mục chương bài học thành công";
    public static final String MSG_CHAPTER_DETAIL_SUCCESS = "Lấy thông tin chương thành công";
    public static final String MSG_CHAPTER_CREATE_SUCCESS = "Tạo chương mới thành công";
    public static final String MSG_CHAPTER_UPDATE_SUCCESS = "Cập nhật chương thành công";
    public static final String MSG_CHAPTER_DELETE_SUCCESS = "Đã xóa chương và các bài học liên quan thành công";

    // Response Messages - Lesson
    public static final String MSG_LESSON_LIST_SUCCESS = "Lấy danh sách bài học thành công";
    public static final String MSG_LESSON_DETAIL_SUCCESS = "Lấy chi tiết bài học thành công";
    public static final String MSG_LESSON_CREATE_SUCCESS = "Xuất bản bài học thành công";
    public static final String MSG_LESSON_UPDATE_SUCCESS = "Cập nhật bài học thành công";
    public static final String MSG_LESSON_DELETE_SUCCESS = "Đã xóa bài học thành công";

    // Error Messages
    public static final String MSG_CHAPTER_NOT_FOUND = "Không tìm thấy chương với mã: ";
    public static final String MSG_LESSON_NOT_FOUND = "Không tìm thấy bài học với mã: ";
}
