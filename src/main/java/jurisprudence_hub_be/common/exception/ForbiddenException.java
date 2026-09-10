package jurisprudence_hub_be.common.exception;

import jurisprudence_hub_be.common.constant.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ErrorCode.ACCESS_DENIED);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }
}
