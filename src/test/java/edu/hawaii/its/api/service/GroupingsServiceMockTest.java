package edu.hawaii.its.api.service;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsServiceMockTest {

    final String username = "username";
    final String group = "group";

    @Mock
    private GroupingsService groupingsService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void construction() {
        assertNotNull(groupingsService);
    }

    @Test
    public void checkSelfOpted() {

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
    public void getGrouping() {

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
