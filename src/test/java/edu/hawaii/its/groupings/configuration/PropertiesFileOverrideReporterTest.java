package edu.hawaii.its.groupings.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("localTest")
@SpringBootTest(classes = { SpringBootWebApplication.class })
@TestPropertySource(locations = "classpath:test-overrides.properties")
public class PropertiesFileOverrideReporterTest {

    @Autowired
    private PropertiesFileOverrideReporter propertiesFileOverrideReporter;

    @Test
    public void getMessageTest() {
        String message = propertiesFileOverrideReporter.getMessage();
        assertThat(message, equalTo("Properties override result: overridden"));
    }

}

