package edu.hawaii.its.groupings.access;

import edu.hawaii.its.api.controller.GroupingsRestController;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;
import org.jasig.cas.client.authentication.SimplePrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class UserBuilderTest {

    @Autowired
    private UserBuilder userBuilder;

    @Autowired
    private AuthorizationService authorizationService;

    @MockBean
    private GroupingsRestController groupingsRestController;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void basics() {
        assertNotNull(authorizationService);
    }

    @Test
    @WithMockUhUser
    public void testAdminUser() {
        Map<String, String> map = new HashMap<>();

        Principal principal = new SimplePrincipal(userContextService.getCurrentUhUuid());

        map.put("uid", userContextService.getCurrentUser().getUid());
        map.put("uhUuid", userContextService.getCurrentUhUuid());


        given(groupingsRestController.hasOwnerPrivs(principal))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));
        given(groupingsRestController.hasAdminPrivs(principal))
                .willReturn(new ResponseEntity<>("true", HttpStatus.OK));

        User user = userBuilder.make(map);

        // Check results.
        assertEquals(4, user.getAuthorities().size());
        assertTrue(user.hasRole(Role.ANONYMOUS));
        assertTrue(user.hasRole(Role.UH));
        assertTrue(user.hasRole(Role.ADMIN));
        assertTrue(user.hasRole(Role.OWNER));
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
