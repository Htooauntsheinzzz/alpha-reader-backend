package com.web.alpha.common.exception;

import java.util.Map;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
