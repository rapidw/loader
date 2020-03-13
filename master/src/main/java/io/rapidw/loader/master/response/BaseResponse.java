package io.rapidw.loader.master.response;


import io.rapidw.loader.master.exception.AppException;
import io.rapidw.loader.master.exception.AppStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponse {
    private Integer errorCode;
    private String errorInfo;

    public final static BaseResponse SUCCESS = new BaseResponse(AppStatus.SUCCESS);
    public final static BaseResponse AUTH_FAILED = new BaseResponse(AppStatus.AUTH_FAILED);

    public BaseResponse(AppStatus status) {
        this.errorCode = status.getErrorCode();
        this.errorInfo = status.getErrorInfo();
    }

    public BaseResponse(AppException exception) {
        this(exception.getStatus(), exception.getMessage());
    }

    public BaseResponse(AppStatus status, String message) {
        this.errorCode = status.getErrorCode();
        if (message == null) {
            this.errorInfo = status.getErrorInfo();
        } else {
            this.errorInfo = status.getErrorInfo() + ": " + message;
        }
    }
}
