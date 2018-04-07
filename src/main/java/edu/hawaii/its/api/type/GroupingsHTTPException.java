package edu.hawaii.its.api.type;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GroupingsHTTPException extends RuntimeException{
    private Integer statusCode = null;
    private String string = null;

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

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        this.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        this.string = sStackTrace;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getString() {
        return string;
    }
}
