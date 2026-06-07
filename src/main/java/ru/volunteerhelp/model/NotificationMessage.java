package ru.volunteerhelp.model;

public class NotificationMessage {
    private String id;
    private String requestId;
    private String coordinatorName;
    private String coordinatorPhone;
    private String messageText;
    private String createdAt;
    private String deliveryStatus;

    public NotificationMessage() {
    }

    public NotificationMessage(String id, String requestId, String coordinatorName, String coordinatorPhone,
                               String messageText, String createdAt, String deliveryStatus) {
        this.id = id;
        this.requestId = requestId;
        this.coordinatorName = coordinatorName;
        this.coordinatorPhone = coordinatorPhone;
        this.messageText = messageText;
        this.createdAt = createdAt;
        this.deliveryStatus = deliveryStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getCoordinatorPhone() {
        return coordinatorPhone;
    }

    public void setCoordinatorPhone(String coordinatorPhone) {
        this.coordinatorPhone = coordinatorPhone;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
