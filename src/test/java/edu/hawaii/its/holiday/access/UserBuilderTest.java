package edu.hawaii.its.holiday.access;

import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class UserBuilderTest {

    @Autowired
    private UserBuilder userBuilder;

    @Test
    public void testAdminUsers() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", "duckart");
        map.put("uhuuid", "89999999");
        User user = userBuilder.make(map);

        // Basics.
        assertEquals("duckart", user.getUsername());
        assertEquals("duckart", user.getUid());
        assertEquals(Long.valueOf(89999999), user.getUhuuid());

        // Granted Authorities.
        assertTrue(user.getAuthorities().size() > 0);
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.EMPLOYEE));
        assertTrue(user.hasRole(Role.ADMIN));

        map = new HashMap<String, String>();
        map.put("uid", "someuser");
        map.put("uhuuid", "10000001");
        user = userBuilder.make(map);

        assertEquals("someuser", user.getUsername());
        assertEquals("someuser", user.getUid());
        assertEquals(Long.valueOf(10000001), user.getUhuuid());

        assertTrue(user.getAuthorities().size() > 0);
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.EMPLOYEE));
        assertTrue(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testEmployees() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", "jjcale");
        map.put("uhuuid", "10000004");
        User user = userBuilder.make(map);

        // Basics.
        assertEquals("jjcale", user.getUsername());
        assertEquals("jjcale", user.getUid());
        assertEquals(Long.valueOf(10000004), user.getUhuuid());

        // Granted Authorities.
        assertEquals(3, user.getAuthorities().size());
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.EMPLOYEE));

        assertFalse(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testEmployeesWithMultivalueUid() {
        Map<String, Object> map = new HashMap<String, Object>();
        ArrayList<Object> uids = new ArrayList<Object>();
        uids.add("aaaaaaa");
        uids.add("bbbbbbb");
        map.put("uid", uids);
        map.put("uhuuid", "10000003");
        User user = userBuilder.make(map);

        // Basics.
        assertEquals("aaaaaaa", user.getUsername());
        assertEquals("aaaaaaa", user.getUid());
        assertEquals(Long.valueOf(10000003), user.getUhuuid());

        // Granted Authorities.
        assertEquals(4, user.getAuthorities().size());
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.EMPLOYEE));
        assertTrue(user.hasRole(Role.ADMIN));
    }

    @Test
    public void testNotAnEmployee() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", "nobody");
        map.put("uhuuid", "10000009");
        User user = userBuilder.make(map);

        // Basics.
        assertEquals("nobody", user.getUsername());
        assertEquals("nobody", user.getUid());
        assertEquals(Long.valueOf(10000009), user.getUhuuid());

        // Granted Authorities.
        assertEquals(2, user.getAuthorities().size());
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertFalse(user.hasRole(Role.EMPLOYEE));
        assertFalse(user.hasRole(Role.ADMIN));
    }

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
            assertEquals(e.getClass(), UsernameNotFoundException.class);
            assertThat(e.getMessage(), containsString("uid is empty"));
        }
    }

    @Test
    public void testUidEmpty() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", "");

        try {
            userBuilder.make(map);
            fail("Should not reach here.");
        } catch (Exception e) {
            assertEquals(e.getClass(), UsernameNotFoundException.class);
            assertThat(e.getMessage(), containsString("uid is empty"));
        }
    }

    @Test(expected = UsernameNotFoundException.class)
    public void make() {
        userBuilder.make(new HashMap<String, String>());
    }
}
