package edu.hawaii.its.api.type;

public class GroupingsHTTPException extends RuntimeException{
    private Integer statusCode = null;

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
    }

    public Integer getStatusCode() {
        return statusCode;
    }

}
