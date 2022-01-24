package edu.hawaii.its.groupings.exceptions;

public class ApiServerHandshakeException extends InitializationException {

    private static final long serialVersionUID = 3L;

    public ApiServerHandshakeException(String message) {
        super(message);
    }

    public ApiServerHandshakeException(String message, Throwable cause) {
        super(message, cause);
    }
}
