package edu.hawaii.its.groupings.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hawaii.its.groupings.type.Campus;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootWebApplication.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CampusServiceTest {

    @Autowired
    private CampusService campusService;

    @Test
    public void findAll() {
        List<Campus> campuses = campusService.findActualAll();
        assertFalse(campuses.isEmpty());
    }

    @Test
    public void find() {
        Campus c0 = campusService.find(7);
        assertThat(c0.getId(), equalTo(7));
        assertThat(c0.getCode(), equalTo("MA"));
        assertThat(c0.getDescription(), equalTo("UH Manoa"));

    }

    @Test
    public void campusCache() {
        Campus c0 = campusService.find(1);
        Campus c1 = campusService.find(1);
        assertSame(c0, c1);
    }

}
