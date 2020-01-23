package edu.hawaii.its.groupings.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class DatesTest {

    protected LocalDate christmasLocalDate;
    protected LocalDate newYearsDay2000LocalDate;
    protected LocalDate dayMusicDiedLocalDate;

    protected Date christmasDate;
    protected Date dayMusicDiedDate;
    protected Date newYearsDay2000Date;

    @Before
    public void setUp() {
        christmasLocalDate = LocalDate.of(1962, Month.DECEMBER, 25);
        newYearsDay2000LocalDate = LocalDate.of(2000, Month.JANUARY, 1);
        dayMusicDiedLocalDate = LocalDate.of(1959, Month.FEBRUARY, 3);

        christmasDate = Dates.toDate(christmasLocalDate);
        dayMusicDiedDate = Dates.toDate(dayMusicDiedLocalDate);
        newYearsDay2000Date = Dates.toDate(newYearsDay2000LocalDate);
    }

    private Calendar makeCalendar() {
        return Calendar.getInstance();
    }

    private Calendar makeCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;
    }

    private Calendar makeCalendar(LocalDate date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date));

        return cal;
    }

    @Test
    public void testFindPreviousSunday() {
        LocalDate date1 = Dates.newLocalDate(2010, Month.AUGUST, 9); // A Monday.
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date1));

        for (int i = 0; i < 75; i++) {
            cal.add(Calendar.DATE, -13); // Move back some days.
            final LocalDate date2 = Dates.toLocalDate(cal.getTime());

            final LocalDate sunday = Dates.previousSunday(date2);
            Calendar calSunday = Calendar.getInstance();
            calSunday.setTime(Dates.toDate(sunday));

            assertThat(calSunday.get(Calendar.DAY_OF_WEEK), is(Calendar.SUNDAY));
            assertTrue(sunday.compareTo(date2) <= 0);
        }

        LocalDate date4 = Dates.previousSunday(christmasLocalDate);
        Calendar cal4 = Calendar.getInstance();
        cal4.setTime(Dates.toDate(date4));
        assertThat(cal4.get(Calendar.DAY_OF_WEEK), is(Calendar.SUNDAY));
        assertThat(cal4.get(Calendar.DAY_OF_MONTH), is(23));
        assertThat(cal4.get(Calendar.YEAR), is(1962));
        cal4 = null;

        LocalDate date5 = Dates.previousSunday(newYearsDay2000LocalDate);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(date5));
        assertThat(cal5.get(Calendar.DAY_OF_WEEK), is(Calendar.SUNDAY));
        assertThat(cal5.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(cal5.get(Calendar.DAY_OF_MONTH), is(26));
        assertThat(cal5.get(Calendar.YEAR), is(1999));
        cal5 = null;

        LocalDate date6 = Dates.newLocalDate(2010, Month.AUGUST, 1); // A Sunday.
        Calendar cal6 = Calendar.getInstance();
        cal6.setTime(Dates.toDate(date6));
        assertThat(cal6.get(Calendar.DAY_OF_WEEK), is(Calendar.SUNDAY));
        assertThat(cal6.get(Calendar.MONTH), is(Calendar.AUGUST));
        assertThat(cal6.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal6.get(Calendar.YEAR), is(2010));
        cal6 = null;
    }

    @Test
    public void testFirstOfYear() {
        assertThat(dayMusicDiedLocalDate, is(Dates.newLocalDate(1959, Month.FEBRUARY, 3)));

        LocalDate date0 = Dates.firstOfYear(dayMusicDiedLocalDate);
        assertThat(date0, is(Dates.newLocalDate(1959, Month.JANUARY, 1)));

        LocalDate date1 = Dates.firstOfYear(christmasLocalDate);
        assertThat(date1, is(Dates.newLocalDate(1962, Month.JANUARY, 1)));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfYear(Dates.toLocalDate(cal.getTime()));
        assertThat(date2, is(Dates.newLocalDate(2012, Month.JANUARY, 1)));

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertThat(cal3.get(Calendar.HOUR_OF_DAY), is(4));
        LocalDate date3 = Dates.firstOfYear(Dates.toLocalDate(cal3.getTime()));
        assertThat(date3, is(Dates.newLocalDate(2012, Month.JANUARY, 1)));
        cal = null;
        cal3 = null;

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfYear(dt);
        assertThat(date4, is(Dates.newLocalDate(2012, Month.JANUARY, 1)));

        Calendar cal4 = Calendar.getInstance();
        cal4.set(Calendar.SECOND, 33);
        cal4.set(Calendar.MINUTE, 22);
        cal4.set(Calendar.HOUR_OF_DAY, 11);
        cal4.set(Calendar.DAY_OF_MONTH, 29);
        cal4.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal4.set(Calendar.YEAR, 2012);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(Dates.firstOfYear(Dates.toLocalDate(cal4.getTime()))));
        cal4 = null;
        assertThat(cal5.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal5.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal5.get(Calendar.YEAR), is(2012));
        assertThat(cal5.get(Calendar.SECOND), is(0));
        assertThat(cal5.get(Calendar.MINUTE), is(0));
        assertThat(cal5.get(Calendar.HOUR_OF_DAY), is(0));
        cal5 = null;
    }

    @Test
    public void testFirstOfPreviousYear() {
        LocalDate date0 = Dates.firstOfPreviousYear(dayMusicDiedLocalDate);
        assertThat(date0, is(Dates.newLocalDate(1958, Month.JANUARY, 1)));

        LocalDate date1 = Dates.firstOfPreviousYear(christmasLocalDate);
        assertThat(date1, is(Dates.newLocalDate(1961, Month.JANUARY, 1)));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal.getTime()));
        assertThat(date2, is(Dates.newLocalDate(2011, Month.JANUARY, 1)));

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertThat(cal3.get(Calendar.HOUR_OF_DAY), is(4));
        LocalDate date3 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal3.getTime()));
        assertThat(date3, is(Dates.newLocalDate(2011, Month.JANUARY, 1)));
        cal3 = null;
        cal = null;

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfPreviousYear(dt);
        assertThat(date4, is(Dates.newLocalDate(2011, Month.JANUARY, 1)));

        Calendar cal4 = Calendar.getInstance();
        cal4.set(Calendar.SECOND, 33);
        cal4.set(Calendar.MINUTE, 22);
        cal4.set(Calendar.HOUR_OF_DAY, 11);
        cal4.set(Calendar.DAY_OF_MONTH, 29);
        cal4.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal4.set(Calendar.YEAR, 2012);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(Dates.firstOfPreviousYear(Dates.toLocalDate(cal4.getTime()))));
        cal4 = null;
        assertThat(cal5.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal5.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal5.get(Calendar.YEAR), is(2011));
        assertThat(cal5.get(Calendar.SECOND), is(0));
        assertThat(cal5.get(Calendar.MINUTE), is(0));
        assertThat(cal5.get(Calendar.HOUR_OF_DAY), is(0));
        cal5 = null;
    }

    @Test
    public void lastDayOfMonth() {

        // Just a bunch of random checks.
        assertThat(Dates.lastDayOfMonth(Month.SEPTEMBER, 1962), is(30));
        assertThat(Dates.lastDayOfMonth(Month.JANUARY, 2000), is(31));
        assertThat(Dates.lastDayOfMonth(Month.FEBRUARY, 2000), is(29));
        assertThat(Dates.lastDayOfMonth(Month.FEBRUARY, 2012), is(29));

        // Now just run some comparisons against
        // methods available from the Calendar class.
        Calendar cal = Calendar.getInstance();
        for (int year = 1979; year < 2039; year++) {
            cal.clear();
            cal.set(Calendar.YEAR, year);
            for (Month month : Month.values()) {
                cal.set(Calendar.MONTH, month.getValue() - 1);
                int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                assertThat(Dates.lastDayOfMonth(month, year), is(lastDayOfMonth));
            }
        }

    }

    @Test
    public void firstOfMonth() {
        LocalDate date1 = Dates.firstOfMonth(Month.DECEMBER, 1962);
        LocalDate date2 = Dates.newLocalDate(1962, Month.DECEMBER, 1);

        assertThat(date2, is(date1));

        Calendar cal = makeCalendar(Dates.toDate(date1));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(Calendar.YEAR), is(1962));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
    }

    @Test
    public void firstOfNextMonth() {
        LocalDate date0 = Dates.newLocalDate(2010, Month.DECEMBER, 31);
        LocalDate date1 = Dates.newLocalDate(2011, Month.JANUARY, 1);
        LocalDate date2 = Dates.newLocalDate(2011, Month.JANUARY, 31);
        LocalDate date3 = Dates.newLocalDate(2011, Month.FEBRUARY, 1);
        LocalDate date4 = Dates.newLocalDate(2011, Month.MARCH, 1);
        LocalDate date5 = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date6 = Dates.newLocalDate(2012, Month.MARCH, 1);

        assertThat(Dates.firstOfNextMonth(date0), is(date1));
        assertThat(Dates.firstOfNextMonth(date2), is(date3));
        assertThat(Dates.firstOfNextMonth(date3), is(date4));
        assertThat(Dates.firstOfNextMonth(date5), is(date6));

        Calendar cal0 = Calendar.getInstance();
        cal0.set(Calendar.SECOND, 33);
        cal0.set(Calendar.MINUTE, 22);
        cal0.set(Calendar.HOUR_OF_DAY, 11);
        cal0.set(Calendar.DAY_OF_MONTH, 3);
        cal0.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal0.set(Calendar.YEAR, 2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(Dates.firstOfNextMonth(Dates.toLocalDate(cal0.getTime()))));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.MARCH));
        assertThat(cal.get(Calendar.YEAR), is(2012));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
    }

    @Test
    public void testMonthValues() {
        // The use of these methods is not recommended.
        assertThat(Month.of(1), is(Month.JANUARY));
        assertThat(Month.valueOf("JANUARY"), is(Month.JANUARY));
        assertThat(Month.of(12), is(Month.DECEMBER));
        assertThat(Month.valueOf("DECEMBER"), is(Month.DECEMBER));

        int month = 5;
        int year = 1999;
        int day = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        final LocalDate from = Dates.toLocalDate(cal.getTime());

        assertThat(Dates.newLocalDate(year, Month.of(month + 1), day), is(from));
        assertThat(Dates.firstOfMonth(Month.of(month + 1), year), is(from));

        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(from));
        assertThat(cal.get(Calendar.YEAR), is(1999));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.JUNE));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.MILLISECOND), is(0));

        LocalDate first = Dates.firstOfMonth(Month.of(month + 1), year);
        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(first));
        assertThat(cal.get(Calendar.YEAR), is(1999));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.JUNE));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.MILLISECOND), is(0));
    }

    @Test
    public void fromOffset() {
        final LocalDate dateStart = Dates.newLocalDate(2011, Month.JANUARY, 1);

        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(Dates.toDate(dateStart));

        Calendar cal1 = makeCalendar(dateStart);
        assertThat(cal1, is(cal0));

        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        LocalDate date = Dates.fromOffset(dateStart, 1);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(2));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        date = Dates.fromOffset(date, 1);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(3));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        date = Dates.fromOffset(date, -2);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(31));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(cal1.get(Calendar.YEAR), is(2010));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        date = Dates.fromOffset(date, 32);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.FEBRUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat( cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertThat(cal1.get(Calendar.DAY_OF_MONTH), is(31));
        assertThat(cal1.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal1.get(Calendar.YEAR), is(2011));
        assertThat(cal1.get(Calendar.HOUR), is(0));
        assertThat(cal1.get(Calendar.MINUTE), is(0));
        assertThat(cal1.get(Calendar.SECOND), is(0));
        assertThat(cal1.get(Calendar.MILLISECOND), is(0));
    }

    @Test
    public void firstDateOfYear() {
        assertThat(Dates.firstDateOfYear(2000), is(Dates.newLocalDate(2000, Month.JANUARY, 1)));
        assertThat(Dates.firstDateOfYear(2009), is(Dates.newLocalDate(2009, Month.JANUARY, 1)));
        assertThat(Dates.firstDateOfYear(2010), is(Dates.newLocalDate(2010, Month.JANUARY, 1)));
        assertThat(Dates.firstDateOfYear(2011), is(Dates.newLocalDate(2011, Month.JANUARY, 1)));
        assertThat(Dates.firstDateOfYear(2012), is(Dates.newLocalDate(2012, Month.JANUARY, 1)));

        // Check that the hours/minutes/seconds are zero-ed.
        Date date = Dates.toDate(Dates.firstDateOfYear(2012));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.JANUARY));
        assertThat(cal.get(Calendar.YEAR), is(2012));
    }

    @Test
    public void firstDateOfYearTwo() {
        for (int year = 2000; year < 2025; year++) {
            LocalDate date = Dates.newLocalDate(year, Month.JANUARY, 1);
            assertThat(Dates.firstDateOfYear(year), is(date));
        }
    }

    @Test
    public void lastDateOfYear() {
        assertThat(Dates.lastDateOfYear(2000), is(Dates.newLocalDate(2000, Month.DECEMBER, 31)));
        assertThat(Dates.lastDateOfYear(2009), is(Dates.newLocalDate(2009, Month.DECEMBER, 31)));
        assertThat(Dates.lastDateOfYear(2010), is(Dates.newLocalDate(2010, Month.DECEMBER, 31)));
        assertThat(Dates.lastDateOfYear(2011), is(Dates.newLocalDate(2011, Month.DECEMBER, 31)));
        assertThat(Dates.lastDateOfYear(2012), is(Dates.newLocalDate(2012, Month.DECEMBER, 31)));

        // Check that the hours/minutes/seconds are zero-ed.
        LocalDate date = Dates.lastDateOfYear(2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date));
        assertThat(cal.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(cal.get(Calendar.MINUTE), is(0));
        assertThat(cal.get(Calendar.SECOND), is(0));
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(31));
        assertThat(cal.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(cal.get(Calendar.YEAR), is(2012));
    }

    @Test
    public void lastDateOfYearTwo() {
        LocalDate date = Dates.newLocalDate(2000, Month.JANUARY, 1);
        assertThat(Dates.lastDateOfYear(date), is(Dates.newLocalDate(2000, Month.DECEMBER, 31)));
        date = Dates.newLocalDate(2009, Month.JANUARY, 1);
        assertThat(Dates.lastDateOfYear(date), is(Dates.newLocalDate(2009, Month.DECEMBER, 31)));
        date = Dates.newLocalDate(2010, Month.JANUARY, 1);
        assertThat(Dates.lastDateOfYear(date), is(Dates.newLocalDate(2010, Month.DECEMBER, 31)));
        date = Dates.newLocalDate(2011, Month.JANUARY, 1);
        assertThat(Dates.lastDateOfYear(date), is(Dates.newLocalDate(2011, Month.DECEMBER, 31)));
        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        assertThat(Dates.lastDateOfYear(date), is(Dates.newLocalDate(2012, Month.DECEMBER, 31)));
    }

    @Test
    public void testMisc() {

        for (int year = 1962; year < 2050; year++) {
            final LocalDate endOfYear = Dates.newLocalDate(year, Month.DECEMBER, 31);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            assertThat(endOfYear, equalTo(Dates.toLocalDate(cal.getTime())));

            cal.add(Calendar.DAY_OF_MONTH, 1);
            LocalDate firstOfNextYear = Dates.firstOfMonth(Month.JANUARY, year + 1);
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));

            firstOfNextYear = Dates.firstOfNextYear(endOfYear);
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));

            firstOfNextYear = Dates.firstOfNextYear(Dates.lastDateOfYear(year));
            assertThat(firstOfNextYear, equalTo(Dates.toLocalDate(cal.getTime())));
        }
    }

    @Test
    public void month() {
        for (Month month : Month.values()) {
            int year = 2012;
            LocalDate lastOfMonth = Dates.lastDateOfMonth(month, year);
            assertThat(Dates.month(lastOfMonth), is(month));
            LocalDate firstOfMonth = Dates.firstDateOfMonth(month, year);
            assertThat(Dates.month(firstOfMonth), is(month));
        }
    }

    @Test
    public void currentYear() {
        Calendar cal = makeCalendar();
        int year = cal.get(Calendar.YEAR);
        assertThat(Dates.currentYear(), is(year));
    }

    @Test
    public void yearOfDate() {
        for (int year = 2000; year < 2050; year++) {
            assertThat(Dates.yearOfDate(Dates.firstDateOfYear(year)), is(year));
            assertThat(Dates.yearOfDate(Dates.lastDateOfYear(year)), is(year));
            assertThat(Dates.yearOfDate(Dates.lastDateOfMonth(Month.FEBRUARY, year)), is(year));
        }
    }

    @Test
    public void dayOfWeek() {
        LocalDate lod = Dates.toLocalDate(Dates.toDate(Dates.firstDateOfYear(2012)));
        for (int i = 0; i < 366; i++) {
            DayOfWeek dayOfWeek = lod.getDayOfWeek();
            assertThat(Dates.dayOfWeek(lod), is(dayOfWeek));
            lod = lod.plusDays(1);
        }
        assertThat(lod, is(Dates.newLocalDate(2013, Month.JANUARY, 1)));

        // Just a random one, a Friday.
        LocalDate date = Dates.newLocalDate(2012, Month.DECEMBER, 7);
        assertThat(Dates.dayOfWeek(date), is(DayOfWeek.FRIDAY));
    }

    @Test
    public void dayOfMonth() {
        int checks = 0;
        Calendar cal = makeCalendar(Dates.toDate(Dates.firstDateOfYear(2013)));
        assertThat(Dates.toLocalDate(cal.getTime()), is(Dates.firstDateOfYear(2013)));
        assertThat(Dates.toLocalDate(cal.getTime()), is(Dates.newLocalDate(2013, Month.JANUARY, 1)));

        for (Month m : Month.values()) {
            cal.set(Calendar.MONTH, m.getValue() - 1);
            int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int d = 1; d <= maxDays; d++) {
                LocalDate localDate = Dates.toLocalDate(cal.getTime());
                assertThat(cal.get(Calendar.DAY_OF_MONTH), is(Dates.dayOfMonth(localDate)));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                checks++;
            }
            LocalDate localDate = Dates.toLocalDate(cal.getTime());
            assertThat(Dates.dayOfMonth(localDate), is(1));
        }

        assertThat(checks, is(365));
        assertThat(Dates.toLocalDate(cal.getTime()), is(Dates.newLocalDate(2014, Month.JANUARY, 1)));
    }

    @Test
    public void formatDate() {
        LocalDate date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr1 = Dates.formatDate(date, "MM/yyyy");
        assertThat(dateStr1, is("02/2012"));

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date, "M/yyyy");
        assertThat(dateStr2, is("2/2012"));

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr3 = Dates.formatDate(date, "MM/yyyy");
        assertThat(dateStr3, is("11/2013"));

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr4 = Dates.formatDate(date, "MMM/yyyy");
        assertThat(dateStr4, is("Feb/2012"));

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr5 = Dates.formatDate(date, "MMM/yyyy");
        assertThat(dateStr5, is("Nov/2013"));

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr6 = Dates.formatDate(date, "M/yyyy");
        assertThat(dateStr6, is("12/2013"));

        // Invalid pattern; defaults to yyyy-MM-dd.
        String dateStr7 = Dates.formatDate(date, "what?");
        assertThat(dateStr7, is("2013-12-30"));

        // Not advised usage.
        String dateStr8 = Dates.formatDate(date, null);
        assertThat(dateStr8, is("2013-12-30"));

        LocalDateTime ldt = LocalDateTime.of(1962, Month.DECEMBER, 25, 13, 45);
        String dateStr9 = Dates.formatDate(ldt, "yyyyMMdd'T'HHmm");
        assertThat(dateStr9, is("19621225T1345"));
    }

    @Test
    public void formatDate2() {
        LocalDate date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr6 = Dates.formatDate(date, "M/yyyy");
        assertThat(dateStr6, is("12/2013"));

        // Invalid pattern; defaults to yyyy-MM-dd.
        String dateStr7 = Dates.formatDate(date, "aint-no-format");
        assertThat(dateStr7, is("2013-12-30"));

        // Not advised usage.
        String dateStr8 = Dates.formatDate(date, null);
        assertThat(dateStr8, is("2013-12-30"));
    }

    @Test
    public void format() {
        LocalDate date = null;
        assertThat(Dates.formatDate(date), is(""));

        Calendar cal = Calendar.getInstance();
        date = Dates.toLocalDate(cal.getTime());
        String dateStr0 = new SimpleDateFormat("MM/dd/yyyy").format(Dates.toDate(date));
        String dateStr1 = Dates.formatDate(date);
        assertThat(dateStr0, is(dateStr1));

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date);
        assertThat(dateStr2, is("02/29/2012"));

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr3 = Dates.formatDate(date);
        assertThat(dateStr3, is("02/29/2012"));

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr4 = Dates.formatDate(date);
        assertThat(dateStr4, is("11/30/2013"));

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr7 = Dates.formatDate(date);
        assertThat(dateStr7, is("12/30/2013"));

        String dateStr8 = Dates.formatDate(date, "yyyyMMdd'T'HHmmss");
        assertThat(dateStr8, is("20131230T000000"));

        // Cause an internal exception to occur;
        // format defaults to a basic pattern.
        String dateStr9 = Dates.formatDate(date, "not-a-format");
        assertThat(dateStr9, is("2013-12-30"));

        LocalDateTime n = LocalDateTime.of(2017, Month.APRIL, 1, 12, 34, 56);
        String dateStrA = Dates.formatDate(n, "yyyyMMdd'T'HHmmss");
        assertThat(dateStrA, is("20170401T123456"));
    }

    @Test
    public void formatDateBasicPattern() throws Exception {

        LocalDateTime n = LocalDateTime.of(2017, Month.APRIL, 1, 12, 34, 56);

        Constructor<Dates> c = Dates.class.getDeclaredConstructor();
        c.setAccessible(true);
        Dates dates = c.newInstance();

        String dateStr0 = ReflectionTestUtils.invokeMethod(dates,
                "formatDateBasicPattern", n);
        assertThat(dateStr0, is("2017-04-01"));

        // Method returns empty string when error occurs.
        n = null;
        String dateStr1 = ReflectionTestUtils.invokeMethod(dates,
                "formatDateBasicPattern", n);
        assertThat(dateStr1, is(""));
    }

    @Test
    public void dateToLocalDate() {
        LocalDate d = Dates.newLocalDate(2016, Month.OCTOBER, 31);
        assertThat(d.getDayOfMonth(), is(31));
        assertThat(d.getYear(), is(2016));
        assertThat(d.getMonth(), is(Month.OCTOBER));
    }

    @Test
    public void dateToLocalDateTime() {
        LocalDateTime d = Dates.newLocalDateTime(2017, Month.MARCH, 28);
        assertThat(d.getDayOfMonth(), is(28));
        assertThat(d.getYear(), is(2017));
        assertThat(d.getMonth(), is(Month.MARCH));
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Dates> constructor = Dates.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
