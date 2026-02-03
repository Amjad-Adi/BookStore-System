package com.example.database.Payment;
public class PaymentMethod {
    private int id;
    private String name;
    private String description;
    private boolean isDisabled;

    public PaymentMethod(int id, String name, String description, boolean isDisabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isDisabled = isDisabled;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
