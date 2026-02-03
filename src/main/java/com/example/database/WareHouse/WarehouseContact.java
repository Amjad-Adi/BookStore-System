package com.example.database.WareHouse;

public class WarehouseContact {
    private int contactId;
    private int warehouseId;
    private String contactNumber;

    public WarehouseContact(String contactNumber) {
        this(0, 0, contactNumber);
    }

    public WarehouseContact(int contactId, int warehouseId, String contactNumber) {
        this.contactId = contactId;
        this.warehouseId = warehouseId;
        this.contactNumber = contactNumber;
    }

    public int getContactId() { return contactId; }
    public int getWarehouseId() { return warehouseId; }
    public String getContactNumber() { return contactNumber; }

    public void setContactId(int contactId) { this.contactId = contactId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return contactNumber;
    }
}