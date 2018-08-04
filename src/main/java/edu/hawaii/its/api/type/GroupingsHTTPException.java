package edu.hawaii.its.api.type;

import java.io.PrintWriter;
import java.io.StringWriter;

//todo Tested by Integration tests
public class GroupingsHTTPException extends RuntimeException{
    private Integer statusCode = null;
    private String string = null;

    public GroupingsHTTPException() {
        //empty
    }

    //todo Not tested
    public GroupingsHTTPException(String message) {
        super(message);
    }

    //todo Not tested
    public GroupingsHTTPException(String message, Throwable cause) {
        super(message, cause);
    }

    // Covered by Integration Tests
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

    // Covered by Integration Tests
    public Integer getStatusCode() {
        return statusCode;
    }

    // Covered by Integration Tests
    public String getString() {
        return string;
    }
}
