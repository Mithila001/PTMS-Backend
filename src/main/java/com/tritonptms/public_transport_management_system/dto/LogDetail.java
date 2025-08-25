package com.tritonptms.public_transport_management_system.dto;

public class LogDetail {
    private String attribute;
    private String previousValue;
    private String newValue;

    public LogDetail(String attribute, String previousValue, String newValue) {
        this.attribute = attribute;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    // Getters and Setters
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}