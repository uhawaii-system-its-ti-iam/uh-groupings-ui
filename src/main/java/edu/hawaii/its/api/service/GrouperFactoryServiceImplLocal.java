package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.Membership;
import edu.hawaii.its.api.type.Person;

import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("grouperFactoryService")
@Profile(value = {"default", "dev", "localTest"})
public class GrouperFactoryServiceImplLocal implements GrouperFactoryService {

    @Value("${groupings.api.settings}")
    private String SETTINGS;

    @Value("${groupings.api.grouping_admins}")
    private String GROUPING_ADMINS;

    @Value("${groupings.api.grouping_apps}")
    private String GROUPING_APPS;

    @Value("${groupings.api.grouping_owners}")
    private String GROUPING_OWNERS;

    @Value("${groupings.api.grouping_superusers}")
    private String GROUPING_SUPERUSERS;

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

    @Value("${groupings.api.test.username}")
    private String USERNAME;

    @Value("${groupings.api.test.name}")
    private String NAME;

    @Value("${groupings.api.test.uuid}")
    private String UUID;

    @Autowired
    private GroupingRepository groupingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PersonRepository personRepository;

    public GrouperFactoryServiceImplLocal() {

    }

    @Override
    public WsGroupSaveResults addEmptyGroup(String username, String path) {

        Group newGroup = new Group(path);
        groupRepository.save(newGroup);

        WsGroupSaveResults wsGroupSaveResults = new WsGroupSaveResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsGroupSaveResults.setResultMetadata(wsResultMeta);

        return wsGroupSaveResults;
    }

    /**
     * @param username: username of user to be looked up
     * @return a WsSubjectLookup with username as the subject identifier
     */
    @Override
    public WsSubjectLookup makeWsSubjectLookup(String username) {
        WsSubjectLookup wsSubjectLookup = new WsSubjectLookup();
        wsSubjectLookup.setSubjectIdentifier(username);

        return wsSubjectLookup;
    }

    /**
     * @param group: group to be looked up
     * @return a WsGroupLookup with group as the group name
     */
    @Override
    public WsGroupLookup makeWsGroupLookup(String group) {
        WsGroupLookup groupLookup = new WsGroupLookup();
        groupLookup.setGroupName(group);

        return groupLookup;
    }

    @Override
    public WsStemLookup makeWsStemLookup(String stemName, String stemUuid) {
        return new WsStemLookup(stemName, stemUuid);
    }

    @Override
    public WsStemSaveResults makeWsStemSaveResults(String username, String stemPath) {
        WsStemSaveResults wsStemSaveResults = new WsStemSaveResults();
        WsStemSaveResult wsStemSaveResult = new WsStemSaveResult();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsStemSaveResult.setResultMetadata(wsResultMeta);
        wsStemSaveResults.setResultMetadata(wsResultMeta);
        wsStemSaveResults.setResults(new WsStemSaveResult[]{wsStemSaveResult});

        return wsStemSaveResults;
    }

    @Override
    public WsAttributeAssignValue makeWsAttributeAssignValue(String time) {
        WsAttributeAssignValue dateTimeValue = new WsAttributeAssignValue();
        dateTimeValue.setValueSystem(time);

        return dateTimeValue;
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, String newMember) {

        return makeWsAddMemberResults(group, newMember);
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, WsSubjectLookup lookup, List<String> newMembers) {
        WsAddMemberResults wsAddMemberResults = new WsAddMemberResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);

        for (String username : newMembers) {
            WsResultMeta wsResultMetaData = makeWsAddMemberResults(group, lookup, username).getResultMetadata();
            if (wsResultMetaData.getResultCode().equals(FAILURE)) {
                wsResultMeta = wsResultMetaData;
            }
        }

        wsAddMemberResults.setResultMetadata(wsResultMeta);

