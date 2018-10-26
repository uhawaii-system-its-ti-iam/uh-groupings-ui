package edu.hawaii.its.api.service;

import java.util.*;

import org.springframework.test.context.ActiveProfiles;

import edu.hawaii.its.api.repository.GroupRepository;
import edu.hawaii.its.api.repository.GroupingRepository;
import edu.hawaii.its.api.repository.MembershipRepository;
import edu.hawaii.its.api.repository.PersonRepository;
import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.Membership;
import edu.hawaii.its.api.type.Person;

@ActiveProfiles("localTest")
class DatabaseSetup {

    private String pathRoot = "path:to:grouping";

    private PersonRepository personRepository;
    private GroupRepository groupRepository;
    private GroupingRepository groupingRepository;
    private MembershipRepository membershipRepository;

    private List<Person> persons = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<Grouping> groupings = new ArrayList<>();

    // Constructor.
    DatabaseSetup(PersonRepository personRepository,
                  GroupRepository groupRepository,
                  GroupingRepository groupingRepository,
                  MembershipRepository membershipRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;
        this.groupingRepository = groupingRepository;
        this.membershipRepository = membershipRepository;

        fillDatabase();
    }

    private void fillDatabase() {
        fillPersonRepository();
        fillGroupRepository();
        fillGroupingRepository();

        setUpMemberships();
    }

    private void fillPersonRepository() {
        setUpPersons();

        personRepository.save(persons);
    }

    private void fillGroupRepository() {
        setUpGroups();

        groupRepository.save(groups);
    }

    private void fillGroupingRepository() {
        setUpGroupings();

        groupingRepository.save(groupings);
    }

    /////////////////////////////////////////////////////
    // setup methods
    /////////////////////////////////////////////////////

    private void setUpPersons() {
        int numberOfPersons = 100;
        for (int i = 0; i < numberOfPersons; i++) {
            makePerson("name" + i, "uuid" + i, "username" + i);
        }
    }

    private void setUpGroups() {
        setUpGroup0();
        setUpGroup1();
        setUpGroup2();
        setUpGroup3();
        setUpGroup4();
    }

    private void setUpGroup(int i,
                            List<Person> basisMembers,
                            List<Person> excludeMembers,
                            List<Person> includeMembers,
                            List<Person> ownerMembers) {

        //todo put strings in a config file
        String BASIS = ":basis";
        makeGroup(basisMembers, pathRoot + i + BASIS);
        String EXCLUDE = ":exclude";
        makeGroup(excludeMembers, pathRoot + i + EXCLUDE);
        String INCLUDE = ":include";
        makeGroup(includeMembers, pathRoot + i + INCLUDE);
        String OWNERS = ":owners";
        makeGroup(ownerMembers, pathRoot + i + OWNERS);

    }

