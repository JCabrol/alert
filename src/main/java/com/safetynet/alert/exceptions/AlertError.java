package com.safetynet.alert.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
class AlertError {

    private HttpStatus status;

    private String message;

    AlertError(HttpStatus status) {
        this.status = status;
    }

    AlertError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

