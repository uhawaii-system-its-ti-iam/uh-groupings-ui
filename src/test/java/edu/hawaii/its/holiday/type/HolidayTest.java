package edu.hawaii.its.holiday.type;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.hawaii.its.holiday.util.Dates;

public class HolidayTest {

    private Holiday holiday;

    @Before
    public void setUp() {
        holiday = new Holiday();
    }

    @Test
    public void construction() {
        assertNotNull(holiday);
    }

    @Test
    public void setters() {
        assertNotNull(holiday);
        assertNull(holiday.getId());
        assertNull(holiday.getDescription());
        assertNull(holiday.getVersion());
        assertNotNull(holiday.getHolidayTypes());

        holiday.setId(666);
        holiday.setDescription("The Beast");
        holiday.setVersion(9);
        assertThat(holiday.getId(), equalTo(666));
        assertThat(holiday.getDescription(), equalTo("The Beast"));
        assertThat(holiday.getVersion(), equalTo(9));

        assertThat(holiday.getHolidayTypes().size(), equalTo(0));
        holiday.setHolidayTypes(null);
        assertNull(holiday.getHolidayTypes());

        assertNull(holiday.getObservedDate());
        assertNull(holiday.getYear());
        LocalDate xmas = Dates.newLocalDate(2016, Month.DECEMBER, 25);
        holiday.setObservedDate(Dates.toDate(xmas));
        assertThat(holiday.getYear().intValue(), equalTo(2016));

        assertNull(holiday.getOfficialDate());
    }

    @Test
    public void testHashCode() {
        Holiday h1 = new Holiday();
        Holiday h2 = new Holiday();
        assertThat(h1.hashCode(), equalTo(h2.hashCode()));

        LocalDate ld1 = Dates.newLocalDate(2016, Month.DECEMBER, 25);
        Date d1 = Dates.toDate(ld1);
        h1 = new Holiday(d1, d1);
        h1.setId(1);
        h1.setVersion(1);
        h1.setDescription("Xmas 2016");

        LocalDate ld2 = Dates.newLocalDate(2016, Month.DECEMBER, 26);
        Date d2 = Dates.toDate(ld2);
        h2 = new Holiday(d1, d2);
        h2.setId(1);
        h2.setVersion(1);
        h2.setDescription("Xmas 2016");

        assertThat(h1.hashCode(), not(equalTo(h2.hashCode())));

    }

    @Test
    public void testEquals() {
        Holiday h1 = new Holiday();
        assertEquals(h1, h1); // To self.
        assertTrue(h1.equals(h1)); // To self.
        assertFalse(h1.equals(null));
        assertFalse(h1.equals(new String())); // Wrong type.

        Holiday h2 = new Holiday();
        assertThat(h1, equalTo(h2));

        LocalDate ld1 = Dates.newLocalDate(2016, Month.DECEMBER, 25);
        Date d1 = Dates.toDate(ld1);
        h1 = new Holiday(d1, d1);
        assertThat(h1, not(equalTo(h2)));

        LocalDate ld2 = Dates.newLocalDate(2016, Month.DECEMBER, 25);
        Date d2 = Dates.toDate(ld2);
        h2 = new Holiday(d1, d2);
        assertThat(h1, equalTo(h2));

        h1.setId(1);
        assertThat(h1, not(equalTo(h2)));
        h2.setId(1);
        assertThat(h1, equalTo(h2));
        h1.setId(null);
        assertThat(h1, not(equalTo(h2)));
        h2.setId(null);
        assertThat(h1, equalTo(h2));
        h1.setId(1);
        h2.setId(1);
        assertThat(h1, equalTo(h2));

        // Version not in equals contract.
        h1.setVersion(1);
        assertThat(h1, equalTo(h2));
        h2.setVersion(1);
        assertThat(h1, equalTo(h2));

        h1.setDescription("Xmas 2016");
        assertThat(h1, not(equalTo(h2)));
        h2.setDescription("Xmas 2016");
        assertThat(h1, equalTo(h2));
        h1.setDescription(null);
        assertThat(h1, not(equalTo(h2)));
        h2.setDescription(null);
        assertThat(h1, equalTo(h2));
        h1.setDescription("Xmas 2016");
        h2.setDescription("Xmas 2016");
        assertThat(h1, equalTo(h2));

        h1.setObservedDate(null);
        assertThat(h1, not(equalTo(h2)));
        h2.setObservedDate(null);
        assertThat(h1, equalTo(h2));
        h1.setObservedDate(d1);
        h2.setObservedDate(d2);
        assertThat(h1, equalTo(h2));

        h1.setOfficialDate(null);
        assertThat(h1, not(equalTo(h2)));
        h2.setOfficialDate(null);
        assertThat(h1, equalTo(h2));
        h1.setOfficialDate(d1);
        h2.setOfficialDate(d2);
        assertThat(h1, equalTo(h2));

        LocalDate ld3 = Dates.newLocalDate(2016, Month.DECEMBER, 26);
        Date d3 = Dates.toDate(ld3);

        h2.setOfficialDate(d3);
        assertThat(h1, not(equalTo(h2)));
    }

    @Test
    public void testToString() {
        assertThat(holiday.toString(), containsString("id=null, description=null"));

        holiday.setId(12345);
        assertThat(holiday.toString(), containsString("Holiday [id=12345,"));
    }
}
