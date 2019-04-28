package com.mobiquityinc.exception;

public class APIException extends Exception {

    public APIException(String message, Exception ex) {
        super(message, ex);
    }

    public APIException(String message) {
        super(message);
    }

}
