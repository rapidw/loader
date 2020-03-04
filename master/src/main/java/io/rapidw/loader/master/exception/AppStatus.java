package io.rapidw.loader.master.exception;

public enum AppStatus {

    SUCCESS(0, "success"),
    INTERNAL_SERVER_ERROR(1, "internal server error"),
    AUTH_FAILED(2, "auth failed"),
    BAD_REQUEST(3, "invalid request parameters"),
    SYSTEM_ERROR(4, "system status error"),
    ;

    private String error;
    private int errorCode;

    AppStatus(int errorCode, String error) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return error;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
