package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Person;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, String> {
    List<Person> findByName(String name);
    List<Person> findByUuid(String uuid);
    Person findByUsername(String username);
}
