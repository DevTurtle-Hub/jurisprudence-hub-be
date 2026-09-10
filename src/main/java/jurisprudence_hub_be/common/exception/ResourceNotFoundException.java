package jurisprudence_hub_be.common.exception;

import jurisprudence_hub_be.common.constant.ErrorCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}