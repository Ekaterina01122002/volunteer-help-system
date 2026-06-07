package ru.volunteerhelp.storage;

import ru.volunteerhelp.model.HelpRequest;
import ru.volunteerhelp.model.NotificationMessage;
import ru.volunteerhelp.model.Volunteer;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private List<HelpRequest> requests = new ArrayList<>();
    private List<Volunteer> volunteers = new ArrayList<>();
    private List<NotificationMessage> notifications = new ArrayList<>();

    public List<HelpRequest> getRequests() {
        if (requests == null) {
            requests = new ArrayList<>();
        }
        return requests;
    }

    public void setRequests(List<HelpRequest> requests) {
        this.requests = requests;
    }

    public List<Volunteer> getVolunteers() {
        if (volunteers == null) {
            volunteers = new ArrayList<>();
        }
        return volunteers;
    }

    public void setVolunteers(List<Volunteer> volunteers) {
        this.volunteers = volunteers;
    }

    public List<NotificationMessage> getNotifications() {
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        return notifications;
    }

    public void setNotifications(List<NotificationMessage> notifications) {
        this.notifications = notifications;
    }
}
