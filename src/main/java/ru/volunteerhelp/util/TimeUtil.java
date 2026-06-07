package ru.volunteerhelp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TimeUtil() {
    }

    public static String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public static String format(LocalDate date, int hour, int minute) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute)).format(DATE_TIME_FORMATTER);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            return LocalDateTime.now();
        }
    }

    public static boolean isOlderThanDays(String dateTime, int days) {
        LocalDateTime created = parse(dateTime);
        return created.plusDays(days).isBefore(LocalDateTime.now());
    }
}
