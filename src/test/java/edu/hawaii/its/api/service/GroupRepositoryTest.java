package edu.hawaii.its.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { SpringBootWebApplication.class })
@ContextConfiguration({"file:src/main/java/edu/hawaii/its/holiday/configuration/"})
@DataJpaTest
public class GroupRepositoryTest {

//    @Autowired
//    private GroupRepository groupRepository;

    @Test
    public void testTest() {
        assertTrue(true);
    }

}
