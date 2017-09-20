package edu.hawaii.its.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.hawaii.its.api.type.Group;
import edu.hawaii.its.api.type.Person;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("localTest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@WebAppConfiguration
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testTest() {
        assertTrue(true);
    }

    @Test
    public void repositoryNotNullTest(){
        Iterable<Group> groups = groupRepository.findAll();
        assertNotNull(groups);
    }

    @Test
    public void personRepositoryTest() {
        Iterable<Person> persons = personRepository.findAll();
        assertNotNull(persons);
    }
}
