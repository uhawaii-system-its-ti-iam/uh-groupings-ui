package exceptions;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Component;

@Component
public class PasswordFoundException extends Exception {

    public PasswordFoundException(String location) {
        super("\n\nAPPLICATION FAILED TO START:\n\nCAUSE: Password Found\n\nLOCATION:" + location + "\n\n");
    }
}
