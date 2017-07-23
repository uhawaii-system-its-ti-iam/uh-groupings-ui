package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
public class GroupingsServiceMockTest {
    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.admins}")
    private String ADMINS;

    @Value("${groupings.api.attributes}")
    private String ATTRIBUTES;

    @Value("${groupings.api.for_groups}")
    private String FOR_GROUPS;

    @Value("${groupings.api.for_memberships}")
    private String FOR_MEMBERSHIPS;

    @Value("${groupings.api.last_modified}")
    private String LAST_MODIFIED;

    @Value("${groupings.api.yyyymmddThhmm}")
    private String YYYYMMDDTHHMM;

    @Value("${groupings.api.uhgrouping}")
    private String UHGROUPING;

    @Value("${groupings.api.destinations}")
    private String DESTINATIONS;

    @Value("${groupings.api.listserv}")
    private String LISTSERV;

    @Value("${groupings.api.trio}")
    private String TRIO;

    @Value("${groupings.api.self_opted}")
    private String SELF_OPTED;

    @Value("${groupings.api.anyone_can}")
    private String ANYONE_CAN;

    @Value("${groupings.api.opt_in}")
    private String OPT_IN;

    @Value("${groupings.api.opt_out}")
    private String OPT_OUT;

    @Value("${groupings.api.basis}")
    private String BASIS;

    @Value("${groupings.api.basis_plus_include}")
    private String BASIS_PLUS_INCLUDE;

    @Value("${groupings.api.exclude}")
    private String EXCLUDE;

    @Value("${groupings.api.include}")
    private String INCLUDE;

    @Value("${groupings.api.owners}")
    private String OWNERS;

    @Value("${groupings.api.assign_type_group}")
    private String ASSIGN_TYPE_GROUP;

    @Value("${groupings.api.assign_type_immediate_membership}")
    private String ASSIGN_TYPE_IMMEDIATE_MEMBERSHIP;

    @Value("${groupings.api.subject_attribute_name_uuid}")
    private String SUBJECT_ATTRIBUTE_NAME_UID;

    @Value("${groupings.api.operation_assign_attribute}")
    private String OPERATION_ASSIGN_ATTRIBUTE;

    @Value("${groupings.api.operation_remove_attribute}")
    private String OPERATION_REMOVE_ATTRIBUTE;

    @Value("${groupings.api.operation_replace_values}")
    private String OPERATION_REPLACE_VALUES;

    @Value("${groupings.api.privilege_opt_out}")
    private String PRIVILEGE_OPT_OUT;

    @Value("${groupings.api.privilege_opt_in}")
    private String PRIVILEGE_OPT_IN;

    @Value("${groupings.api.every_entity}")
    private String EVERY_ENTITY;

    @Value("${groupings.api.is_member}")
    private String IS_MEMBER;

    @Value("${groupings.api.success}")
    private String SUCCESS;

    @Value("${groupings.api.failure}")
    private String FAILURE;

    @Value("${groupings.api.success_allowed}")
    private String SUCCESS_ALLOWED;

    @Value("$groupings.api.stem}")
    private String STEM;

    final String username = "username";
    final String group = "group";

    @Autowired
    private GroupingsService groupingsService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkValues() {
        assertEquals(FAILURE, "FAILURE");
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
