package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Grouping;
import edu.hawaii.its.api.type.Person;

import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles("localTest")
public class DatabaseFactory {
    private int numberOfPersons = 100;

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

    private List<List<Person>> memberLists = new ArrayList<>();

    public DatabaseFactory(PersonRepository personRepository, GroupRepository groupRepository, GroupingRepository groupingRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;
        this.groupingRepository = groupingRepository;

        fillDatabase();

    }

    private void fillDatabase() {
        setUpPersons();

//        fillPersonRepository();
//        fillGroupRepository();
        fillGroupingRepository();

    }

    private void fillPersonRepository() {
        for(Person person : persons) {
            personRepository.save(person);
        }
    }

    private void fillGroupRepository() {
        for (Group group : groups) {
            groupRepository.save(group);
        }
    }

    private void fillGroupingRepository() {
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
                            List<Person> ownerMembers){

        Group basis = new Group();
        basis.setPath("path:to:grouping" + i + BASIS);
        basis.setMembers(basisMembers);

        Group exclude = new Group();
        exclude.setPath("path:to:grouping" + i + EXCLUDE);
        exclude.setMembers(excludeMembers);

        Group include = new Group();
        include.setPath("path:to:grouping" + i + INCLUDE);
        include.setMembers(includeMembers);

        Group owners = new Group();
        owners.setPath("path:to:grouping" + i + OWNERS);
        owners.setMembers(ownerMembers);

        groups.add(basis);
        groups.add(exclude);
        groups.add(include);
        groups.add(owners);
    }

    private void setUpGroup0 () {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();
        //TODO add people to the lists

        setUpGroup(0, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup1 () {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();
        //TODO add people to the lists

        setUpGroup(1, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup2 () {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();
        //TODO add people to the lists

        setUpGroup(2, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup3 () {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();
        //TODO add people to the lists

        setUpGroup(3, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroup4 () {
        List<Person> basisMembers = new ArrayList<>();
        List<Person> excludeMembers = new ArrayList<>();
        List<Person> includeMembers = new ArrayList<>();
        List<Person> ownerMembers = new ArrayList<>();
        //TODO add people to the lists

        setUpGroup(4, basisMembers, excludeMembers, includeMembers, ownerMembers);
    }

    private void setUpGroupings() {

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

    private void makeGrouping(String path,
                              Group basis,
                              Group exclude,
                              Group include,
                              Group owners,
                              boolean listServeOn,
                              boolean optInOn,
                              boolean optOutOn) {

        Grouping grouping = new Grouping(path);
        Group composite = buildComposite(include, exclude, basis, path);

        grouping.setBasis(basis);
        grouping.setExclude(exclude);
        grouping.setInclude(include);
        grouping.setOwners(owners);
        grouping.setComposite(composite);

        grouping.setListservOn(listServeOn);
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
