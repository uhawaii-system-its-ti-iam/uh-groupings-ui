package edu.hawaii.its.groupings.exceptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class ApiServerHandshakeAnalyzerTest {

    @Test
    public void construction() {
        ApiServerHandshakeAnalyzer analyzer = new ApiServerHandshakeAnalyzer();
        assertNotNull(analyzer);

        FailureAnalysis fa = analyzer.analyze(null, null);
        assertNotNull(fa);
        assertThat(fa.getDescription(), startsWith("Could not connect to the API server"));
        assertThat(fa.getAction(), startsWith("Start the UH Groupings API"));
    }
}
