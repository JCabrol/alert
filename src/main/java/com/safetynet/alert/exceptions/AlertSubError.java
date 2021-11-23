package com.safetynet.alert.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

abstract class AlertSubError {

}

@Data
@EqualsAndHashCode(callSuper = false)
class AlertValidationError extends AlertSubError {

    private String message;

    AlertValidationError(String message) {
        this.message = message;
    }
}