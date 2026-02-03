package com.example.database.WareHouse;

import java.time.LocalDate;

public class Warehouse {
    private int id;
    private String name;
    private String address;
    private LocalDate dateOfEstablishment;
    private int maxStorage;
    private int currentStorage;
    private boolean isDisabled;

    public Warehouse(String name, String address, LocalDate dateOfEstablishment,
                     int maxStorage, int currentStorage, boolean isDisabled) {
        this(0, name, address, dateOfEstablishment, maxStorage, currentStorage, isDisabled);
    }

    public Warehouse(int id, String name, String address, LocalDate dateOfEstablishment,
                     int maxStorage, int currentStorage, boolean isDisabled) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateOfEstablishment = dateOfEstablishment;
        this.maxStorage = maxStorage;
        this.currentStorage = currentStorage;
        this.isDisabled = isDisabled;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public LocalDate getDateOfEstablishment() { return dateOfEstablishment; }
    public int getMaxStorage() { return maxStorage; }
    public int getCurrentStorage() { return currentStorage; }
    public boolean isDisabled() { return isDisabled; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setDateOfEstablishment(LocalDate dateOfEstablishment) { this.dateOfEstablishment = dateOfEstablishment; }
    public void setMaxStorage(int maxStorage) { this.maxStorage = maxStorage; }
    public void setCurrentStorage(int currentStorage) { this.currentStorage = currentStorage; }
    public void setDisabled(boolean disabled) { isDisabled = disabled; }

    public double getCapacityPercentage() {
        if (maxStorage == 0) return 0;
        return (currentStorage * 100.0) / maxStorage;
    }
}