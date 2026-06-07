package ru.volunteerhelp.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String nextId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
