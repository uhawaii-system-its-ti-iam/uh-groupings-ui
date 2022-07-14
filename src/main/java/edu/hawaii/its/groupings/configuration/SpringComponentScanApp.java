package edu.hawaii.its.groupings.configuration;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("edu.hawaii.its")
public class SpringComponentScanApp {
    private static final Log logger = LogFactory.getLog(SpringComponentScanApp.class);
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("init; Component discovery found...");
        for (String name : applicationContext.getBeanDefinitionNames()) {
            logger.info("component: " + name);
        }
        logger.info("init; done.");
    }
}