package edu.hawaii.its.groupings.exceptions;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.stereotype.Component;

@Component
public class CredentialInitializationAnalyzer extends AbstractFailureAnalyzer<CredentialInitializationException> {

    @Override protected FailureAnalysis analyze(Throwable rootFailure, CredentialInitializationException cause) {
        String description = "Overrides file credentials cannot be found.";
        String action = "Check overrides file formatting for missing or invalid credentials.";
        return new FailureAnalysis(description, action, cause);
    }
}