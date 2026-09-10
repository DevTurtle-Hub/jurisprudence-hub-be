package jurisprudence_hub_be.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jurisprudence_hub_be.common.constant.ErrorCode;
import jurisprudence_hub_be.common.dto.ErrorResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.RESOURCE_NOT_FOUND;
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.UNAUTHORIZED;
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.ACCESS_DENIED;
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.INVALID_REQUEST;
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(BusinessException.class)
    @SuppressWarnings("null")
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.INVALID_REQUEST;
        HttpStatus status = resolveHttpStatus(errorCode);

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.validation(
                        errorCode.getCode(),
                        "Validation failed",
                        request.getRequestURI(),
                        errors,
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "param";
            errors.put(path, violation.getMessage());
        }

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.validation(
                        ErrorCode.VALIDATION_FAILED.getCode(),
                        "Constraint violation",
                        request.getRequestURI(),
                        errors,
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Malformed JSON request at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.withDetails(
                        ErrorCode.INVALID_FORMAT.getCode(),
                        "Malformed JSON request payload or unrecognized property",
                        request.getRequestURI(),
                        ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String paramName = ex.getName();
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' should be of type '%s'", paramName, typeName);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_FIELD.getCode(),
                        message,
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        String message = String.format("Required request parameter '%s' is missing", ex.getParameterName());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.REQUIRED_FIELD_MISSING.getCode(),
                        message,
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String message = String.format("Request method '%s' is not supported for this endpoint", ex.getMethod());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(
                        ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                        message,
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        String message = String.format("Content type '%s' is not supported", ex.getContentType());
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.of(
                        ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                        message,
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Database constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.withDetails(
                        ErrorCode.CONFLICT.getCode(),
                        "Resource conflict or database constraint violation",
                        request.getRequestURI(),
                        "A database constraint was violated (e.g. duplicate unique key or invalid reference)",
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        ErrorCode.ACCESS_DENIED.getCode(),
                        "Access is denied: " + ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        ErrorCode.UNAUTHORIZED.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        "Resource not found: " + ex.getResourcePath(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        ErrorCode.INVALID_REQUEST.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        getRequestId(request)
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.withDetails(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        request.getRequestURI(),
                        "An unexpected error occurred. Please contact support if the problem persists.",
                        getRequestId(request)
                ));
    }

    private HttpStatus resolveHttpStatus(ErrorCode errorCode) {
        if (errorCode == null) {
            return HttpStatus.BAD_REQUEST;
        }

        return switch (errorCode) {
            case CONFLICT, EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case ACCESS_DENIED, INSUFFICIENT_PERMISSIONS -> HttpStatus.FORBIDDEN;
            case UNAUTHORIZED, INVALID_CREDENTIALS, TOKEN_INVALID,
                 TOKEN_EXPIRED, TOKEN_MISSING, INVALID_TOKEN_FORMAT -> HttpStatus.UNAUTHORIZED;
            case RESOURCE_NOT_FOUND, USER_NOT_FOUND, DEPENDENCY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case UNSUPPORTED_MEDIA_TYPE -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-ID");
    }
}
