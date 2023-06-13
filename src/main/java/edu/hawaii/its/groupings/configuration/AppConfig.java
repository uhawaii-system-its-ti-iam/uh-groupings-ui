package edu.hawaii.its.groupings.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages = "edu.hawaii.its")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
        @PropertySource(value = "file:${user.home}/.${user.name}-conf/uh-groupings-ui-overrides.properties",
                ignoreResourceNotFound = true)
})
public class AppConfig {
    // Empty.
}
