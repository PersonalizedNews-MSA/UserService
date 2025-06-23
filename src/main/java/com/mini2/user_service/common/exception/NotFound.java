package com.mini2.user_service.common.exception;

public class NotFound extends ClientError {

    public NotFound(String errorMessage) {
        this.errorCode = "NotFound";
        this.errorMessage = errorMessage;
    }

}