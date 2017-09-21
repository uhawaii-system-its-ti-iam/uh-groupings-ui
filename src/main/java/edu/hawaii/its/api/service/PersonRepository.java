package edu.hawaii.its.api.service;

import edu.hawaii.its.api.type.Person;

import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, String> {
}
