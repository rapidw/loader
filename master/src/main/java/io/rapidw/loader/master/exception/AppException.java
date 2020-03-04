package io.rapidw.loader.master.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException {

    private AppStatus status;
    private String message;

    public AppException(AppStatus status) {
        this.status = status;
    }
}
