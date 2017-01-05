package edu.hawaii.its.holiday.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class EmployeeTest {

    private Employee employee;

    @Before
    public void setUp() {
        employee = new Employee();
    }

    @Test
    public void construction() {
        assertNotNull(employee);
        assertNull(employee.getUhNumber());

        employee = new Employee(123456789L);
        assertThat(employee.getUhNumber(), equalTo(123456789L));
    }

    @Test
    public void setters() {
        assertNotNull(employee);
        assertNull(employee.getUhNumber());
        assertNotNull(employee.toString());

        employee.setUhNumber(12345678L);
        assertThat(employee.getUhNumber(), equalTo(12345678L));
        assertThat(employee.toString(), containsString("uhNumber=12345678"));
    }
}
