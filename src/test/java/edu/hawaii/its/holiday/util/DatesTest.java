package edu.hawaii.its.holiday.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

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

            assertEquals(Calendar.SUNDAY, calSunday.get(Calendar.DAY_OF_WEEK));
            assertTrue(sunday.compareTo(date2) <= 0);
        }

        LocalDate date4 = Dates.previousSunday(christmasLocalDate);
        Calendar cal4 = Calendar.getInstance();
        cal4.setTime(Dates.toDate(date4));
        assertEquals(Calendar.SUNDAY, cal4.get(Calendar.DAY_OF_WEEK));
        assertEquals(23, cal4.get(Calendar.DAY_OF_MONTH));
        assertEquals(1962, cal4.get(Calendar.YEAR));
        cal4 = null;

        LocalDate date5 = Dates.previousSunday(newYearsDay2000LocalDate);
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(Dates.toDate(date5));
        assertEquals(Calendar.SUNDAY, cal5.get(Calendar.DAY_OF_WEEK));
        assertEquals(Calendar.DECEMBER, cal5.get(Calendar.MONTH));
        assertEquals(26, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(1999, cal5.get(Calendar.YEAR));
        cal5 = null;

        LocalDate date6 = Dates.newLocalDate(2010, Month.AUGUST, 1); // A Sunday.
        Calendar cal6 = Calendar.getInstance();
        cal6.setTime(Dates.toDate(date6));
        assertEquals(Calendar.SUNDAY, cal6.get(Calendar.DAY_OF_WEEK));
        assertEquals(Calendar.AUGUST, cal6.get(Calendar.MONTH));
        assertEquals(1, cal6.get(Calendar.DAY_OF_MONTH));
        assertEquals(2010, cal6.get(Calendar.YEAR));
        cal6 = null;
    }

    @Test
    public void testFirstOfYear() {
        assertEquals(Dates.newLocalDate(1959, Month.FEBRUARY, 3), dayMusicDiedLocalDate);

        LocalDate date0 = Dates.firstOfYear(dayMusicDiedLocalDate);
        assertEquals(Dates.newLocalDate(1959, Month.JANUARY, 1), date0);

        LocalDate date1 = Dates.firstOfYear(christmasLocalDate);
        assertEquals(Dates.newLocalDate(1962, Month.JANUARY, 1), date1);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfYear(Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date2);

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertEquals(4, cal3.get(Calendar.HOUR_OF_DAY));
        LocalDate date3 = Dates.firstOfYear(Dates.toLocalDate(cal3.getTime()));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date3);
        cal = null;
        cal3 = null;

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfYear(dt);
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), date4);

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
        assertEquals(1, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal5.get(Calendar.MONTH));
        assertEquals(2012, cal5.get(Calendar.YEAR));
        assertEquals(0, cal5.get(Calendar.SECOND));
        assertEquals(0, cal5.get(Calendar.MINUTE));
        assertEquals(0, cal5.get(Calendar.HOUR_OF_DAY));
        cal5 = null;
    }

    @Test
    public void testFirstOfPreviousYear() {
        LocalDate date0 = Dates.firstOfPreviousYear(dayMusicDiedLocalDate);
        assertEquals(Dates.newLocalDate(1958, Month.JANUARY, 1), date0);

        LocalDate date1 = Dates.firstOfPreviousYear(christmasLocalDate);
        assertEquals(Dates.newLocalDate(1961, Month.JANUARY, 1), date1);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.HOUR_OF_DAY, 4);
        LocalDate date2 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date2);

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(cal.getTime());
        assertEquals(4, cal3.get(Calendar.HOUR_OF_DAY));
        LocalDate date3 = Dates.firstOfPreviousYear(Dates.toLocalDate(cal3.getTime()));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date3);
        cal3 = null;
        cal = null;

        LocalDate dt = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        LocalDate date4 = Dates.firstOfPreviousYear(dt);
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), date4);

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
        assertEquals(1, cal5.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal5.get(Calendar.MONTH));
        assertEquals(2011, cal5.get(Calendar.YEAR));
        assertEquals(0, cal5.get(Calendar.SECOND));
        assertEquals(0, cal5.get(Calendar.MINUTE));
        assertEquals(0, cal5.get(Calendar.HOUR_OF_DAY));
        cal5 = null;
    }

    @Test
    public void lastDayOfMonth() {

        // Just a bunch of random checks.
        assertEquals(30, Dates.lastDayOfMonth(Month.SEPTEMBER, 1962));
        assertEquals(31, Dates.lastDayOfMonth(Month.JANUARY, 2000));
        assertEquals(29, Dates.lastDayOfMonth(Month.FEBRUARY, 2000));
        assertEquals(29, Dates.lastDayOfMonth(Month.FEBRUARY, 2012));

        // Now just run some comparisons against 
        // methods available from the Calendar class.
        Calendar cal = Calendar.getInstance();
        for (int year = 1979; year < 2039; year++) {
            cal.clear();
            cal.set(Calendar.YEAR, year);
            for (Month month : Month.values()) {
                cal.set(Calendar.MONTH, month.getValue() - 1);
                int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                assertEquals(lastDayOfMonth, Dates.lastDayOfMonth(month, year));
            }
        }

    }

    @Test
    public void firstOfMonth() {
        LocalDate date1 = Dates.firstOfMonth(Month.DECEMBER, 1962);
        LocalDate date2 = Dates.newLocalDate(1962, Month.DECEMBER, 1);

        assertEquals(date1, date2);

        Calendar cal = makeCalendar(Dates.toDate(date1));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(1962, cal.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
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

        assertEquals(date1, Dates.firstOfNextMonth(date0));
        assertEquals(date3, Dates.firstOfNextMonth(date2));
        assertEquals(date4, Dates.firstOfNextMonth(date3));
        assertEquals(date6, Dates.firstOfNextMonth(date5));

        Calendar cal0 = Calendar.getInstance();
        cal0.set(Calendar.SECOND, 33);
        cal0.set(Calendar.MINUTE, 22);
        cal0.set(Calendar.HOUR_OF_DAY, 11);
        cal0.set(Calendar.DAY_OF_MONTH, 3);
        cal0.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal0.set(Calendar.YEAR, 2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(Dates.firstOfNextMonth(Dates.toLocalDate(cal0.getTime()))));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testMonthValues() {
        // The use of these methods is not recommended.
        assertEquals(Month.JANUARY, Month.of(1));
        assertEquals(Month.JANUARY, Month.valueOf("JANUARY"));
        assertEquals(Month.DECEMBER, Month.of(12));
        assertEquals(Month.DECEMBER, Month.valueOf("DECEMBER"));

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

        assertEquals(from, Dates.newLocalDate(year, Month.of(month + 1), day));
        assertEquals(from, Dates.firstOfMonth(Month.of(month + 1), year));

        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(from));
        assertEquals(1999, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.MILLISECOND));

        LocalDate first = Dates.firstOfMonth(Month.of(month + 1), year);
        cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(first));
        assertEquals(1999, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void fromOffset() {
        final LocalDate dateStart = Dates.newLocalDate(2011, Month.JANUARY, 1);

        Calendar cal0 = Calendar.getInstance();
        cal0.setTime(Dates.toDate(dateStart));

        Calendar cal1 = makeCalendar(dateStart);
        assertEquals(cal0, cal1);

        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        LocalDate date = Dates.fromOffset(dateStart, 1);
        cal1 = makeCalendar(date);
        assertEquals(2, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, 1);
        cal1 = makeCalendar(date);
        assertEquals(3, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -2);
        cal1 = makeCalendar(date);
        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertEquals(31, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.DECEMBER, cal1.get(Calendar.MONTH));
        assertEquals(2010, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, 32);
        cal1 = makeCalendar(date);
        assertEquals(1, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.FEBRUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));

        date = Dates.fromOffset(date, -1);
        cal1 = makeCalendar(date);
        assertEquals(31, cal1.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal1.get(Calendar.MONTH));
        assertEquals(2011, cal1.get(Calendar.YEAR));
        assertEquals(0, cal1.get(Calendar.HOUR));
        assertEquals(0, cal1.get(Calendar.MINUTE));
        assertEquals(0, cal1.get(Calendar.SECOND));
        assertEquals(0, cal1.get(Calendar.MILLISECOND));
    }

    @Test
    public void firstDateOfYear() {
        assertEquals(Dates.newLocalDate(2000, Month.JANUARY, 1), Dates.firstDateOfYear(2000));
        assertEquals(Dates.newLocalDate(2009, Month.JANUARY, 1), Dates.firstDateOfYear(2009));
        assertEquals(Dates.newLocalDate(2010, Month.JANUARY, 1), Dates.firstDateOfYear(2010));
        assertEquals(Dates.newLocalDate(2011, Month.JANUARY, 1), Dates.firstDateOfYear(2011));
        assertEquals(Dates.newLocalDate(2012, Month.JANUARY, 1), Dates.firstDateOfYear(2012));

        // Check that the hours/minutes/seconds are zero-ed.
        Date date = Dates.toDate(Dates.firstDateOfYear(2012));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
    }

    @Test
    public void firstDateOfYear_Two() {
        for (int year = 2000; year < 2025; year++) {
            LocalDate date = Dates.newLocalDate(year, Month.JANUARY, 1);
            assertEquals(date, Dates.firstDateOfYear(year));
        }
    }

    @Test
    public void lastDateOfYear() {
        assertEquals(Dates.newLocalDate(2000, Month.DECEMBER, 31), Dates.lastDateOfYear(2000));
        assertEquals(Dates.newLocalDate(2009, Month.DECEMBER, 31), Dates.lastDateOfYear(2009));
        assertEquals(Dates.newLocalDate(2010, Month.DECEMBER, 31), Dates.lastDateOfYear(2010));
        assertEquals(Dates.newLocalDate(2011, Month.DECEMBER, 31), Dates.lastDateOfYear(2011));
        assertEquals(Dates.newLocalDate(2012, Month.DECEMBER, 31), Dates.lastDateOfYear(2012));

        // Check that the hours/minutes/seconds are zero-ed.
        LocalDate date = Dates.lastDateOfYear(2012);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Dates.toDate(date));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
        assertEquals(2012, cal.get(Calendar.YEAR));
    }

    @Test
    public void lastDateOfYear_Two() {
        LocalDate date = Dates.newLocalDate(2000, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2000, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2009, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2009, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2010, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2010, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2011, Month.JANUARY, 1);
        assertEquals(Dates.newLocalDate(2011, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        assertEquals(Dates.newLocalDate(2012, Month.DECEMBER, 31), Dates.lastDateOfYear(date));
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
            assertEquals(month, Dates.month(lastOfMonth));
            LocalDate firstOfMonth = Dates.firstDateOfMonth(month, year);
            assertEquals(month, Dates.month(firstOfMonth));
        }
    }

    @Test
    public void currentYear() {
        Calendar cal = makeCalendar();
        int year = cal.get(Calendar.YEAR);
        assertEquals(year, Dates.currentYear());
    }

    @Test
    public void yearOfDate() {
        for (int year = 2000; year < 2050; year++) {
            assertEquals(year, Dates.yearOfDate(Dates.firstDateOfYear(year)));
            assertEquals(year, Dates.yearOfDate(Dates.lastDateOfYear(year)));
            assertEquals(year, Dates.yearOfDate(Dates.lastDateOfMonth(Month.FEBRUARY, year)));
        }
    }

    @Test
    public void dayOfWeek() {
        LocalDate lod = Dates.toLocalDate(Dates.toDate(Dates.firstDateOfYear(2012)));
        for (int i = 0; i < 366; i++) {
            DayOfWeek dayOfWeek = lod.getDayOfWeek();
            assertEquals(dayOfWeek, Dates.dayOfWeek(lod));
            lod = lod.plusDays(1);
        }
        assertEquals(Dates.newLocalDate(2013, Month.JANUARY, 1), lod);

        // Just a random one, a Friday.
        LocalDate date = Dates.newLocalDate(2012, Month.DECEMBER, 7);
        assertEquals(DayOfWeek.FRIDAY, Dates.dayOfWeek(date));

    }

    @Test
    public void dayOfMonth() {
        int checks = 0;
        Calendar cal = makeCalendar(Dates.toDate(Dates.firstDateOfYear(2013)));
        assertEquals(Dates.firstDateOfYear(2013), Dates.toLocalDate(cal.getTime()));
        assertEquals(Dates.newLocalDate(2013, Month.JANUARY, 1), Dates.toLocalDate(cal.getTime()));

        for (Month m : Month.values()) {
            cal.set(Calendar.MONTH, m.getValue() - 1);
            int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int d = 1; d <= maxDays; d++) {
                LocalDate localDate = Dates.toLocalDate(cal.getTime());
                assertEquals(Dates.dayOfMonth(localDate), cal.get(Calendar.DAY_OF_MONTH));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                checks++;
            }
            LocalDate localDate = Dates.toLocalDate(cal.getTime());
            assertEquals(1, Dates.dayOfMonth(localDate));
        }

        assertEquals(365, checks);
        assertEquals(Dates.newLocalDate(2014, Month.JANUARY, 1), Dates.toLocalDate(cal.getTime()));
    }

    @Test
    public void formatDate() {
        LocalDate date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr1 = Dates.formatDate(date, "MM/yyyy");
        assertEquals("02/2012", dateStr1);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date, "M/yyyy");
        assertEquals("2/2012", dateStr2);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr3 = Dates.formatDate(date, "MM/yyyy");
        assertEquals("11/2013", dateStr3);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr4 = Dates.formatDate(date, "MMM/yyyy");
        assertEquals("Feb/2012", dateStr4);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr5 = Dates.formatDate(date, "MMM/yyyy");
        assertEquals("Nov/2013", dateStr5);

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr6 = Dates.formatDate(date, "M/yyyy");
        assertEquals("12/2013", dateStr6);

        // Invalid pattern; defaults to yyyy-MM-dd.
        String dateStr7 = Dates.formatDate(date, "what?");
        assertEquals("2013-12-30", dateStr7);

        // Not advised usage.
        String dateStr8 = Dates.formatDate(date, null);
        assertEquals("2013-12-30", dateStr8);
    }

    @Test
    public void format() {
        LocalDate date = null;
        assertEquals("", Dates.formatDate(date));

        Calendar cal = Calendar.getInstance();
        date = Dates.toLocalDate(cal.getTime());
        String dateStr0 = new SimpleDateFormat("MM/dd/yyyy").format(Dates.toDate(date));
        String dateStr1 = Dates.formatDate(date);
        assertEquals(dateStr1, dateStr0);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr2 = Dates.formatDate(date);
        assertEquals("02/29/2012", dateStr2);

        date = Dates.newLocalDate(2012, Month.FEBRUARY, 29);
        String dateStr3 = Dates.formatDate(date);
        assertEquals("02/29/2012", dateStr3);

        date = Dates.newLocalDate(2013, Month.NOVEMBER, 30);
        String dateStr4 = Dates.formatDate(date);
        assertEquals("11/30/2013", dateStr4);

        date = Dates.newLocalDate(2013, Month.DECEMBER, 30);
        String dateStr7 = Dates.formatDate(date);
        assertEquals("12/30/2013", dateStr7);
    }

    @Test
    public void dateToLocalDate() {
        LocalDate d = Dates.newLocalDate(2016, Month.OCTOBER, 31);
        assertEquals(31, d.getDayOfMonth());
        assertEquals(2016, d.getYear());
        assertEquals(Month.OCTOBER, d.getMonth());
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Dates> constructor = Dates.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
