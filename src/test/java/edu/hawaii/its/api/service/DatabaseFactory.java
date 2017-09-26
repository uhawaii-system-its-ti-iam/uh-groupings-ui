package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Person;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFactory {
    private PersonRepository personRepository;
    private GroupRepository groupRepository;
    private Person[] persons = new Person[10];
    private Group[] groups = new Group[5];
    private List<List<Person>> memberLists = new ArrayList<>();

    public DatabaseFactory(PersonRepository personRepository, GroupRepository groupRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;

        fillDatabase();

    }

    public void fillDatabase() {
        fillPersonDatabase();
        fillGroupDatabase();

    }

    public void fillPersonDatabase() {

        for (int i = 0; i < 10; i++)

        {
            persons[i] = new Person("name" + i, "uuid" + i, "username" + i);
            personRepository.save(persons[i]);
        }

    }

    public void fillGroupDatabase() {
        for (int i = 0; i < 5; i++) {
            memberLists.add(new ArrayList<>());
        }

        memberLists.get(1).add(persons[0]);
        memberLists.get(1).add(persons[1]);

        memberLists.get(2).add(persons[2]);
        memberLists.get(2).add(persons[3]);
        memberLists.get(2).add(persons[4]);

        memberLists.get(3).add(persons[5]);
        memberLists.get(3).add(persons[6]);
        memberLists.get(3).add(persons[7]);
        memberLists.get(3).add(persons[8]);

        memberLists.get(4).add(persons[0]);
        memberLists.get(4).add(persons[1]);
        memberLists.get(4).add(persons[2]);
        memberLists.get(4).add(persons[3]);
        memberLists.get(4).add(persons[4]);
        memberLists.get(4).add(persons[5]);
        memberLists.get(4).add(persons[6]);
        memberLists.get(4).add(persons[7]);
        memberLists.get(4).add(persons[8]);
        memberLists.get(4).add(persons[9]);

        for (int i = 0; i < 5; i++)

        {
            groups[i] = new Group("path:to:group" + i, memberLists.get(i));
            groupRepository.save(groups[i]);
        }
    }


}
