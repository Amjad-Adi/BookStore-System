package com.example.database.Payment;

public class PaymentPlan {
    private int id;
    private int periodInMonths;
    private Integer monthsBeforeLegalTrial;
    private String description;
    private boolean isDisabled;

    public PaymentPlan(int id, int periodInMonths, Integer monthsBeforeLegalTrial, String description, boolean isDisabled) {
        this.id = id;
        this.periodInMonths = periodInMonths;
        this.monthsBeforeLegalTrial = monthsBeforeLegalTrial;
        this.description = description;
        this.isDisabled = isDisabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPeriodInMonths() {
        return periodInMonths;
    }

    public void setPeriodInMonths(int periodInMonths) {
        this.periodInMonths = periodInMonths;
    }

    public Integer getMonthsBeforeLegalTrial() {
        return monthsBeforeLegalTrial;
    }

    public void setMonthsBeforeLegalTrial(Integer monthsBeforeLegalTrial) {
        this.monthsBeforeLegalTrial = monthsBeforeLegalTrial;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}
