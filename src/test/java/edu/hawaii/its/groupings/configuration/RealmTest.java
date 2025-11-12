package edu.hawaii.its.groupings.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"default", "localTest"})
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class RealmTest {

    @Autowired
    private Realm realm;

    @Test
    public void basics() {
        assertNotNull(realm);
        assertTrue(realm.isDefault());
        assertFalse(realm.isTest());
        assertFalse(realm.isProduction());
        assertFalse(realm.isOotb());

        assertFalse(realm.isProfileActive("not"));
        assertFalse(realm.isProfileActive(""));
        assertFalse(realm.isProfileActive(null));

        assertFalse(realm.isAnyProfileActive());
        assertFalse(realm.isAnyProfileActive((String) null));
        assertFalse(realm.isAnyProfileActive("not"));
        assertFalse(realm.isAnyProfileActive("not", "question"));
        assertTrue(realm.isAnyProfileActive("not", "question", "default"));
        assertFalse(realm.isAnyProfileActive("test", "prod"));

        // This test is using the 'default' profile.
        assertTrue(realm.isAnyProfileActive("default"));
        assertTrue(realm.isAnyProfileActive("test", "default"));
        assertTrue(realm.isAnyProfileActive("test", "qa", "default"));
        assertTrue(realm.isAnyProfileActive("test", "qa", "prod", "default"));

        assertFalse(realm.isAnyProfileActive("test"));
        assertFalse(realm.isAnyProfileActive("test", "qa"));
        assertFalse(realm.isAnyProfileActive("test", "qa", "prod"));

        String[] array = null;
        assertFalse(realm.isAnyProfileActive(array));
        array = new String[3];
        assertFalse(realm.isAnyProfileActive(array));
        array[2] = "default";
        assertTrue(realm.isAnyProfileActive(array));
    }

}
