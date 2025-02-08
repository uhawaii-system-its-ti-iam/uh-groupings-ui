package edu.hawaii.its.api.type;

public abstract class ApiSubError {
    private String message;

    protected ApiSubError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}