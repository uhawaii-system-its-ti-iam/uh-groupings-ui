package edu.hawaii.its.holiday.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import edu.internet2.middleware.grouperClient.util.GrouperClientConfig;

@Configuration
public class GrouperPropertyConfigurer {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        GrouperClientConfig config = GrouperClientConfig.retrieveConfig();

        String key = "grouperClient.webService.url";
        config.propertiesOverrideMap().put(key, env.getProperty(key));

        key = "grouperClient.webService.login";
        config.propertiesOverrideMap().put(key, env.getProperty(key));

        key = "grouperClient.webService.password";
        config.propertiesOverrideMap().put(key, env.getProperty(key));
    }

}
