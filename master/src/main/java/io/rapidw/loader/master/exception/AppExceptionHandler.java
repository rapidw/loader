package io.rapidw.loader.master.exception;

import io.rapidw.loader.master.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse> handleAppException(AppException e) {
        if (e.getStatus() == AppStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(new BaseResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(new BaseResponse(e), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 处理 自动校验未通过 产生的异常
     */
    @ExceptionHandler({BindException.class})
    public ResponseEntity<BaseResponse> handleBindException(BindException e) {
        String splitStr = "; ";
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : e.getAllErrors()) {
            if (error instanceof FieldError) {
                sb.append(((FieldError) error).getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append(splitStr);
            }
        }
        String resMsg = StringUtils.removeEnd(sb.toString(), splitStr);
        log.error("BindException:[{}]", resMsg);
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST, resMsg), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数校验异常
     *
     * @param e 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrorList) {
            stringBuilder.append(error.getField()).append("(").append(error.getDefaultMessage()).append("); ");
        }
        String errMsg = StringUtils.removeEnd(stringBuilder.toString(), ";");
        log.error("MethodArgumentNotValidException:[{}]", errMsg);
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST, errMsg), HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理前端传递数据格式错误引起的解析异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<BaseResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST, e.getMostSpecificCause().getMessage()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<BaseResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 鉴权失败
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new BaseResponse(AppStatus.AUTH_FAILED), HttpStatus.FORBIDDEN);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse> handleHttpRequestMethodNotSupportedException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new BaseResponse(AppStatus.BAD_REQUEST, "method not supported"), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return new ResponseEntity<>(new BaseResponse(AppStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
