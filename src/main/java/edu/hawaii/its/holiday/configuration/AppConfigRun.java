package edu.hawaii.its.holiday.configuration;

import org.springframework.context.annotation.*;

@Profile(value = {"localhost", "test", "integrationTest"})
@Configuration
@ComponentScan(basePackages = "edu.hawaii.its")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
        @PropertySource(value = "file:${user.home}/.${user.name}-conf/myiam-overrides.properties",
                ignoreResourceNotFound = true)
})
public class AppConfigRun {
    // Empty.
}
