package com.orderplatform.common.dto;

public record ApiResponse<T>(
boolean success,
    T data,
    String message,
    String traceId
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, getTraceId());
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, null, message, getTraceId());
    }

    private static String getTraceId() {
        return org.slf4j.MDC.get("traceId");
    }
}
