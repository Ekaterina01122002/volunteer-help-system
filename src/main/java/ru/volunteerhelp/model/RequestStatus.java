package ru.volunteerhelp.model;

public enum RequestStatus {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнена"),
    CANCELLED("Отменена"),
    OVERDUE("Просрочена");

    private final String title;

    RequestStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