        return wsAddMemberResults;
    }

    @Override
    public WsAddMemberResults makeWsAddMemberResults(String group, String newMember) {
        WsAddMemberResults wsAddMemberResults = new WsAddMemberResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsAddMemberResults.setResultMetadata(wsResultMeta);

        Grouping grouping = groupingRepository.findByIncludePathOrExcludePathOrCompositePathOrOwnersPath(group, group, group, group);
        Person newGroupMember = personRepository.findByUsername(newMember);

        if (grouping == null) {
            Group groupToAddTo = groupRepository.findByPath(group);
            addMember(groupToAddTo, newGroupMember);
        } else {

            boolean inBasis = grouping.getBasis().getMembers().contains(newGroupMember);
            boolean inExclude = grouping.getExclude().getMembers().contains(newGroupMember);
            boolean inInclude = grouping.getInclude().getMembers().contains(newGroupMember);

            if (group.endsWith(OWNERS)) {
                addMember(grouping.getOwners(), newGroupMember);
            } else if (group.endsWith(EXCLUDE)) {
                if (inBasis) {
                    addMember(grouping.getExclude(), newGroupMember);
                } else if (inInclude) {
                    deleteMember(grouping.getInclude(), newGroupMember);
                }
            } else if (group.endsWith(INCLUDE)) {
                if (inExclude) {
                    deleteMember(grouping.getExclude(), newGroupMember);
                } else if (!inBasis) {
                    addMember(grouping.getInclude(), newGroupMember);
                }
            }

            grouping = groupingRepository.findByPath(grouping.getPath());
            Group composite = buildComposite(
                    grouping.getInclude(),
                    grouping.getExclude(),
                    grouping.getBasis(),
                    grouping.getPath());

            groupRepository.save(composite);

        }
        return wsAddMemberResults;
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, String memberToDelete) {
        WsDeleteMemberResults wsDeleteMemberResults = new WsDeleteMemberResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsDeleteMemberResults.setResultMetadata(wsResultMeta);

        Grouping grouping = groupingRepository.findByIncludePathOrExcludePathOrCompositePathOrOwnersPath(group, group, group, group);
        Person personToDelete = personRepository.findByUsername(memberToDelete);

        if (grouping == null) {
            Group groupToDeleteFrom = groupRepository.findByPath(group);
            deleteMember(groupToDeleteFrom, personToDelete);
        } else {

            if (group.endsWith(OWNERS)) {
                deleteMember(grouping.getOwners(), personToDelete);
            } else if (group.endsWith(EXCLUDE)) {
                deleteMember(grouping.getExclude(), personToDelete);

            } else if (group.endsWith(INCLUDE)) {
                deleteMember(grouping.getInclude(), personToDelete);
            }

            grouping = groupingRepository.findByPath(grouping.getPath());
            grouping = groupingRepository.findByPath(grouping.getPath());
            Group composite = buildComposite(
                    grouping.getInclude(),
                    grouping.getExclude(),
                    grouping.getBasis(),
                    grouping.getPath());

            groupRepository.save(composite);
        }
        return wsDeleteMemberResults;
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, String memberToDelete) {

        return makeWsDeleteMemberResults(group, memberToDelete);
    }

    @Override
    public WsDeleteMemberResults makeWsDeleteMemberResults(String group, WsSubjectLookup lookup, List<String> membersToDelete) {
        WsDeleteMemberResults wsDeleteMemberResults = new WsDeleteMemberResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);

        for (String username : membersToDelete) {
            WsResultMeta wsResultMetaData = makeWsDeleteMemberResults(group, lookup, username).getResultMetadata();
            if (wsResultMetaData.getResultCode().equals(FAILURE)) {
                wsResultMeta = wsResultMetaData;
            }
        }

        wsDeleteMemberResults.setResultMetadata(wsResultMeta);

        return wsDeleteMemberResults;
    }


    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
                                                                                     String attributeDefNameName) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsGetAttributeAssignmentsResults.setResultMetadata(wsResultMeta);

        Iterable<Group> groups = groupRepository.findAll();
        List<WsGroup> groupList = new ArrayList<>();
        List<WsAttributeAssign> attributeAssignList = new ArrayList<>();

        for (Group group : groups) {
            //todo check to see if it returns null when there is no match
            if (groupingRepository.findByPath(group.getPath()) != null) {
                WsGroup g = new WsGroup();
                WsAttributeAssign attributeAssign = new WsAttributeAssign();

                g.setName(group.getPath());
                attributeAssign.setOwnerGroupName(group.getPath());
                attributeAssign.setAttributeDefNameName(attributeDefNameName);

                attributeAssignList.add(attributeAssign);
                groupList.add(g);
            }
        }

        WsGroup[] wsGroups = groupList.toArray(new WsGroup[groupList.size()]);
        WsAttributeAssign[] wsAttributeAssigns = attributeAssignList.toArray(new WsAttributeAssign[attributeAssignList.size()]);

        wsGetAttributeAssignmentsResults.setWsGroups(wsGroups);
        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(wsAttributeAssigns);

        return wsGetAttributeAssignmentsResults;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
                                                                                     String attributeDefNameName0,
                                                                                     String attributeDefNameName1) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = makeWsGetAttributeAssignmentsResultsTrio(assignType, attributeDefNameName0);

        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();
        attributeAssigns.addAll(Arrays.asList(wsGetAttributeAssignmentsResults.getWsAttributeAssigns()));
        attributeAssigns.addAll(attributeAssignsOptIn());
        attributeAssigns.addAll(attributeAssignsOptOut());

        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(attributeAssigns.toArray(new WsAttributeAssign[attributeAssigns.size()]));

        if (attributeDefNameName1.equals(OPT_IN)) {
            wsGetAttributeAssignmentsResults = removeGroupsWithoutOptIn(wsGetAttributeAssignmentsResults);
        } else if (attributeDefNameName1.equals(OPT_IN)) {
            wsGetAttributeAssignmentsResults = removeGroupsWithoutOptOut(wsGetAttributeAssignmentsResults);
        }

        return wsGetAttributeAssignmentsResults;
    }

    private List<WsAttributeAssign> attributeAssignsOptIn() {
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        Iterable<Grouping> groupings = groupingRepository.findAll();

        for (Grouping grouping : groupings) {
            if (grouping.isOptInOn()) {
                WsAttributeAssign attributeAssign = new WsAttributeAssign();
                attributeAssign.setAttributeDefNameName(OPT_IN);
                attributeAssign.setOwnerGroupName(grouping.getPath());

                attributeAssigns.add(attributeAssign);
            }
        }

        return attributeAssigns;
    }

    private List<WsAttributeAssign> attributeAssignsOptOut() {
        List<WsAttributeAssign> attributeAssigns = new ArrayList<>();

        Iterable<Grouping> groupings = groupingRepository.findAll();

        for (Grouping grouping : groupings) {
            if (grouping.isOptOutOn()) {
                WsAttributeAssign attributeAssign = new WsAttributeAssign();
                attributeAssign.setAttributeDefNameName(OPT_OUT);
                attributeAssign.setOwnerGroupName(grouping.getPath());

                attributeAssigns.add(attributeAssign);
            }
        }

        return attributeAssigns;

    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
                                                                                     String attributeDefNameName,
                                                                                     List<String> ownerGroupNames) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = makeWsGetAttributeAssignmentsResultsTrio(
                assignType,
                attributeDefNameName);

        wsGetAttributeAssignmentsResults = removeGroupsNotInList(wsGetAttributeAssignmentsResults, ownerGroupNames);

        return wsGetAttributeAssignmentsResults;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsTrio(String assignType,
                                                                                     String attributeDefNameName0,
                                                                                     String attributeDefNameName1,
                                                                                     List<String> ownerGroupNames) {

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = makeWsGetAttributeAssignmentsResultsTrio(
                assignType,
                attributeDefNameName0,
                attributeDefNameName1);

        wsGetAttributeAssignmentsResults = removeGroupsNotInList(wsGetAttributeAssignmentsResults, ownerGroupNames);

        return wsGetAttributeAssignmentsResults;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForMembership(String assignType,
                                                                                              String attributeDefNameName,
                                                                                              String membershipId) {
        Membership membership = membershipRepository.findById(membershipId);

        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();

        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);

        WsAttributeAssign[] wsAttributeAssigns = new WsAttributeAssign[1];
        WsAttributeAssign wsAttributeAssign = new WsAttributeAssign();
        if (membership.getSelfOpted()) {
            wsAttributeAssign.setAttributeDefNameName(SELF_OPTED);
        }

        wsAttributeAssigns[0] = wsAttributeAssign;
        wsGetAttributeAssignmentsResults.setResultMetadata(wsResultMeta);
        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(wsAttributeAssigns);

        return wsGetAttributeAssignmentsResults;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
                                                                                         String group) {
        WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults = new WsGetAttributeAssignmentsResults();

        Grouping grouping = groupingRepository.findByPath(group);
        if (grouping.isListservOn()) {
            wsGetAttributeAssignmentsResults = addAssignmentResults(wsGetAttributeAssignmentsResults, LISTSERV);
        }
        if (grouping.isOptInOn()) {
            wsGetAttributeAssignmentsResults = addAssignmentResults(wsGetAttributeAssignmentsResults, OPT_IN);
        }
        if (grouping.isOptOutOn()) {
            wsGetAttributeAssignmentsResults = addAssignmentResults(wsGetAttributeAssignmentsResults, OPT_OUT);
        }

        return wsGetAttributeAssignmentsResults;
    }

    @Override
    public WsGetAttributeAssignmentsResults makeWsGetAttributeAssignmentsResultsForGroup(String assignType,
                                                                                         String attributeDefNameName,
                                                                                         String group) {
        return makeWsGetAttributeAssignmentsResultsForGroup(assignType, group);
    }

    @Override
    public WsHasMemberResults makeWsHasMemberResults(String group, String username) {
        WsHasMemberResults wsHasMemberResults = new WsHasMemberResults();
        WsHasMemberResult wsHasMemberResult = new WsHasMemberResult();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsHasMemberResult.setResultMetadata(wsResultMeta);
        wsHasMemberResults.setResults(new WsHasMemberResult[]{wsHasMemberResult});

        Group groupToCheck = groupRepository.findByPath(group);
        Person person = personRepository.findByUsername(username);

        if (groupToCheck.getMembers().contains(person)) {
            wsResultMeta.setResultCode(IS_MEMBER);
        } else {
            wsResultMeta.setResultCode("not member");
        }

        return wsHasMemberResults;
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResults(String attributeAssignType,
                                                                   String attributeAssignOperation,
                                                                   String ownerGroupName,
                                                                   String attributeDefNameName,
                                                                   String attributeAssignValueOperation,
                                                                   WsAttributeAssignValue value) {

        WsAssignAttributesResults wsAssignAttributesResults = new WsAssignAttributesResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsAssignAttributesResults.setResultMetadata(wsResultMeta);

        Grouping grouping = groupingRepository.findByPath(ownerGroupName);

        Boolean onOrrOff = null;

        if (attributeAssignOperation.equals(OPERATION_ASSIGN_ATTRIBUTE)) {
            onOrrOff = true;
        } else if (attributeAssignOperation.equals(OPERATION_REMOVE_ATTRIBUTE)) {
            onOrrOff = false;
        }

        if (onOrrOff != null) {
            if (attributeDefNameName.equals(LISTSERV))

            {
                grouping.setListservOn(onOrrOff);

            } else if (attributeDefNameName.equals(OPT_IN))

            {
                grouping.setOptInOn(onOrrOff);

            } else if (attributeDefNameName.equals(OPT_OUT))

            {
                grouping.setOptOutOn(onOrrOff);
            }
        }

        return wsAssignAttributesResults;
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForMembership(String attributeAssignType,
                                                                                String attributeAssignOperation,
                                                                                String attributeDefNameName,
                                                                                String ownerMembershipId) {

        WsAssignAttributesResults wsAssignAttributesResults = new WsAssignAttributesResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);
        wsAssignAttributesResults.setResultMetadata(wsResultMeta);

        Membership membership = membershipRepository.findById(ownerMembershipId);

        if (attributeAssignOperation.equals(OPERATION_ASSIGN_ATTRIBUTE)) {
            membership.setSelfOpted(true);
        } else if (attributeAssignOperation.equals(OPERATION_REMOVE_ATTRIBUTE)) {
            membership.setSelfOpted(false);
        }

        membershipRepository.save(membership);

        return wsAssignAttributesResults;
    }


    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(String attributeAssingType,
                                                                           String attributeAssignOperation,
                                                                           String attributeDefNameName,
                                                                           String ownerGroupName) {
        WsAssignAttributesResults wsAssignAttributesResults = new WsAssignAttributesResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(FAILURE);

        Grouping grouping = groupingRepository.findByPath(ownerGroupName);

        if (attributeAssignOperation.equals(OPERATION_ASSIGN_ATTRIBUTE)) {
            if (setGroupingAttribute(grouping, attributeDefNameName, true)) {
                wsResultMeta.setResultCode(SUCCESS);
            }
        } else if (attributeAssignOperation.equals(OPERATION_REMOVE_ATTRIBUTE)) {
            if (setGroupingAttribute(grouping, attributeDefNameName, false)) {
                wsResultMeta.setResultCode(SUCCESS);
            }
        }

        wsAssignAttributesResults.setResultMetadata(wsResultMeta);
        return wsAssignAttributesResults;
    }

    @Override
    public WsAssignAttributesResults makeWsAssignAttributesResultsForGroup(WsSubjectLookup lookup,
                                                                           String attributeAssingType,
                                                                           String attributeAssignOperation,
                                                                           String attributeDefNameName,
                                                                           String ownerGroupName) {
        WsAssignAttributesResults wsAssignAttributesResults;

        Grouping grouping = groupingRepository.findByPath(ownerGroupName);
        Person person = personRepository.findByUsername(lookup.getSubjectIdentifier());

        if (grouping.getOwners().getMembers().contains(person)) {
            wsAssignAttributesResults = makeWsAssignAttributesResultsForGroup(attributeAssingType,
                    attributeAssignOperation,
                    attributeDefNameName,
                    ownerGroupName);
        } else {
            wsAssignAttributesResults = new WsAssignAttributesResults();
            WsResultMeta wsResultMeta = new WsResultMeta();
            wsResultMeta.setResultCode(FAILURE);
            wsAssignAttributesResults.setResultMetadata(wsResultMeta);
        }
        return wsAssignAttributesResults;
    }

    @Override
    public WsAssignGrouperPrivilegesLiteResult makeWsAssignGrouperPrivilegesLiteResult(String groupName,
                                                                                       String privilegeName,
                                                                                       WsSubjectLookup lookup,
                                                                                       boolean allowed) {

        WsAssignGrouperPrivilegesLiteResult wsAssignGrouperPrivilegsLiteResult = new WsAssignGrouperPrivilegesLiteResult();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(SUCCESS);

        Person person = personRepository.findByUsername(lookup.getSubjectIdentifier());
        Group group = groupRepository.findByPath(groupName);

        Membership membership = membershipRepository.findByPersonAndGroup(person, group);

        if (privilegeName.equals(PRIVILEGE_OPT_IN)) {

            membership.setOptInEnabled(allowed);
        } else if (privilegeName.equals(PRIVILEGE_OPT_OUT)) {

            membership.setOptOutEnabled(allowed);
        } else {
            throw new IllegalArgumentException("Privilege Name not acceptable");
        }

        wsAssignGrouperPrivilegsLiteResult.setResultMetadata(wsResultMeta);
        return wsAssignGrouperPrivilegsLiteResult;
    }

    @Override
    public WsGetGrouperPrivilegesLiteResult makeWsGetGrouperPrivilegesLiteResult(String groupName,
                                                                                 String privilegeName,
                                                                                 WsSubjectLookup lookup) {

        WsGetGrouperPrivilegesLiteResult wsGetGrouperPrivilegesLiteResult = new WsGetGrouperPrivilegesLiteResult();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(FAILURE);

        Person person = personRepository.findByUsername(EVERY_ENTITY);
        Group group = groupRepository.findByPath(groupName);
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);

        boolean enabled = false;

        if (privilegeName.equals(PRIVILEGE_OPT_IN)) {

            if (membership.isOptInEnabled()) {
                enabled = true;
            }
        } else if (privilegeName.equals(PRIVILEGE_OPT_OUT)) {

            if (membership.isOptOutEnabled()) {
                enabled = true;
            }
        } else {
            throw new IllegalArgumentException("Privilege Name not acceptable");
        }

        if (enabled) {
            wsResultMeta.setResultCode(SUCCESS_ALLOWED);
        }

        wsGetGrouperPrivilegesLiteResult.setResultMetadata(wsResultMeta);

        return wsGetGrouperPrivilegesLiteResult;
    }

    @Override
    public WsGetMembershipsResults makeWsGetMembershipsResults(String groupName,
                                                               WsSubjectLookup lookup) {

        Person person = personRepository.findByUsername(lookup.getSubjectIdentifier());
        Group group = groupRepository.findByPath(groupName);
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);

        WsGetMembershipsResults wsGetMembershipsResults = new WsGetMembershipsResults();
        WsResultMeta wsResultMeta = new WsResultMeta();
        wsResultMeta.setResultCode(FAILURE);
        WsMembership[] wsMemberships = new WsMembership[1];
        WsMembership wsMembership = new WsMembership();

        if (membership != null) {
            wsMembership.setMembershipId(membership.getId());
            wsResultMeta.setResultCode(SUCCESS);
        }

        wsMemberships[0] = wsMembership;
        wsGetMembershipsResults.setWsMemberships(wsMemberships);

        return wsGetMembershipsResults;
    }

    @Override
    public WsGetMembersResults makeWsGetMembersResults(String subjectAttributeName,
                                                       WsSubjectLookup lookup,
                                                       String groupName) {

        WsGetMembersResults wsGetMembersResults = new WsGetMembersResults();
        WsGetMembersResult wsGetMembersResult = new WsGetMembersResult();
        WsSubject[] subjects;

        Group group = groupRepository.findByPath(groupName);
        List<Person> members = group.getMembers();
        List<WsSubject> subjectList = new ArrayList<>();

        for (Person person : members) {
            WsSubject subject = new WsSubject();
            subject.setId(person.getUuid());
            subject.setName(person.getName());
            subject.setAttributeValues(new String[]{person.getUsername()});

            subjectList.add(subject);
        }

        subjects = subjectList.toArray(new WsSubject[subjectList.size()]);

        wsGetMembersResult.setWsSubjects(subjects);
        wsGetMembersResults.setResults(new WsGetMembersResult[]{wsGetMembersResult});

        return wsGetMembersResults;
    }

    @Override
    public WsGetGroupsResults makeWsGetGroupsResults(String username,
                                                     WsStemLookup stemLookup,
                                                     StemScope stemScope) {

        WsGetGroupsResults wsGetGroupsResults = new WsGetGroupsResults();
        WsGetGroupsResult wsGetGroupsResult = new WsGetGroupsResult();
        WsGroup[] groups;

        List<WsGroup> wsGroupList = new ArrayList<>();
        List<Group> groupList = groupRepository.findByMembersUsername(username);

        for (Group group : groupList) {
            WsGroup g = new WsGroup();
            g.setName(group.getPath());
            wsGroupList.add(g);
        }

        groups = wsGroupList.toArray(new WsGroup[wsGroupList.size()]);

        wsGetGroupsResult.setWsGroups(groups);
        wsGetGroupsResults.setResults(new WsGetGroupsResult[]{wsGetGroupsResult});

        return wsGetGroupsResults;
    }

    @Override
    public WsAttributeAssign[] makeEmptyWsAttributeAssignArray() {
        return new WsAttributeAssign[0];
    }


    ////////////////////////////////////////////////////////////////////////////////
    // Helper methods
    ////////////////////////////////////////////////////////////////////////////////

    private Group buildComposite(Group include, Group exclude, Group basis, String path) {
        Group basisMinusExclude = removeExcludedMembers(basis, exclude);
        Group compositeGroup = addIncludedMembers(include, basisMinusExclude);
        compositeGroup.setPath(path);

        return compositeGroup;
    }

    private Group addIncludedMembers(Group include, Group basis) {
        Group unionGroup = new Group();
        List<Person> unionList = new ArrayList<>();
        unionList.addAll(include.getMembers());
        unionList.addAll(basis.getMembers());

        //remove duplicates
        Set<Person> s = new TreeSet<>();
        s.addAll(unionList);
        unionGroup.setMembers(Arrays.asList(s.toArray(new Person[s.size()])));

        return unionGroup;
    }

    private Group removeExcludedMembers(Group basisPlusInclude, Group exclude) {
        Group basisPlusIncludeMinusExcludeGroup = new Group();
        ArrayList<Person> newBasisPlusInclude = new ArrayList<>();
        newBasisPlusInclude.addAll(basisPlusInclude.getMembers());

        newBasisPlusInclude.removeAll(exclude.getMembers());
        basisPlusIncludeMinusExcludeGroup.setMembers(newBasisPlusInclude);

        return basisPlusIncludeMinusExcludeGroup;
    }

    private boolean setGroupingAttribute(Grouping grouping, String attributeName, boolean on) {
        if (attributeName.equals(LISTSERV)) {
            grouping.setListservOn(on);
        } else if (attributeName.equals(OPT_IN)) {
            grouping.setOptInOn(on);
        } else if (attributeName.equals(OPT_OUT)) {
            grouping.setOptOutOn(on);
        } else {
            return false;
        }
        groupingRepository.save(grouping);
        return true;

    }

    private void addMember(Group group, Person person) {
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);

        if (membership == null) {
            group.getMembers().add(person);
            membership = new Membership(person, group);

            groupRepository.save(group);
            membershipRepository.save(membership);
        }
    }

    private void deleteMember(Group group, Person person) {
        Membership membership = membershipRepository.findByPersonAndGroup(person, group);
        if (membership != null) {
            group.getMembers().remove(person);

            groupRepository.save(group);
            membershipRepository.delete(membership);
        }
    }

    private WsGetAttributeAssignmentsResults addAssignmentResults(
            WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults,
            String attributeName) {

        List<WsAttributeDefName> wsAttributeDefNames = new ArrayList<>();
        List<WsAttributeAssign> wsAttributeAssigns = new ArrayList<>();

        if (wsGetAttributeAssignmentsResults.getWsAttributeAssigns() != null) {
            Collections.addAll(wsAttributeAssigns, wsGetAttributeAssignmentsResults.getWsAttributeAssigns());
        }

        if (wsGetAttributeAssignmentsResults.getWsAttributeDefNames() != null) {
            Collections.addAll(wsAttributeDefNames, wsGetAttributeAssignmentsResults.getWsAttributeDefNames());
        }

        WsAttributeAssign wsAttributeAssign = new WsAttributeAssign();
        WsAttributeDefName wsAttributeDefName = new WsAttributeDefName();

        wsAttributeDefName.setName(attributeName);
        wsAttributeAssign.setAttributeDefNameName(attributeName);

        wsAttributeAssigns.add(wsAttributeAssign);
        wsAttributeDefNames.add(wsAttributeDefName);

        wsGetAttributeAssignmentsResults.setWsAttributeDefNames(wsAttributeDefNames.toArray(new WsAttributeDefName[wsAttributeDefNames.size()]));
        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(wsAttributeAssigns.toArray(new WsAttributeAssign[wsAttributeAssigns.size()]));

        return wsGetAttributeAssignmentsResults;

    }

    private WsGetAttributeAssignmentsResults removeGroupsWithoutOptIn(WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults) {

        List<WsGroup> wsGroupList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsGroups());
        List<WsGroup> wsGroupsWithOptIn = new ArrayList<>();
        List<WsAttributeAssign> wsAttributeAssignList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsAttributeAssigns());
        List<WsAttributeAssign> wsAttributeAssignsWithOptIn = new ArrayList<>();

        for (WsGroup wsGroup : wsGroupList) {
            Grouping grouping = groupingRepository.findByPath(wsGroup.getName());

            if (grouping.isOptInOn()) {
                wsGroupsWithOptIn.add(wsGroup);
            }
        }

        for (WsAttributeAssign wsAttributeAssign : wsAttributeAssignList) {
            Grouping grouping = groupingRepository.findByPath(wsAttributeAssign.getOwnerGroupName());

            if (grouping.isOptInOn()) {
                wsAttributeAssignsWithOptIn.add(wsAttributeAssign);
            }
        }

        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(wsAttributeAssignsWithOptIn.toArray(new WsAttributeAssign[wsAttributeAssignsWithOptIn.size()]));
        wsGetAttributeAssignmentsResults.setWsGroups(wsGroupsWithOptIn.toArray(new WsGroup[wsGroupsWithOptIn.size()]));

        return wsGetAttributeAssignmentsResults;
    }

    private WsGetAttributeAssignmentsResults removeGroupsWithoutOptOut(WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults) {

        List<WsGroup> wsGroupList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsGroups());
        List<WsAttributeAssign> wsAttributeAssignList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsAttributeAssigns());

        for (WsGroup wsGroup : wsGroupList) {
            Grouping grouping = groupingRepository.findByPath(wsGroup.getName());

            if (!grouping.isOptOutOn()) {
                wsGroupList.remove(wsGroup);
            }
        }

        for (WsAttributeAssign wsAttributeAssign : wsAttributeAssignList) {
            Grouping grouping = groupingRepository.findByPath(wsAttributeAssign.getOwnerGroupName());

            if (!grouping.isOptOutOn()) {
                wsAttributeAssignList.remove(wsAttributeAssign);
            }
        }

        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(wsAttributeAssignList.toArray(new WsAttributeAssign[wsAttributeAssignList.size()]));
        wsGetAttributeAssignmentsResults.setWsGroups(wsGroupList.toArray(new WsGroup[wsGroupList.size()]));

        return wsGetAttributeAssignmentsResults;

    }

    private WsGetAttributeAssignmentsResults removeGroupsNotInList(
            WsGetAttributeAssignmentsResults wsGetAttributeAssignmentsResults,
            List<String> ownerGroupNames) {

        List<WsGroup> wsGroupList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsGroups());
        List<WsGroup> groupList = new ArrayList<>();
        List<WsAttributeAssign> wsAttributeAssignList = Arrays.asList(wsGetAttributeAssignmentsResults.getWsAttributeAssigns());
        List<WsAttributeAssign> attributeAssignList = new ArrayList<>();

        groupList.addAll(wsGroupList
                .stream()
                .filter(group -> ownerGroupNames.contains(group.getName()))
                .collect(Collectors.toList()));
        attributeAssignList.addAll(wsAttributeAssignList
                .stream()
                .filter(attributeAssign -> ownerGroupNames.contains(attributeAssign.getOwnerGroupName()))
                .collect(Collectors.toList()));

        wsGetAttributeAssignmentsResults.setWsAttributeAssigns(attributeAssignList.toArray(new WsAttributeAssign[attributeAssignList.size()]));
        wsGetAttributeAssignmentsResults.setWsGroups(groupList.toArray(new WsGroup[groupList.size()]));

        return wsGetAttributeAssignmentsResults;
    }

}
