package edu.hawaii.its.groupings.type;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class Announcement {

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
    public String toString() {
        return "Announcements [" +
                "message='" + message + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                "]";
    }

}
