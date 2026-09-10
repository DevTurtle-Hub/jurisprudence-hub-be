package jurisprudence_hub_be.common.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // API Configuration
    public static final String API_PREFIX = "/api";
    public static final String API_VERSION = "/v1";
    public static final String API_BASE_PATH = API_PREFIX + API_VERSION;

    // Pagination Configuration
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Sorting Configuration
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Date/Time Configuration
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_TIME_FORMAT = "HH:mm:ss";

    // File Upload Configuration
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "txt"};

    // Cache Configuration
    public static final long CACHE_TTL_SECONDS = 3600; // 1 hour
    public static final String DEFAULT_CACHE_NAME = "default";

    // Application Configuration
    public static final String APPLICATION_NAME = "Jurisprudence Hub";
    public static final String APPLICATION_VERSION = "1.0.0";

    // Response Messages
    public static final String SUCCESS_MESSAGE = "Operation completed successfully";
    public static final String CREATED_MESSAGE = "Resource created successfully";
    public static final String UPDATED_MESSAGE = "Resource updated successfully";
    public static final String DELETED_MESSAGE = "Resource deleted successfully";
    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";
}