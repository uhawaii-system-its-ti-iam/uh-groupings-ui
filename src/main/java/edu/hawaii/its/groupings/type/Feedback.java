package edu.hawaii.its.groupings.type;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Feedback {

    private String name;
    private String email;
    private String type;
    private String message;
    private Throwable exception;
    private String exceptionError;

    // Constructor.
    public Feedback() {
        // Empty.
    }

    public Feedback(Throwable exception) {
        this.exception = exception;
        updateExceptionStr(exception);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getExceptionError() {
        return exceptionError;
    }

    public void setExceptionError(String exceptionError) {
        this.exceptionError = exceptionError;
    }

    private void updateExceptionStr(Throwable exception) {
        this.exceptionError = null;
        if(exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            this.exceptionError = sw.toString();
        }
    }

    @Override
    public String toString() {
        return "Feedback [email=" + email
                + ", message=" + message
                + ", name=" + name
                + ", type=" + type +
                "]";
    }

}
