package edu.hawaii.its.holiday.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "edu.hawaii.its.holiday")
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:custom.properties"),
        @PropertySource(value = "file:${user.home}/.${user.name}-conf/myiam-overrides.properties",
                        ignoreResourceNotFound = true)
})
public class AppConfig {
    // Empty.
}