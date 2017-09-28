package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Person;

import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles("localTest")
public class DatabaseFactory {

    private static String BASIS = ":basis";
    private static String EXCLUDE = ":exclude";
    private static String INCLUDE = ":include";
    private static String OWNERS = ":owners";

    private PersonRepository personRepository;
    private GroupRepository groupRepository;
    private Person[] persons = new Person[100];
    private Group[] groups = new Group[25];
    private List<List<Person>> memberLists = new ArrayList<>();

    public DatabaseFactory(PersonRepository personRepository, GroupRepository groupRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;

        fillDatabase();

    }

    private void fillDatabase() {
        for (int i = 0; i < groups.length; i++) {
            memberLists.add(new ArrayList<>());
        }

        fillPersonDatabase();
        fillGroupDatabase();

    }

    private void fillPersonDatabase() {

        for (int i = 0; i < persons.length; i++) {
            persons[i] = new Person("name" + i, "uuid" + i, "username" + i);
            personRepository.save(persons[i]);
        }
    }

    private void fillGroupDatabase() {
        addGrouping0();
        addGrouping1();
        addGrouping2();
        addGrouping3();
        addGrouping4();
    }

    private void addGrouping0() {
        int i = 0;
        int k = i * 5;

        memberLists.get(k + 1).add(persons[0]);
        memberLists.get(k + 1).add(persons[1]);
        memberLists.get(k + 1).add(persons[2]);

        memberLists.get(k + 2).add(persons[5]);
        memberLists.get(k + 2).add(persons[6]);
        memberLists.get(k + 2).add(persons[7]);
        memberLists.get(k + 2).add(persons[8]);
        memberLists.get(k + 2).add(persons[9]);

        memberLists.get(k + 3).add(persons[3]);
        memberLists.get(k + 3).add(persons[4]);
        memberLists.get(k + 3).add(persons[5]);
        memberLists.get(k + 3).add(persons[6]);
        memberLists.get(k + 3).add(persons[7]);
        memberLists.get(k + 3).add(persons[8]);
        memberLists.get(k + 3).add(persons[9]);

        memberLists.get(k + 4).add(persons[0]);

        memberLists.get(k).addAll(makeComposite(
                memberLists.get(k + 1),
                memberLists.get(k + 2),
                memberLists.get(k +  3)));

        saveGrouping(i);
    }

    private void addGrouping1() {
        int i = 1;
        int k = i * 5;

        memberLists.get(k + 1).add(persons[0]);
        memberLists.get(k + 1).add(persons[1]);
        memberLists.get(k + 1).add(persons[2]);

        memberLists.get(k + 2).add(persons[5]);
        memberLists.get(k + 2).add(persons[6]);
        memberLists.get(k + 2).add(persons[7]);
        memberLists.get(k + 2).add(persons[8]);
        memberLists.get(k + 2).add(persons[9]);

        memberLists.get(k + 3).add(persons[3]);
        memberLists.get(k + 3).add(persons[4]);
        memberLists.get(k + 3).add(persons[5]);
        memberLists.get(k + 3).add(persons[6]);
        memberLists.get(k + 3).add(persons[7]);
        memberLists.get(k + 3).add(persons[8]);
        memberLists.get(k + 3).add(persons[9]);

        memberLists.get(k + 4).add(persons[0]);

        memberLists.get(k).addAll(makeComposite(
                memberLists.get(k + 1),
                memberLists.get(k + 2),
                memberLists.get(k +  3)));

        saveGrouping(i);
    }

    private void addGrouping2() {
        int i = 2;
        int k = i * 5;

        memberLists.get(k + 1).add(persons[0]);
        memberLists.get(k + 1).add(persons[1]);
        memberLists.get(k + 1).add(persons[2]);

        memberLists.get(k + 2).add(persons[5]);
        memberLists.get(k + 2).add(persons[6]);
        memberLists.get(k + 2).add(persons[7]);
        memberLists.get(k + 2).add(persons[8]);
        memberLists.get(k + 2).add(persons[9]);

        memberLists.get(k + 3).add(persons[3]);
        memberLists.get(k + 3).add(persons[4]);
        memberLists.get(k + 3).add(persons[5]);
        memberLists.get(k + 3).add(persons[6]);
        memberLists.get(k + 3).add(persons[7]);
        memberLists.get(k + 3).add(persons[8]);
        memberLists.get(k + 3).add(persons[9]);

        memberLists.get(k + 4).add(persons[0]);

        memberLists.get(k).addAll(makeComposite(
                memberLists.get(k + 1),
                memberLists.get(k + 2),
                memberLists.get(k +  3)));

        saveGrouping(i);
    }

