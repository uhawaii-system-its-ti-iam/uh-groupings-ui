package edu.hawaii.its.groupings.type;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Feedback {

    private String name;
    private String email;
    private String type;
    private String message;

    // Constructor.
    public Feedback() {
        // Empty.
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

     @Override
    public String toString() {
        return "Feedback [email=" + email
                + ", message=" + name
                + "]";
    }

}
