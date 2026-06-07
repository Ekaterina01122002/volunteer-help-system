package ru.volunteerhelp.model;

public class WeatherWarning {
    private String id;
    private String requestId;
    private double temperature;
    private String condition;
    private String warningText;
    private String checkedAt;

    public WeatherWarning() {
    }

    public WeatherWarning(double temperature, String condition, String warningText, String checkedAt) {
        this.temperature = temperature;
        this.condition = condition;
        this.warningText = warningText;
        this.checkedAt = checkedAt;
    }

    public WeatherWarning(String id, String requestId, double temperature, String condition,
                          String warningText, String checkedAt) {
        this.id = id;
        this.requestId = requestId;
        this.temperature = temperature;
        this.condition = condition;
        this.warningText = warningText;
        this.checkedAt = checkedAt;
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getWarningText() {
        return warningText;
    }

    public void setWarningText(String warningText) {
        this.warningText = warningText;
    }

    public String getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(String checkedAt) {
        this.checkedAt = checkedAt;
    }

    @Override
    public String toString() {
        return warningText == null ? "" : warningText;
    }
}
