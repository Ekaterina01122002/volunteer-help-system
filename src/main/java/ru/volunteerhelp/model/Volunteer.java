package ru.volunteerhelp.model;

public class Volunteer {
    private String id;
    private String fullName;
    private String phone;
    private String skills;
    private boolean available;
    private int assignedCount;

    public Volunteer() {
    }

    public Volunteer(String id, String fullName, String phone, String skills, boolean available) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.skills = skills;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAvailableText() {
        return available ? "Доступен" : "Недоступен";
    }

    public int getAssignedCount() {
        return assignedCount;
    }

    public void setAssignedCount(int assignedCount) {
        this.assignedCount = assignedCount;
    }

    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}
