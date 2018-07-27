package edu.hawaii.its.groupings.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Profile(value = { "localhost", "test", "integrationTest", "qa", "prod" })
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
