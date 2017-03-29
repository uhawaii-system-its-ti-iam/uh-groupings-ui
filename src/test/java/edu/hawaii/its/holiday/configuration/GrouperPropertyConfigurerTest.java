package edu.hawaii.its.holiday.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.internet2.middleware.grouperClient.util.GrouperClientConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class GrouperPropertyConfigurerTest {

    @Autowired
    private ConfigurableEnvironment env;

    @Autowired
    private GrouperPropertyConfigurer grouperPropertyConfigurer;

    @Test
    public void construction() {
        assertNotNull(grouperPropertyConfigurer);
    }

    @Test
    public void testing() {
        GrouperClientConfig config = GrouperClientConfig.retrieveConfig();
        String key = "grouperClient.webService.url";

        String value = config.propertiesOverrideMap().get(key);
        assertThat(value, equalTo(null));

        // Will cause an override of value.
        env.getSystemProperties().put(key, "test-url-b");

        grouperPropertyConfigurer.init();

        value = config.propertiesOverrideMap().get(key);
        assertThat(value, equalTo("test-url-b"));
    }

}