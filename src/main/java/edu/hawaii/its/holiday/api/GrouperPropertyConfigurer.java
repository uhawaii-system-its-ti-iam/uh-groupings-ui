package edu.hawaii.its.holiday.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class GrouperPropertyConfigurer {

    protected static ConfigurableEnvironment env;

    @Autowired
    public void setEnvironment(ConfigurableEnvironment env) {
        GrouperPropertyConfigurer.env = env;
    }

    public static String get(String key) {
        return env.getProperty(key);
    }
}
