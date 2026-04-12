package org.example.medicinalplant.exception;

import org.example.medicinalplant.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<Void> handle(Exception e) {
        e.printStackTrace();
        return R.fail(e.getMessage() == null ? "服务器错误" : e.getMessage());
    }
}
