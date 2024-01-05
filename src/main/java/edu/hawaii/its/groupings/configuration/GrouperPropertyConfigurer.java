package edu.hawaii.its.groupings.configuration;

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

        setOverride(config, "grouperClient.webService.url");
        setOverride(config, "grouperClient.webService.login");
        setOverride(config, "grouperClient.webService.password");
    }

    private void setOverride(GrouperClientConfig config, String key) {
        if (overrideExists(key)) {
            config.propertiesOverrideMap().put(key, env.getProperty(key));
        }
    }

    private boolean overrideExists(String key) {
        return env.containsProperty(key);
    }
}
