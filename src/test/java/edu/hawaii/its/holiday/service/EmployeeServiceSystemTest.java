package edu.hawaii.its.holiday.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class EmployeeServiceSystemTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void construction() {
        assertNotNull(employeeService.getEntityManager());
    }

    @Test
    public void exists() {
        assertTrue(employeeService.exists("89999999"));
        assertTrue(employeeService.exists("10000004"));

        assertFalse(employeeService.exists(null));
        assertFalse(employeeService.exists(""));
        assertFalse(employeeService.exists("  "));
        assertFalse(employeeService.exists("no-way-none"));
    }
}