    private void setUpGroup0() {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();

        basisMembers.add(persons.get(0));
        basisMembers.add(persons.get(1));
        basisMembers.add(persons.get(2));
        basisMembers.add(persons.get(3));
        basisMembers.add(persons.get(4));

        excludeMembers.add(persons.get(2));
        excludeMembers.add(persons.get(3));
        excludeMembers.add(persons.get(4));

        includeMembers.add(persons.get(5));
        includeMembers.add(persons.get(6));
        includeMembers.add(persons.get(7));
        includeMembers.add(persons.get(8));
        includeMembers.add(persons.get(9));

        ownerMembers.add(persons.get(0));

        setUpGroup(0, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup1() {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();

        basisMembers.add(persons.get(0));
        basisMembers.add(persons.get(1));
        basisMembers.add(persons.get(2));
        basisMembers.add(persons.get(3));
        basisMembers.add(persons.get(4));

        excludeMembers.add(persons.get(2));
        excludeMembers.add(persons.get(3));
        excludeMembers.add(persons.get(4));

        includeMembers.add(persons.get(5));
        includeMembers.add(persons.get(6));
        includeMembers.add(persons.get(7));
        includeMembers.add(persons.get(8));
        includeMembers.add(persons.get(9));

        ownerMembers.add(persons.get(0));

        setUpGroup(1, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup2() {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();

        basisMembers.add(persons.get(0));
        basisMembers.add(persons.get(1));
        basisMembers.add(persons.get(2));
        basisMembers.add(persons.get(3));
        basisMembers.add(persons.get(4));

        excludeMembers.add(persons.get(2));
        excludeMembers.add(persons.get(3));
        excludeMembers.add(persons.get(4));

        includeMembers.add(persons.get(5));
        includeMembers.add(persons.get(6));
        includeMembers.add(persons.get(7));
        includeMembers.add(persons.get(8));
        includeMembers.add(persons.get(9));

        ownerMembers.add(persons.get(0));

        setUpGroup(2, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup3() {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();

        basisMembers.add(persons.get(0));
        basisMembers.add(persons.get(1));
        basisMembers.add(persons.get(2));
        basisMembers.add(persons.get(3));
        basisMembers.add(persons.get(4));

        excludeMembers.add(persons.get(2));
        excludeMembers.add(persons.get(3));
        excludeMembers.add(persons.get(4));

        includeMembers.add(persons.get(5));
        includeMembers.add(persons.get(6));
        includeMembers.add(persons.get(7));
        includeMembers.add(persons.get(8));
        includeMembers.add(persons.get(9));

        ownerMembers.add(persons.get(0));

        setUpGroup(3, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup4() {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();

        basisMembers.add(persons.get(0));
        basisMembers.add(persons.get(1));
        basisMembers.add(persons.get(2));
        basisMembers.add(persons.get(3));
        basisMembers.add(persons.get(4));

        excludeMembers.add(persons.get(2));
        excludeMembers.add(persons.get(3));
        excludeMembers.add(persons.get(4));

        includeMembers.add(persons.get(5));
        includeMembers.add(persons.get(6));
        includeMembers.add(persons.get(7));
        includeMembers.add(persons.get(8));
        includeMembers.add(persons.get(9));

        ownerMembers.add(persons.get(0));

        setUpGroup(4, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroupings() {
        makeGrouping(pathRoot + 0, groups.get(0), groups.get(1), groups.get(2), groups.get(3), false, true, false);
        makeGrouping(pathRoot + 1, groups.get(4), groups.get(5), groups.get(6), groups.get(7), false, true, true);
        makeGrouping(pathRoot + 2, groups.get(8), groups.get(9), groups.get(10), groups.get(11), true, false, false);
        makeGrouping(pathRoot + 3, groups.get(12), groups.get(13), groups.get(14), groups.get(15), true, true, true);
        makeGrouping(pathRoot + 4, groups.get(16), groups.get(17), groups.get(18), groups.get(19), false, false, false);
    }

    private void setUpMemberships() {
        Person grouperAll = new Person();
        grouperAll.setUsername("GrouperAll");
        personRepository.save(grouperAll);

        Iterable<Group> groups = groupRepository.findAll();

        for (Group group : groups) {
            group.addMember(grouperAll);
            groupRepository.save(group);
            for (Person person : group.getMembers()) {
                Membership membership = new Membership(person, group);
                membershipRepository.save(membership);
            }
        }

        Iterable<Grouping> groupings = groupingRepository.findAll();

        for (Grouping grouping : groupings) {
            Membership allExclude = membershipRepository.findByPersonAndGroup(grouperAll, grouping.getExclude());
            Membership allInclude = membershipRepository.findByPersonAndGroup(grouperAll, grouping.getInclude());
            Membership allComposite = membershipRepository.findByPersonAndGroup(grouperAll, grouping.getComposite());
            if (grouping.isOptOutOn()) {
                allComposite.setOptOutEnabled(true);
                allExclude.setOptInEnabled(true);
                allExclude.setOptOutEnabled(true);

            }
            if (grouping.isOptInOn()) {
                allComposite.setOptInEnabled(true);
                allInclude.setOptInEnabled(true);
                allInclude.setOptOutEnabled(true);

            }

            membershipRepository.save(allComposite);
            membershipRepository.save(allExclude);
            membershipRepository.save(allInclude);
        }
    }

    ///////////////////////////////////////////////////////////
    // factory methods
    ///////////////////////////////////////////////////////////

    //todo put strings in a config file
    private void makePerson(String name, String uuid, String username) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("cn", name);
        attributes.put("uuid", uuid);
        attributes.put("uid", username);
        persons.add(new Person(attributes));
    }

    private void makeGroup(List<Person> members, String path) {
        groups.add(new Group(path, members));
    }

    private void makeGrouping(
            String path,
            Group basis,
            Group exclude,
            Group include,
            Group owners,
            boolean listserveOn,
            boolean optInOn,
            boolean optOutOn) {

        Grouping grouping = new Grouping(path);
        Group composite = buildComposite(include, exclude, basis, path);
        groupRepository.save(composite);

        grouping.setBasis(basis);
        grouping.setExclude(exclude);
        grouping.setInclude(include);
        grouping.setOwners(owners);
        grouping.setComposite(composite);

        grouping.setListservOn(listserveOn);
        grouping.setOptInOn(optInOn);
        grouping.setOptOutOn(optOutOn);

        groupings.add(grouping);
    }

    ///////////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////////

    private Group buildComposite(Group include, Group exclude, Group basis, String path) {
        Group basisPlusInclude = addIncludedMembers(include, basis);
        Group compositeGroup = removeExcludedMembers(basisPlusInclude, exclude);
        compositeGroup.setPath(path);

        return compositeGroup;
    }

    private Group addIncludedMembers(Group include, Group basis) {
        Set<Person> s = new TreeSet<>();
        s.addAll(include.getMembers());
        s.addAll(basis.getMembers());

        return new Group(new ArrayList<>(s));
    }

    private Group removeExcludedMembers(Group basisPlusInclude, Group exclude) {
        List<Person> newBasisPlusInclude = new ArrayList<>(basisPlusInclude.getMembers());
        newBasisPlusInclude.removeAll(exclude.getMembers());

        Group basisPlusIncludeMinusExcludeGroup = new Group();
        basisPlusIncludeMinusExcludeGroup.setMembers(newBasisPlusInclude);

        return basisPlusIncludeMinusExcludeGroup;
    }
}
