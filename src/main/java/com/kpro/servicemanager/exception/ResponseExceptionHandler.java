package com.kpro.servicemanager.exception;

import com.kpro.common.dto.base.ApiMessage;
import com.kpro.common.dto.base.BaseApiResponse;
import com.kpro.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(value = PriorityOrdered.HIGHEST_PRECEDENCE)
@Slf4j
public class ResponseExceptionHandler extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<Object> handleException(Throwable ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof BaseException exception) {
            return new ResponseEntity<>(exception.getErrorObject(), headers, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<BaseApiResponse> handleAccessDeniedException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        BaseApiResponse<Object> rs = new BaseApiResponse<>();
        rs.setMessages(Collections.singletonList(new ApiMessage("403", "Access denied")));
        rs.setData(null);
        return new ResponseEntity<>(rs, headers, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.debug("Exception handle invalid message params {}", ex);
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(fieldName, errorMessage);
                        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(errors);
    }
}
