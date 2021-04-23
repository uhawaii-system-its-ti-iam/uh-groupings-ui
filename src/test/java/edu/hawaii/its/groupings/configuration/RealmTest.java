package edu.hawaii.its.groupings.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.groupings.configuration.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles(value = {"dev", "localTest"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })

public class RealmTest {
    @Autowired
    private Realm realm;

    @Test
    public void basics() {
        assertNotNull(realm);
        assertTrue(realm.isDev());
        assertFalse(realm.isTest());
        assertFalse(realm.isProduction());
        assertFalse(realm.isProfileActive("not"));
        assertFalse(realm.isProfileActive(""));
        assertFalse(realm.isProfileActive(null));
        assertFalse(realm.isAnyProfileActive());
        assertFalse(realm.isAnyProfileActive((String) null));
        assertFalse(realm.isAnyProfileActive("not"));
        assertFalse(realm.isAnyProfileActive("not", "question"));
        assertTrue(realm.isAnyProfileActive("not", "question", "dev"));
        assertFalse(realm.isAnyProfileActive("test", "prod"));
        assertTrue(realm.isAnyProfileActive("test", "prod", "dev"));
        String[] array = null;
        assertFalse(realm.isAnyProfileActive(array));
        array = new String[3];
        assertFalse(realm.isAnyProfileActive(array));
        array[2] = "dev";
        assertTrue(realm.isAnyProfileActive(array));
    }
}