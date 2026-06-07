package ru.volunteerhelp.model;

public class HelpRequest {
    private String id;
    private String applicantName;
    private String applicantPhone;
    private HelpType helpType;
    private LocationType locationType;
    private String address;
    private String plannedDateTime;
    private String description;
    private RequestStatus status;
    private String assignedVolunteerId;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
    private String cancelReason;
    private WeatherWarning weatherWarning;
    private String notificationText;
    private String statusHistory;

    public HelpRequest() {
    }

    public HelpRequest(String id, String applicantName, String applicantPhone, HelpType helpType,
                       LocationType locationType, String address, String plannedDateTime, String description,
                       RequestStatus status, String createdAt, String updatedAt) {
        this.id = id;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.helpType = helpType;
        this.locationType = locationType;
        this.address = address;
        this.plannedDateTime = plannedDateTime;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.statusHistory = createdAt + ": создана заявка со статусом " + status.getTitle();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public HelpType getHelpType() {
        return helpType;
    }

    public void setHelpType(HelpType helpType) {
        this.helpType = helpType;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlannedDateTime() {
        return plannedDateTime;
    }

    public void setPlannedDateTime(String plannedDateTime) {
        this.plannedDateTime = plannedDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getAssignedVolunteerId() {
        return assignedVolunteerId;
    }

    public void setAssignedVolunteerId(String assignedVolunteerId) {
        this.assignedVolunteerId = assignedVolunteerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public WeatherWarning getWeatherWarning() {
        return weatherWarning;
    }

    public void setWeatherWarning(WeatherWarning weatherWarning) {
        this.weatherWarning = weatherWarning;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(String statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String getStatusText() {
        return status == null ? "" : status.getTitle();
    }

    public String getHelpTypeText() {
        return helpType == null ? "" : helpType.getTitle();
    }

    public String getLocationTypeText() {
        return locationType == null ? "" : locationType.getTitle();
    }

    public String getWeatherWarningText() {
        return weatherWarning == null ? "" : weatherWarning.getWarningText();
    }

    public boolean isClosed() {
        return status == RequestStatus.COMPLETED || status == RequestStatus.CANCELLED;
    }

    public void addHistory(String line) {
        if (statusHistory == null || statusHistory.isBlank()) {
            statusHistory = line;
        } else {
            statusHistory = statusHistory + System.lineSeparator() + line;
        }
    }

    @Override
    public String toString() {
        return "№" + id + " — " + applicantName + " — " + getHelpTypeText();
    }
}
