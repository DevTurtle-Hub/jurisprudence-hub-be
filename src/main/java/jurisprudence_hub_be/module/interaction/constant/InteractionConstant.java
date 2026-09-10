package jurisprudence_hub_be.module.interaction.constant;

public final class InteractionConstant {

    private InteractionConstant() {
    }

    public static final String ANNOTATION_ID_PREFIX = "ant-";
    public static final String DEFAULT_KIND = "highlight";
    public static final String DEFAULT_COLOR = "yellow";

    // Response Messages - Annotation
    public static final String MSG_ANNOTATION_LIST_SUCCESS = "Lấy danh sách highlight thành công";
    public static final String MSG_ANNOTATION_CREATE_SUCCESS = "Đã lưu đánh dấu thành công";
    public static final String MSG_ANNOTATION_DELETE_SUCCESS = "Đã xóa đánh dấu thành công";

    // Response Messages - Lesson Progress
    public static final String MSG_PROGRESS_UPDATE_SUCCESS = "Cập nhật tiến độ học thành công";

    // Error Messages
    public static final String MSG_ANNOTATION_NOT_FOUND = "Không tìm thấy ghi chú với mã: ";
    public static final String MSG_ANNOTATION_DELETE_FORBIDDEN = "Bạn không có quyền xóa đánh dấu này";
}
