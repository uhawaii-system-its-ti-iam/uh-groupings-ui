package edu.hawaii.its.groupings.type;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class EmployeeTest {

    private Employee employee;

    @Before
    public void setUp() {
        employee = new Employee();
    }

    @Test
    public void construction() {
        assertNotNull(employee);
        assertNull(employee.getUhUuid());

        employee = new Employee(123456789L);
        assertThat(employee.getUhUuid(), equalTo(123456789L));
    }

    @Test
    public void setters() {
        assertNotNull(employee);
        assertNull(employee.getUhUuid());
        assertNotNull(employee.toString());

        employee.setUhUuid(12345678L);
        assertThat(employee.getUhUuid(), equalTo(12345678L));
        assertThat(employee.toString(), containsString("uhUuid=12345678"));
    }
}
