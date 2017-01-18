package edu.hawaii.its.holiday.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Profile(value = { "localhost", "test" })
@Configuration
@ComponentScan(basePackages = "edu.hawaii.its.holiday")
@PropertySources({
        @PropertySource("classpath:custom.properties"),
        @PropertySource(value = "file:${user.home}/.${user.name}-conf/myiam-overrides.properties",
                        ignoreResourceNotFound = true)
})
public class AppConfigPrimary {
    // Empty.
}
