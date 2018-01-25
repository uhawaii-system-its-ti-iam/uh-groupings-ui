package edu.hawaii.its.groupings.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.api.type.Campus;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.repository.CampusRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class CampusServiceTest {

    @Autowired
    private CampusService campusService;

    @Test
    public void findAll() {
        List<Campus> campuses = campusService.findAll();
        assertFalse(campuses.isEmpty());
        assertThat(campuses.size(), equalTo(11));

        // Make sure caching is configured.
        CampusRepository repo = campusService.getCampusRepository();
        campusService.setCampusRepository(null);
        List<Campus> list = campusService.findAll();
        assertFalse(list.isEmpty());
        assertThat(list.size(), equalTo(11));
        assertSame(campuses, list);
        campusService.setCampusRepository(repo);
    }

    @Test
    public void findActualAll() {
        List<Campus> campuses = campusService.findActualAll();
        assertFalse(campuses.isEmpty());
        assertThat(campuses.size(), equalTo(11));
        int id = 0;
        for (Campus c : campuses) {
            assertThat(c.getActual(), equalTo("Y"));

            // Goofy way to check on sorting.
            assertTrue(c.getId() > (id++));
        }

        // Make sure caching is configured.
        CampusRepository repo = campusService.getCampusRepository();
        campusService.setCampusRepository(null);
        List<Campus> list = campusService.findActualAll();
        assertFalse(list.isEmpty());
        assertThat(list.size(), equalTo(11));
        assertSame(campuses, list);
        campusService.setCampusRepository(repo);
    }

    @Test
    public void find() {
        Campus c0 = campusService.find(7);
        assertThat(c0.getId(), equalTo(7));
        assertThat(c0.getCode(), equalTo("MA"));
        assertThat(c0.getDescription(), equalTo("UH Manoa"));
        assertThat(c0.getActual(), equalTo("Y"));
    }

    @Test
    public void campusCache() {
        Campus c0 = campusService.find(1);
        Campus c1 = campusService.find(1);
        assertSame(c0, c1);

        CampusRepository repo = campusService.getCampusRepository();
        campusService.setCampusRepository(null);
        Campus c2 = campusService.find(1);
        Campus c3 = campusService.find(1);
        assertSame(c2, c3);
        campusService.setCampusRepository(repo);
    }

}
