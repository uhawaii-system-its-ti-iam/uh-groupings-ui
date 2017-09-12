package edu.hawaii.its.api.type;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

import edu.hawaii.its.api.service.GroupRepositoryService;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.hawaii.its.holiday.service.HolidayService;
import edu.hawaii.its.holiday.type.Holiday;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupRepositoryTest {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private GroupRepositoryService groupRepositoryService;

    @Test
    public void hibernateTest() {
        List<Holiday> holidays = holidayService.findHolidays();

        assertNotNull(holidays);
    }

    @Test
    public void hibernateTest2() {
        List<Group> groups = groupRepositoryService.findGroups();

        assertNotNull(groups);
    }
}
