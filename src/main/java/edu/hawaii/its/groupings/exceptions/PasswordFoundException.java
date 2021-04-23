package edu.hawaii.its.groupings.exceptions;

public class PasswordFoundException extends PatternFoundException {

    public PasswordFoundException(String location) {
        super(location);
    }
}
