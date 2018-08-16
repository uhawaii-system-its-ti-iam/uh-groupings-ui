package edu.hawaii.its.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.util.Dates;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssign;
import edu.internet2.middleware.grouperClient.ws.beans.WsAttributeAssignValue;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration
public class GrouperFactoryServiceMockTest {

    GrouperFactoryServiceImpl gfs = new GrouperFactoryServiceImpl();

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

    @Test
    public void makeWsAttributeAssignValueTest() {
        String time = Dates.formatDate(LocalDateTime.now(), "yyyyMMdd'T'HHmm");

        WsAttributeAssignValue attributeAssignValue = gfs.makeWsAttributeAssignValue(time);
        assertEquals(time, attributeAssignValue.getValueSystem());
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
