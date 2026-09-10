package jurisprudence_hub_be.common.exception;

import jurisprudence_hub_be.common.constant.ErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }
}
