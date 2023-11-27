package edu.hawaii.its.groupings.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeTest {

    private Employee employee;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
    }

    @Test
    public void construction() {
        assertNotNull(employee);
        assertNull(employee.getUhUuid());

        employee = new Employee(123456789L);
        assertThat(employee.getUhUuid(), is(123456789L));
    }

    @Test
    public void setters() {
        assertNotNull(employee);
        assertNull(employee.getUhUuid());
        assertNotNull(employee.toString());

        employee.setUhUuid(12345678L);
        assertThat(employee.getUhUuid(), is(12345678L));
        assertThat(employee.toString(), containsString("uhUuid=12345678"));
    }
}