    private void addGrouping3() {
        int i = 3;
        int k = i * 5;

        memberLists.get(k + 1).add(persons[0]);
        memberLists.get(k + 1).add(persons[1]);
        memberLists.get(k + 1).add(persons[2]);

        memberLists.get(k + 2).add(persons[5]);
        memberLists.get(k + 2).add(persons[6]);
        memberLists.get(k + 2).add(persons[7]);
        memberLists.get(k + 2).add(persons[8]);
        memberLists.get(k + 2).add(persons[9]);

        memberLists.get(k + 3).add(persons[3]);
        memberLists.get(k + 3).add(persons[4]);
        memberLists.get(k + 3).add(persons[5]);
        memberLists.get(k + 3).add(persons[6]);
        memberLists.get(k + 3).add(persons[7]);
        memberLists.get(k + 3).add(persons[8]);
        memberLists.get(k + 3).add(persons[9]);

        memberLists.get(k + 4).add(persons[0]);

        memberLists.get(k).addAll(makeComposite(
                memberLists.get(k + 1),
                memberLists.get(k + 2),
                memberLists.get(k +  3)));

        saveGrouping(i);
    }

    private void addGrouping4() {
        int i = 4;
        int k = i * 5;

        memberLists.get(k + 1).add(persons[0]);
        memberLists.get(k + 1).add(persons[1]);
        memberLists.get(k + 1).add(persons[2]);

        memberLists.get(k + 2).add(persons[5]);
        memberLists.get(k + 2).add(persons[6]);
        memberLists.get(k + 2).add(persons[7]);
        memberLists.get(k + 2).add(persons[8]);
        memberLists.get(k + 2).add(persons[9]);

        memberLists.get(k + 3).add(persons[3]);
        memberLists.get(k + 3).add(persons[4]);
        memberLists.get(k + 3).add(persons[5]);
        memberLists.get(k + 3).add(persons[6]);
        memberLists.get(k + 3).add(persons[7]);
        memberLists.get(k + 3).add(persons[8]);
        memberLists.get(k + 3).add(persons[9]);

        memberLists.get(k + 4).add(persons[0]);

        memberLists.get(k).addAll(makeComposite(
                memberLists.get(k + 1),
                memberLists.get(k + 2),
                memberLists.get(k +  3)));

        saveGrouping(i);
    }

    private void saveGrouping(int i) {
        groups[(i * 5)] = new Group("path:to:grouping" + (i), memberLists.get(i * 5));
        groups[(i * 5) + 1] = new Group("path:to:grouping" + (i) + INCLUDE, memberLists.get((i * 5) + 1));
        groups[(i * 5) + 2] = new Group("path:to:grouping" + (i) + EXCLUDE, memberLists.get((i * 5) + 2));
        groups[(i * 5) + 3] = new Group("path:to:grouping" + (i) + BASIS, memberLists.get((i * 5) + 3));
        groups[(i * 5) + 4] = new Group("path:to:grouping" + (i) + OWNERS, memberLists.get((i * 5) + 4));
        groupRepository.save(groups[(i * 5)]);
        groupRepository.save(groups[(i * 5) + 1]);
        groupRepository.save(groups[(i * 5) + 2]);
        groupRepository.save(groups[(i * 5) + 3]);
        groupRepository.save(groups[(i * 5) + 4]);

    }

    private List<Person> makeComposite(List<Person> include, List<Person> exclude, List<Person> basis) {
        List<Person> basisPlusInclude = unionMemberLists(include, basis);
        return removeExcludedPersons(basisPlusInclude, exclude);
    }

    private List<Person> unionMemberLists(List<Person> include, List<Person> basis) {
        List<Person> unionList = new ArrayList<>();
        unionList.addAll(include);
        unionList.addAll(basis);

        //remove duplicates
        Set<Person> s = new TreeSet<>();
        s.addAll(unionList);
        return Arrays.asList(s.toArray(new Person[s.size()]));
    }

    private List<Person> removeExcludedPersons(List<Person> basisPlusInclude, List<Person> exclude) {
        ArrayList<Person> newBasisPlusInclude = new ArrayList<>();
        newBasisPlusInclude.addAll(basisPlusInclude);
       for(Person person : exclude) {
           newBasisPlusInclude.remove(person);
       }
        return newBasisPlusInclude;
    }
}
