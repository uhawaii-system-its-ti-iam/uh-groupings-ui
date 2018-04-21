package edu.hawaii.its.api.type;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class GroupingsHTTPException extends RuntimeException {
    private int statusCode;
    private String exceptionMessage;

    public GroupingsHTTPException() {
        //empty
    }

    public GroupingsHTTPException(String message) {
        super(message);
    }

    public GroupingsHTTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupingsHTTPException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.setStackTrace(cause.getStackTrace());

        this.exceptionMessage = ExceptionUtils.getStackTrace(cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
