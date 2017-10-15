package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.Person;

import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles("localTest")
public class DatabaseSetup {
    private int numberOfPersons = 100;

    String pathRoot = "path:to:grouping";

    private static String BASIS = ":basis";
    private static String EXCLUDE = ":exclude";
    private static String INCLUDE = ":include";
    private static String OWNERS = ":owners";

    private PersonRepository personRepository;
    private GroupRepository groupRepository;
    private GroupingRepository groupingRepository;

    private List<Person> persons = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<Grouping> groupings = new ArrayList<>();

    public DatabaseSetup(PersonRepository personRepository, GroupRepository groupRepository, GroupingRepository groupingRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;
        this.groupingRepository = groupingRepository;

        fillDatabase();
    }

    private void fillDatabase() {


        fillPersonRepository();
        fillGroupRepository();
        fillGroupingRepository();

    }

    private void fillPersonRepository() {
        setUpPersons();

        for (Person person : persons) {
            personRepository.save(person);
        }
    }

    private void fillGroupRepository() {
        setUpGroups();

        for (Group group : groups) {
            groupRepository.save(group);
        }
    }

    private void fillGroupingRepository() {
        setUpGroupings();

        for (Grouping grouping : groupings) {
            groupingRepository.save(grouping);
        }
    }

    /////////////////////////////////////////////////////
    // setup methods
    /////////////////////////////////////////////////////

    private void setUpPersons() {
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


        makeGroup(basisMembers, pathRoot + i + BASIS);
        makeGroup(excludeMembers, pathRoot + i + EXCLUDE);
        makeGroup(includeMembers, pathRoot + i + INCLUDE);
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

        //todo change member lists
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

        //todo change member lists
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

        //todo change member lists
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

        //todo change member lists
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

    ///////////////////////////////////////////////////////////
    // factory methods
    ///////////////////////////////////////////////////////////

    private void makePerson(String name, String uuid, String username) {
        Person person = new Person(name, uuid, username);
        persons.add(person);
    }

    private void makeGroup(List<Person> members, String path) {
        Group group = new Group(path, members);
        groups.add(group);
    }

    public Grouping makeGrouping(String path,
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
        return grouping;
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

        for (Person person : exclude.getMembers()) {
            newBasisPlusInclude.remove(person);
        }
        basisPlusIncludeMinusExcludeGroup.setMembers(newBasisPlusInclude);

        return basisPlusIncludeMinusExcludeGroup;
    }
}
