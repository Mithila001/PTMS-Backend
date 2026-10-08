package com.tritonptms.ptms.feature.schedule;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TripTimeTest {

    @Test
    void treatsEarlierEndAsNextDayAndAllowsTouchingIntervals() {
        LocalDate date = LocalDate.of(2026, 10, 8);
        var overnight = TripTime.interval(date, LocalTime.of(23, 0), LocalTime.of(1, 0));
        var followingTrip = TripTime.interval(date.plusDays(1), LocalTime.of(1, 0), LocalTime.of(2, 0));

        assertThat(overnight.end().toLocalDate()).isEqualTo(date.plusDays(1));
        assertThat(overnight.overlaps(followingTrip)).isFalse();
    }

    @Test
    void rejectsEqualStartAndEnd() {
        assertThatThrownBy(() -> TripTime.interval(LocalDate.now(), LocalTime.NOON, LocalTime.NOON))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("must differ");
    }
}
