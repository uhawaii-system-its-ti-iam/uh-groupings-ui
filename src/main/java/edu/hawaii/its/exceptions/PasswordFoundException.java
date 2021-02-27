package edu.hawaii.its.exceptions;

public class PasswordFoundException extends PatternFoundException {

    public PasswordFoundException(String location) {
        super(location);
    }
}
