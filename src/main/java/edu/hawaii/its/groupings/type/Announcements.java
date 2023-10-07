package edu.hawaii.its.groupings.type;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Announcements {

    private final Comparator comparator =
            Comparator.comparing(Announcement::getStart)
                    .thenComparing(Announcement::getEnd)
                    .thenComparing(Announcement::getMessage);
    private final Set<Announcement> values = new TreeSet<>(comparator);

    // Constructor.
    public Announcements(@JsonProperty("announcements") List<Announcement> announcements) {
        if (announcements != null) {
            for (Announcement a : announcements) {
                this.values.add(a);
            }
        }
    }

    public List<Announcement> values() {
        return values.stream().collect(Collectors.toList());
    }

    public List<Announcement> getAnnouncements() {
        return values();
    }

    public List<Announcement> values(Announcement.State state) {
        return values.stream()
                .filter(a -> a.state() == state)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Announcements [" +
                "values=" + values +
                "]";
    }
}
