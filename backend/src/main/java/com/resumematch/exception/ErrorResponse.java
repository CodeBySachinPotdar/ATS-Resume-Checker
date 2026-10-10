package com.resumematch.exception;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {
    private boolean success;
    private String errorCode;
    private String message;
    private String timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponse() {
        this.success = false;
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(String errorCode, String message) {
        this.success = false;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public ErrorResponse(String errorCode, String message, Map<String, String> validationErrors) {
        this.success = false;
        this.errorCode = errorCode;
        this.message = message;
        this.validationErrors = validationErrors;
        this.timestamp = Instant.now().toString();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
}
