package edu.hawaii.its.api.type;

public class ApiValidationError extends ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;

    public ApiValidationError(String object, String message) {
        super(message);
        this.object = object;
    }

    public ApiValidationError(String object, String field, Object rejectedValue, String message) {
        super(message);
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

}