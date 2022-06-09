package edu.hawaii.its.groupings.configuration;

import edu.internet2.middleware.grouperClient.util.GrouperClientConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
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
        String testUrl = "test-url-b";

        String value = config.propertiesOverrideMap().get(key);
        assertNotEquals(value, testUrl);

        // Will cause an override of value.
        env.getSystemProperties().put(key, testUrl);

        grouperPropertyConfigurer.init();

        value = config.propertiesOverrideMap().get(key);
        assertThat(value, equalTo(testUrl));
    }
}