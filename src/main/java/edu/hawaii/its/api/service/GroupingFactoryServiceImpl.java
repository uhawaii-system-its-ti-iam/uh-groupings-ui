package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.GroupingsServiceResult;
import edu.hawaii.its.api.type.Person;

import edu.internet2.middleware.grouperClient.ws.StemScope;
import edu.internet2.middleware.grouperClient.ws.beans.WsFindGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsStemLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service("groupingFactoryService")
public class GroupingFactoryServiceImpl implements GroupingFactoryService {

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

    @Value("${groupings.api.person_attributes.uuid}")
    private String UUID_KEY;

    @Value("${groupings.api.person_attributes.username}")
    private String UID_KEY;

    @Value("${groupings.api.person_attributes.first_name}")
    private String FIRST_NAME_KEY;

    @Value("${groupings.api.person_attributes.last_name}")
    private String LAST_NAME_KEY;

    @Value("${groupings.api.person_attributes.composite_name}")
    private String COMPOSITE_NAME_KEY;

    @Autowired
    private GrouperFactoryService grouperFactoryService;

    @Autowired
    private MemberAttributeService memberAttributeService;

    @Autowired
    private HelperService helperService;

    @Autowired
    private MembershipService membershipService;

    @Override
    //todo change basis to a String
    public List<GroupingsServiceResult> addGrouping(
            String adminUsername,
            String groupingPath,
            List<String> basis,
            List<String> include,
            List<String> exclude,
            List<String> owners) {

        List<GroupingsServiceResult> addGroupingResults = new ArrayList<>();
        String action = adminUsername + " is adding a Grouping: " + groupingPath;

        //make sure that adminUsername is actually an admin
        if (!memberAttributeService.isAdmin(adminUsername)) {
            GroupingsServiceResult gsr = helperService.makeGroupingsServiceResult(
                    FAILURE + ": " + adminUsername + " does not have permission to add this grouping", action);
            addGroupingResults.add(gsr);
            return addGroupingResults;
        }

        //make sure that there is not already a group there
        if (!pathIsEmpty(adminUsername, groupingPath)) {
            GroupingsServiceResult gsr = helperService.makeGroupingsServiceResult(
                    FAILURE + ": a group already exists at " + groupingPath, action);
            addGroupingResults.add(gsr);
            return addGroupingResults;
        }

        Map<String, List<String>> memberLists = new HashMap<>();
        memberLists.put("", new ArrayList<>());
        memberLists.put(BASIS_PLUS_INCLUDE, new ArrayList<>());
        memberLists.put(BASIS, new ArrayList<>());
        memberLists.put(INCLUDE, include);
        memberLists.put(EXCLUDE, exclude);
        memberLists.put(OWNERS, owners);

        // a stem the same as a folder
        //create main stem
        grouperFactoryService.makeWsStemSaveResults(adminUsername, groupingPath);

        //create basis stem
        grouperFactoryService.makeWsStemSaveResults(adminUsername, groupingPath + BASIS);

        for (Map.Entry<String, List<String>> entry : memberLists.entrySet()) {
            String groupPath = groupingPath + entry.getKey();

            //make the groups in grouper
            addGroupingResults.add(helperService.makeGroupingsServiceResult(
                    grouperFactoryService.addEmptyGroup(adminUsername, groupPath),
                    action));

            //add members to the groups
            addGroupingResults.addAll(membershipService.addGroupMembersByUsername(adminUsername,
                    groupPath, entry.getValue()));

            if(groupingPath.equals(groupPath)) {
                //todo create is-trio attribute
            }

            //todo this needs to be created not updated
            //update the last modified values of those groups
            addGroupingResults.add(membershipService.updateLastModified(groupPath));
        }

        WsSubjectLookup lookup = grouperFactoryService.makeWsSubjectLookup(adminUsername);
        WsStemLookup stemLookup = grouperFactoryService.makeWsStemLookup(STEM);
        String basisUid = getGroupId(groupingPath + BASIS);
        String includeUid = getGroupId(groupingPath + INCLUDE);
        String excludeUid = getGroupId(groupingPath + EXCLUDE);
        String basisPlusIncludeUid = getGroupId(groupingPath + BASIS_PLUS_INCLUDE);

        //add memberships for BASIS_PLUS_INCLUDE (basis group and include group)
        addGroupingResults.add(
                helperService.makeGroupingsServiceResult(
                        grouperFactoryService.makeWsAddMemberResultsGroup(groupingPath + BASIS_PLUS_INCLUDE, lookup, basisUid),
                        "add " + groupingPath + BASIS + " to " + groupingPath + BASIS_PLUS_INCLUDE));
        addGroupingResults.add(
                helperService.makeGroupingsServiceResult(
                        grouperFactoryService.makeWsAddMemberResultsGroup(groupingPath + BASIS_PLUS_INCLUDE, lookup, includeUid),
                        "add " + groupingPath + INCLUDE + " to " + groupingPath + BASIS_PLUS_INCLUDE));

        //add members for the composite (basisPlusInclude group complement exclude group)
        addGroupingResults.add(
                helperService.makeGroupingsServiceResult(
                        grouperFactoryService.makeWsAddMemberResultsGroup(groupingPath, lookup, basisPlusIncludeUid),
                        "add " + groupingPath + BASIS_PLUS_INCLUDE + " to " + groupingPath));
        //todo do a complement

        //add the isTrio attribute to the grouping
        grouperFactoryService.makeWsAssignAttributesResultsForGroup(
                lookup,
                ASSIGN_TYPE_GROUP,
                OPERATION_ASSIGN_ATTRIBUTE,
                TRIO,
                groupingPath
        );

        return addGroupingResults;
    }

