package edu.hawaii.its.api.type;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class GroupingsHTTPException extends RuntimeException {

    private int statusCode;
    private String exceptionMessage;

    public GroupingsHTTPException() {
        // Empty constructor.
    }

    public GroupingsHTTPException(String message) {
        super(message);
    }

    public GroupingsHTTPException(String message, Throwable cause) {
        super(message, cause);
        this.exceptionMessage = ExceptionUtils.getStackTrace(cause);
    }

    public GroupingsHTTPException(String message, Throwable cause, int statusCode) {
        this(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return a string containing the stack trace of the exception thrown
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
