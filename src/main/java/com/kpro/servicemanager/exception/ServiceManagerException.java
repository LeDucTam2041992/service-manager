package com.kpro.servicemanager.exception;

import com.kpro.common.exception.BaseException;
import com.kpro.common.exception.ErrorObject;

public class ServiceManagerException extends BaseException {
    public ServiceManagerException() {
    }

    public ServiceManagerException(String errorCode) {
        super(errorCode);
    }

    public ServiceManagerException(String errorCode, Object tag) {
        super(errorCode, tag);
    }

    public ServiceManagerException(ErrorObject errorObject, Object tag) {
        super(errorObject, tag);
    }

    public ServiceManagerException(ErrorObject errorObject) {
        super(errorObject);
    }

    public ServiceManagerException(String message, ErrorObject errorObject) {
        super(message, errorObject);
    }

    public ServiceManagerException(String message, Throwable cause, ErrorObject errorObject) {
        super(message, cause, errorObject);
    }

    public ServiceManagerException(Throwable cause, ErrorObject errorObject) {
        super(cause, errorObject);
    }

    public ServiceManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorObject errorObject) {
        super(message, cause, enableSuppression, writableStackTrace, errorObject);
    }
}
