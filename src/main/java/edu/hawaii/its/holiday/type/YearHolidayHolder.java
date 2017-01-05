package edu.hawaii.its.holiday.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import edu.hawaii.its.holiday.util.Dates;

public class YearHolidayHolder {

    private TreeMap<Integer, List<Holiday>> yearMap = new TreeMap<Integer, List<Holiday>>();
    private int year = 0;

    public YearHolidayHolder() {
        // Empty.
    }

    public YearHolidayHolder(List<Holiday> holidays) {
        if (holidays != null && !holidays.isEmpty()) {
            for (Holiday h : holidays) {
                add(Dates.yearOfDate(h.getObservedDate()), h);
                add(Dates.yearOfDate(h.getOfficialDate()), h);
            }

            Integer currentYear = currentYear();
            if (yearMap.containsKey(currentYear)) {
                this.year = currentYear;
            }
        }
    }

    // Note: do not change the access qualifier for this method
    // unless the year field logic is worked out properly.
    private void add(int year, Holiday holiday) {
        if (holiday != null) {
            List<Holiday> list = yearMap.get(Integer.valueOf(year));
            if (list == null) {
                list = new ArrayList<Holiday>();
                yearMap.put(Integer.valueOf(year), list);
            }
            if (!list.contains(holiday)) {
                list.add(holiday);
            }
            this.year = Math.max(this.year, year);
        }
    }

    public List<Holiday> getHolidays(Integer year) {
        if (year == null || !yearMap.containsKey(year)) {
            return new ArrayList<Holiday>(0);
        }
        return yearMap.get(year);
    }

    public Set<Integer> getYears() {
        return yearMap.descendingKeySet();
    }

    public boolean isEmpty() {
        return yearMap.isEmpty();
    }

    public Integer getYear() {
        return Integer.valueOf(year);
    }

    public void setYear(Integer year) {
        this.year = year != null ? year.intValue() : 0;
    }

    protected Integer currentYear() {
        return Dates.currentYear();
    }
}
