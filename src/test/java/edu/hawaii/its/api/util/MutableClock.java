package edu.hawaii.its.api.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class MutableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant initialInstant, ZoneId zone) {
        this.instant = initialInstant;
        this.zone = zone;
    }

    @Override public ZoneId getZone() {
        return zone;
    }

    @Override public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override public Instant instant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public void advanceBySeconds(long seconds) {
        this.instant = this.instant.plusSeconds(seconds);
    }
}

