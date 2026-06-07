package ru.volunteerhelp.model;

public enum HelpType {
    ACCOMPANIMENT("Сопровождение"),
    FOOD_DELIVERY("Доставка продуктов"),
    MEDICINE_DELIVERY("Доставка лекарств"),
    HOUSEHOLD_HELP("Бытовая помощь"),
    OUTDOOR_HELP("Помощь на улице"),
    OTHER("Другое");

    private final String title;

    HelpType(String title) {
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
