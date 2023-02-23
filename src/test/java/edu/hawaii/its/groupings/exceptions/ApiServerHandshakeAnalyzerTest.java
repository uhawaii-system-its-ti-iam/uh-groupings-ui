package edu.hawaii.its.groupings.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
