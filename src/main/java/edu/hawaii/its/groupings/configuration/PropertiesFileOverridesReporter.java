package edu.hawaii.its.groupings.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class PropertiesFileOverridesReporter {

    private static final Log logger = LogFactory.getLog(PropertiesFileOverridesReporter.class);
    private final String result;

    public PropertiesFileOverridesReporter(@Value("${properties.override.result:default}") String result) {
        this.result = result;
    }

    @PostConstruct
    public void checkOverridesStatus() {
        logger.info("Properties override result: " + result);
    }
}
