package jurisprudence_hub_be.module.auth.constant;

public final class AuthConstant {

    private AuthConstant() {
    }

    public static final String USER_ID_PREFIX = "usr-";
    public static final String REFRESH_TOKEN_PREFIX = "rt-";
    public static final String DEFAULT_UNIT = "Công an Nhân dân";
    public static final String DEFAULT_READ_TIME = "5 phút";
    public static final int DEFAULT_ID_RANDOM_LENGTH = 10;

    // Response Messages
    public static final String MSG_REGISTER_SUCCESS = "Đăng ký tài khoản thành công";
    public static final String MSG_LOGIN_SUCCESS = "Đăng nhập thành công";
    public static final String MSG_GET_ME_SUCCESS = "Lấy thông tin tài khoản thành công";
    public static final String MSG_REFRESH_TOKEN_SUCCESS = "Làm mới token thành công";
    public static final String MSG_LOGOUT_SUCCESS = "Đăng xuất thành công";

    // Error Messages
    public static final String MSG_EMAIL_ALREADY_EXISTS = "Email đã tồn tại trong hệ thống";
    public static final String MSG_INVALID_CREDENTIALS = "Email hoặc mật khẩu không chính xác";
    public static final String MSG_ACCOUNT_DISABLED = "Tài khoản đã bị vô hiệu hóa";
    public static final String MSG_USER_NOT_FOUND = "Không tìm thấy thông tin người dùng";
    public static final String MSG_REFRESH_TOKEN_MISSING = "Refresh token không được để trống";
    public static final String MSG_REFRESH_TOKEN_NOT_FOUND_OR_EXPIRED = "Refresh token không tồn tại hoặc đã hết hạn";
    public static final String MSG_REFRESH_TOKEN_EXPIRED = "Refresh token đã hết hạn";
}
