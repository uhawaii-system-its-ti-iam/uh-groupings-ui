package edu.hawaii.its.groupings.exceptions;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.stereotype.Component;

@Component
public class ApiServerHandshakeAnalyzer extends AbstractFailureAnalyzer<InitializationException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, InitializationException cause) {
        String description = "Could not connect to the API server.";

        String action = "Start the UH Groupings API first \n"
                + "or check the connection configuration and \n"
                + "fix any possible problems.\n";

        return new FailureAnalysis(description, action, cause);
    }
}