    @Override public List<GroupingsServiceResult> deleteGrouping(String adminUsername, String groupingPath) {

        //Todo implement this once we have the ability to make groupings
        //we don't want to delete stuff that we can't bring back

        //        List<GroupingsServiceResult> deleteGroupingResults = new ArrayList<>();
        //        if (isAdmin(username)) {
        //            deleteGroupingResults.add(assignGroupAttributes(username, PURGE_GROUPING, OPERATION_ASSIGN_ATTRIBUTE, groupingPath));
        //            deleteGroupingResults.add(assignGroupAttributes(username, TRIO, OPERATION_REMOVE_ATTRIBUTE, groupingPath));
        //        } else if (isApp(username)) {
        //            deleteGroupingResults.add(assignGroupAttributes(PURGE_GROUPING, OPERATION_ASSIGN_ATTRIBUTE, groupingPath));
        //            deleteGroupingResults.add(assignGroupAttributes(TRIO, OPERATION_REMOVE_ATTRIBUTE, groupingPath));
        //        } else {
        //            GroupingsServiceResult failureResult = makeGroupingsServiceResult(FAILURE, "delete grouping" + groupingPath);
        //
        //            deleteGroupingResults.add(failureResult);
        //        }
        //        return deleteGroupingResults;
        throw new UnsupportedOperationException();
    }

    //set of elements in list0 or list1
    private List<String> union(List<String> list0, List<String> list1) {

        if (list0 == null) {
            return list1 != null ? list1 : new ArrayList<>();
        }

        //remove duplicates
        Set<String> treeSet = new TreeSet<>(list0);
        treeSet.addAll(list1);

        return new ArrayList<>(treeSet);
    }

    //set of elements in list0, but not in list1
    private List<String> complement(List<String> list0, List<String> list1) {
        if (list0 == null) {
            return new ArrayList<>();
        }

        if (list1 == null) {
            return list0;
        }

        list0.removeAll(list1);
        return list0;
    }

    //set of elements in both list0 and list1
    private List<String> intersection(List<String> list0, List<String> list1) {
        if (list0 == null || list1 == null) {
            return new ArrayList<>();
        }

        list0.retainAll(list1);
        return new ArrayList<>(list0);

    }

    //returns a group of Persons that have usernames from usernames
    //all other values will be left null
    private Group makeGroup(String groupPath, List<String> usernames) {
        List<Person> people = new ArrayList<>();

        for (String username : usernames) {
            people.add(new Person(null, null, username));
        }

        return new Group(groupPath, people);
    }

    //returns true if there is not a group at groupingPath
    private boolean pathIsEmpty(String adminUsername, String groupingPath) {

        WsFindGroupsResults wsFindGroupsResults = grouperFactoryService.makeWsFindGroupsResults(groupingPath);

        return wsFindGroupsResults.getGroupResults() == null;
    }

    //returns the uid for a group in grouper
    private String getGroupId(String groupPath) {
        WsFindGroupsResults results = grouperFactoryService.makeWsFindGroupsResults(groupPath);
        WsGroup result = results.getGroupResults()[0];
        return result.getUuid();
    }
}
