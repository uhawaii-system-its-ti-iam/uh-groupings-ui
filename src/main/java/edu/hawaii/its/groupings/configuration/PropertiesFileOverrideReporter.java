package edu.hawaii.its.groupings.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class PropertiesFileOverrideReporter {

    private static final Log logger = LogFactory.getLog(PropertiesFileOverrideReporter.class);
    private final String message;

    public PropertiesFileOverrideReporter(@Value("${properties.override.result:DEFAULT}") String result) {
        this.message = "Properties override result: " + result;
    }

    @PostConstruct
    public void checkOverridesStatus() {
        logger.info(this.message);
    }

    public String getMessage() {
        return this.message;
    }

}

