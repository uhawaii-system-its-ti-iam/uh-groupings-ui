package edu.hawaii.its.groupings.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class Realm {

    private static final Log logger = LogFactory.getLog(Realm.class);

    @Autowired
    private Environment environment;

    private Map<String, Boolean> profileMap = new ConcurrentHashMap<>();

    @PostConstruct
    public synchronized void init() {
        logger.info("init; starting");
        logger.info("init; environment: " + environment);
        Assert.notNull(environment, "Property 'environment' is required.");
        for (String p : environment.getActiveProfiles()) {
            profileMap.put(p, Boolean.TRUE);
        }
        logger.info("init; finished");
    }

    /*
     * Determine if a profile is active.
     *
     * @param profile The name of the profile.
     */
    public boolean isProfileActive(String profile) {
        return profile != null && profileMap.containsKey(profile);
    }

    public boolean isAnyProfileActive(String... profiles) {
        if (profiles != null && profiles.length > 0) {
            for (String p : profiles) {
                if (p != null && profileMap.containsKey(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isProduction() {
        return isProfileActive("prod");
    }

    public boolean isTest() {
        return isProfileActive("test");
    }

    public boolean isDefault() {
        return isProfileActive("default");
    }

}
