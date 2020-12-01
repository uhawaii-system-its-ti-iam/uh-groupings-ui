package edu.hawaii.its.groupings.access;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserBuilderTest {

    @Autowired
    private UserBuilder userBuilder;

    @Test
    public void testUidNull() {
        List<String> uids = new ArrayList<>();
        uids.add("   ");
        Map<String, List<String>> map = new HashMap<>();
        map.put("uid", uids);

        try {
            userBuilder.make(map);
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(UsernameNotFoundException.class, equalTo(e.getClass()));
            assertThat(e.getMessage(), containsString("uid is empty"));
        }
    }

    @Test
    public void testUidEmpty() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", "");

        try {
            userBuilder.make(map);
            fail("Should not reach here.");
        } catch (Exception e) {
            assertThat(UsernameNotFoundException.class, equalTo(e.getClass()));
            assertThat(e.getMessage(), containsString("uid is empty"));
        }
    }

    @Test(expected = UsernameNotFoundException.class)
    public void make() {
        userBuilder.make(new HashMap<String, String>());
    }
}
