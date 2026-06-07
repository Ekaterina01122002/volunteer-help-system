package ru.volunteerhelp.model;

public enum LocationType {
    INDOOR("Помещение"),
    OUTDOOR("Улица");

    private final String title;

    LocationType(String title) {
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
