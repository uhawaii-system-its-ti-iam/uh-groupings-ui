package edu.hawaii.its.groupings.api;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsServiceTest {
    final String username = "username";
    final String group = "group";

    @MockBean
    private GroupingsService groupingsService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void construction() {
        assertNotNull(groupingsService);
    }

    @Test
    public void checkSelfOpted() {
        given(groupingsService.checkSelfOpted(group, username))
                .willReturn(true);

        assertTrue(groupingsService.checkSelfOpted(group, username));
    }

    @Test
    public void hasListserv() {

    }

    @Test
    public void groupingsIn() {

    }

    @Test
    public void groupingsOptedInto() {

    }

    @Test
    public void groupingsOptedOutOf() {

    }

    @Test
    public void inGroup() {

    }

    @Test
    public void addMemberAs() {

    }

    @Test
    public void deleteMemberAs() {

    }

    @Test
    public void assignOwnership() {

    }

    @Test
    public void removeOwnership() {

    }

    @Test
    public void getGrouping () {

    }

    @Test
    public void getMyGroupings() {

    }

    @Test
    public void optIn() {

    }

    @Test
    public void optOut() {

    }
    @Test
    public void cancelOptIn() {

    }

    @Test
    public void cancelOptOut() {

    }

    @Test
    public void changeListservStatus() {

    }

    @Test
    public void changeOptInStatus() {

    }

    @Test
    public void changeOptOutStatus() {

    }

    @Test
    public void findOwners() {

    }

    @Test
    public void isOwner() {

    }

    @Test
    public void groupOptInPermission() {

    }

    @Test
    public void addSelfOpted() {

    }

    @Test
    public void removeSelfOpted() {

    }

    @Test
    public void groupOptOutPermission() {

    }

    @Test
    public void updateLastModified() {

    }

    @Test
    public void groupHasAttribute() {

    }

    @Test
    public void optOutPermission() {

    }

    @Test
    public void optInPermission() {

    }
}
