package edu.hawaii.its.groupings.type;

import java.time.LocalDateTime;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class Announcement implements Comparable<Announcement> {

    public static final long serialVersionUID = 2L;
    private final String message;
    private final State state;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyyMMdd'T'HHmmss")
    private final LocalDateTime start;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyyMMdd'T'HHmmss")
    private final LocalDateTime end;

    public enum State {
        Active,
        Expired,
        Future
    }

    // Constructor.
    @JsonCreator
    public Announcement(@JsonProperty("message") String message,
                        @JsonProperty("start") LocalDateTime start,
                        @JsonProperty("end") LocalDateTime end) {
        this.message = message;
        this.start = start;
        this.end = end;

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(start)) {
            state = State.Future;
        } else if (now.isAfter(end)) {
            state = State.Expired;
        } else if (now.isAfter(start) && now.isBefore(end)) {
            state = State.Active;
        } else {
            state = State.Expired;
        }
    }

    public State state() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public int compareTo(Announcement o) {
        return Comparator.comparing(Announcement::getStart)
                .compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Announcement that = (Announcement) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Announcement [" +
                "message='" + message + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                "]";
    }

}
