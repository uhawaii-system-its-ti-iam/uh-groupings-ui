package edu.hawaii.its.groupings.exceptions;

import org.springframework.beans.factory.BeanCreationException;

public class InitializationException extends BeanCreationException {

    private static final long serialVersionUID = 3L;

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
