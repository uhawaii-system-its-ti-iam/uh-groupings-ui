package edu.hawaii.its.groupings.exceptions;
import org.springframework.beans.factory.BeanCreationException;

public class CredentialInitializationException extends BeanCreationException {
    private static final long serialVersionUID = 1L;
    public CredentialInitializationException(String message) {
        super(message);
    }
    public CredentialInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
