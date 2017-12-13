package edu.hawaii.its.api.type;

/**
 * Created by zknoebel on 12/12/2017.
 */
public class GroupingsHTTPException extends RuntimeException{
    private Integer statusCode = null;


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


}
