package edu.hawaii.its.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.util.Dates;

import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@WebAppConfiguration
public class GroupingsFactoryServiceMockTest {

    private static final String PATH_ROOT = "path:to:grouping";

    private static final String GROUPING_0_PATH = PATH_ROOT + 0;
    private static final String GROUPING_1_PATH = PATH_ROOT + 1;
    private static final String GROUPING_2_PATH = PATH_ROOT + 2;
    private static final String GROUPING_3_PATH = PATH_ROOT + 3;
    private static final String GROUPING_4_PATH = PATH_ROOT + 4;

    private static final String GROUPING_0_INCLUDE_PATH = GROUPING_0_PATH + ":include";
    private static final String GROUPING_0_OWNERS_PATH = GROUPING_0_PATH + ":owners";

    private static final String GROUPING_1_INCLUDE_PATH = GROUPING_1_PATH + ":include";
    private static final String GROUPING_1_EXCLUDE_PATH = GROUPING_1_PATH + ":exclude";

    private static final String GROUPING_2_INCLUDE_PATH = GROUPING_2_PATH + ":include";
    private static final String GROUPING_2_EXCLUDE_PATH = GROUPING_2_PATH + ":exclude";
    private static final String GROUPING_2_BASIS_PATH = GROUPING_2_PATH + ":basis";
    private static final String GROUPING_2_OWNERS_PATH = GROUPING_2_PATH + ":owners";

    private static final String GROUPING_3_INCLUDE_PATH = GROUPING_3_PATH + ":include";
    private static final String GROUPING_3_EXCLUDE_PATH = GROUPING_3_PATH + ":exclude";

    private static final String GROUPING_4_EXCLUDE_PATH = GROUPING_4_PATH + ":exclude";

    GrouperFactoryServiceImpl gfs = new GrouperFactoryServiceImpl();
    GrouperFactoryServiceImplLocal gfsl = new GrouperFactoryServiceImplLocal();

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
