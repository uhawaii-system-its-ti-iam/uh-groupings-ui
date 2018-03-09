package edu.hawaii.its.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@WebAppConfiguration
public class GroupingsFactoryServiceMockTest {

    GrouperFactoryServiceImpl gfs = new GrouperFactoryServiceImpl();

    //todo
    @Test
    public void addEmptyGroupTest() {
    }

    @Test
    public void makeWsSubjectLookupTest() {
        String username = "username";
        WsSubjectLookup lookup = gfs.makeWsSubjectLookup(username);

        assertEquals(username, lookup.getSubjectIdentifier());
    }

    @Test
    public void makeWsGroupLookupTest() {
        String groupPath = "path:to:group";
        WsGroupLookup groupLookup = gfs.makeWsGroupLookup(groupPath);
        assertEquals(groupPath, groupLookup.getGroupName());
    }

    @Test
    public void makeWsStemLookupTest() {
        String stemPath = "path:to:stem";
        String stemUuid = "12345";
        WsStemLookup stemLookup = gfs.makeWsStemLookup(stemPath);
        assertEquals(stemPath, stemLookup.getStemName());

        stemLookup = gfs.makeWsStemLookup(stemPath, stemUuid);
        assertEquals(stemPath, stemLookup.getStemName());
        assertEquals(stemUuid, stemLookup.getUuid());
    }

    //todo
    @Test
    public void makeWsStemSaveResultsTest() {
    }

    @Test
    public void makeWsAttributeAssignValueTest() {
        String time = Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");

        WsAttributeAssignValue attributeAssignValue = gfs.makeWsAttributeAssignValue(time);
        assertEquals(time, attributeAssignValue.getValueSystem());
    }

    //todo
    @Test
    public void makeWsAddMemberResultsWithLookupTest() {
    }

    //todo
    @Test
    public void makeWsAddMemberResultsWithListTest() {
    }

    //todo
    @Test
    public void makeWsAddMemberResultsTest() {
    }

    //todo
    @Test
    public void makeWsDeleteMemberResultsTest() {
    }

    //todo
    @Test
    public void makeWsDeleteMemberResultsTestWithSubjectLookup() {
    }

    //todo
    @Test
    public void makeWsDeleteMemberResultsWithListTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsTrioTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsTrioWithListTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsTrioWithTwoAttributeDefNamesTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsForMembershipTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsForGroupTest() {
    }

    //todo
    @Test
    public void makeWsGetAttributeAssignmentsResultsForGroupWithAttributeDefNameTest() {
    }

    //todo
    @Test
    public void makeWsHasMemberResultsTest() {
    }

    //todo
    @Test
    public void makeWsAssignAttributesReultsTest() {
    }

    //todo
    @Test
    public void makeWsAssignAttributesResultsForMembershipTest() {
    }

    //todo
    @Test
    public void makeWsAssignAttributesResultsForGroupTest() {
    }

    //todo
    @Test
    public void makeWsAssignAttributesResultsForGroup() {
    }

    //todo
    @Test
    public void makeWsAssignGrouperPrivilegesLiteResult() {
    }

    //todo
    @Test
    public void makeWsGetGrouperPrivilegesLiteResult() {
    }

    //todo
    @Test
    public void makeWsGetMembershipsResults() {
    }

    //todo
    @Test
    public void makeWsGetMembersResults() {
    }


    //todo
    @Test
    public void makeWsGetGroupsResults() {
    }


    @Test
    public void makeEmptyWsAttributeAssignArrayTest() {
        WsAttributeAssign[] emptyAttributeAssigns = new WsAttributeAssign[0];
        assertTrue(Arrays.equals(emptyAttributeAssigns, gfs.makeEmptyWsAttributeAssignArray()));
    }


    @Test
    public void toStringTest() {
        assertEquals("GrouperFactoryServiceImpl", gfs.toString());
    }
}
