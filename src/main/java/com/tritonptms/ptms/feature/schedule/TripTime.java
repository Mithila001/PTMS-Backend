package com.tritonptms.ptms.feature.schedule;

import com.tritonptms.ptms.common.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TripTime {

    private TripTime() {
    }

    public static Interval interval(LocalDate date, LocalTime start, LocalTime end) {
        if (start.equals(end)) {
            throw new BadRequestException("Trip start and end times must differ.");
        }
        LocalDateTime startAt = date.atTime(start);
        LocalDateTime endAt = date.atTime(end);
        if (end.isBefore(start)) {
            endAt = endAt.plusDays(1);
        }
        return new Interval(startAt, endAt);
    }

    public record Interval(LocalDateTime start, LocalDateTime end) {
        public boolean overlaps(Interval other) {
            return start.isBefore(other.end) && other.start.isBefore(end);
        }
    }
}
