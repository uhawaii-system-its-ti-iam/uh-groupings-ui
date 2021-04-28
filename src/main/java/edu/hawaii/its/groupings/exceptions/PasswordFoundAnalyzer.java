package edu.hawaii.its.groupings.exceptions;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.stereotype.Component;

@Component
public class PasswordFoundAnalyzer extends AbstractFailureAnalyzer<PasswordFoundException> {

    @Override protected FailureAnalysis analyze(Throwable rootFailure, PasswordFoundException cause) {
        String description = "A password(s) was found.";
        String action = "Delete the password(s) in the following location(s):"
                + cause.getMessage();
        return new FailureAnalysis(description, action, cause);
    }
}