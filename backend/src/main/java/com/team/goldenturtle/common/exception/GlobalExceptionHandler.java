package com.team.goldenturtle.common.exception;

import com.team.goldenturtle.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 BusinessException을 처리합니다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("BusinessException: {}: {}", errorCode.getCode(), errorCode.getMessage(), e); // 예외를 로깅
        return ResponseEntity.status(errorCode.getHttpStatusCode())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    // 일반 IllegalArgumentException을 처리합니다. BusinessException으로 아직 커버되지 않은 경우에 사용합니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> illegalArgumentException(IllegalArgumentException e) {
        // 현재는 일반적인 IllegalArgumentException을 BAD_REQUEST (400)로 매핑합니다.
        // 리팩토링을 계속하면서, 이들 중 많은 부분이 특정 ErrorCode를 가진 BusinessException으로 대체될 수 있습니다.
        log.error("IllegalArgumentException: {}", e.getMessage(), e); // 예외를 로깅
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ErrorCode.INVALID_REQUEST.getCode(), e.getMessage()));
    }

    // 처리되지 않은 다른 모든 예외를 처리하여 일반 500 Internal Server Error를 반환합니다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e); // 예외를 로깅
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
