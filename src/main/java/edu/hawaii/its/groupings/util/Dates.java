package edu.hawaii.its.groupings.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public final class Dates {

    public static final String DATE_FORMAT = "MMMM dd, yyyy";
    public static final String DATE_SORT_FORMAT = "yyyy-MM-dd";

    // Private constructor; prevent instantiation.
    private Dates() {
        // Emtpy.
    }

    public static LocalDate newLocalDate(int year, Month month, int day) {
        return LocalDate.of(year, month, day);
    }

    public static LocalDateTime newLocalDateTime(int year, Month month, int day) {
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.of(year, month, day);
        return LocalDateTime.of(date, time);
    }

    public static Month month(LocalDate date) {
        return date.getMonth();
    }

    public static LocalDate firstOfYear(LocalDate date) {
        return newLocalDate(date.getYear(), Month.JANUARY, 1);
    }

    public static LocalDate firstOfNextYear(LocalDate date) {
        return newLocalDate(date.getYear() + 1, Month.JANUARY, 1);
    }

    public static LocalDate firstOfPreviousYear(LocalDate date) {
        return newLocalDate(date.getYear() - 1, Month.JANUARY, 1);
    }

    public static LocalDate firstOfMonth(Month month, int year) {
        return firstDateOfMonth(month, year);
    }

    public static LocalDate firstDateOfMonth(Month month, int year) {
        return newLocalDate(year, month, 1);
    }

    public static LocalDate firstOfNextMonth(LocalDate date) {
        return newLocalDate(date.getYear(), date.getMonth(), 1).plusMonths(1);
    }

    public static LocalDate previousSunday(LocalDate date) {
        return date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    }

    public static LocalDate fromOffset(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static int lastDayOfMonth(Month month, int year) {
        return LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .getDayOfMonth();
    }

    public static LocalDate lastDateOfMonth(Month month, int year) {
        return LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate firstDateOfYear(int year) {
        return newLocalDate(year, Month.JANUARY, 1);
    }

    public static LocalDate lastDateOfYear(int year) {
        return newLocalDate(year, Month.DECEMBER, 31);
    }

    public static LocalDate lastDateOfYear(LocalDate date) {
        return lastDateOfYear(date.getYear());
    }

    public static int dayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    public static String formatDate(LocalDate date, String formatStr) {
        return formatDate(toLocalDateTime(date), formatStr);
    }

    public static String formatDate(LocalDateTime datetime, String str) {
        String formatStr = str;
        if (datetime == null) {
            return "";
        }
        if (formatStr == null) {
            formatStr = DATE_SORT_FORMAT;
        }

        String result = datetime.toString();

        try {
            result = datetime.format(DateTimeFormatter.ofPattern(formatStr));
        } catch (Exception e) {
            // Try again with a basic pattern.
            result = formatDateBasicPattern(datetime);
        }

        return result;
    }

    private static String formatDateBasicPattern(LocalDateTime datetime) {
        String result;
        try {
            result = datetime.format(DateTimeFormatter.ofPattern(DATE_SORT_FORMAT));
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    private static LocalDateTime toLocalDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    // Not sure we really need this method.
    public static String formatDate(LocalDate date) {
        return formatDate(date, "MM/dd/yyyy");
    }

    public static int currentYear() {
        return LocalDate.now().getYear();
    }

    public static int yearOfDate(Date date) {
        return toLocalDate(date).getYear();
    }

    public static int yearOfDate(LocalDate date) {
        return date.getYear();
    }

    public static DayOfWeek dayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    private static ZoneId zoneId() {
        return ZoneId.systemDefault();
    }

    public static LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = zoneId();
        ZonedDateTime zoneDateTime = instant.atZone(zoneId);
        return zoneDateTime.toLocalDate();
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(zoneId()).toInstant());
    }
}
