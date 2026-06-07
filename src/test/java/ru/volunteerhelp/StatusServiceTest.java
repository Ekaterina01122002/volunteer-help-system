package ru.volunteerhelp;

import org.junit.jupiter.api.Test;
import ru.volunteerhelp.util.TimeUtil;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusServiceTest {
    @Test
    void olderThanThreeDaysShouldBeOverdue() {
        String date = TimeUtil.format(LocalDateTime.now().minusDays(4));
        assertTrue(TimeUtil.isOlderThanDays(date, 3));
    }

    @Test
    void youngerThanThreeDaysShouldNotBeOverdue() {
        String date = TimeUtil.format(LocalDateTime.now().minusDays(2));
        assertFalse(TimeUtil.isOlderThanDays(date, 3));
    }
}
