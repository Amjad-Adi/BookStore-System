package com.example.database.WareHouse;
import com.example.database.WareHouse.*;
public class InventoryItem {
    private int inventoryId;
    private int warehouseId;
    private int productId;
    private String productName;
    private int quantityInWarehouse;
    private boolean isDisabled;

    public InventoryItem(int productId, String productName, int quantityInWarehouse) {
        this(0, 0, productId, productName, quantityInWarehouse, false);
    }

    public InventoryItem(int inventoryId, int warehouseId, int productId, String productName,
                         int quantityInWarehouse, boolean isDisabled) {
        this.inventoryId = inventoryId;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.productName = productName;
        this.quantityInWarehouse = quantityInWarehouse;
        this.isDisabled = isDisabled;
    }

    public int getInventoryId() { return inventoryId; }
    public int getWarehouseId() { return warehouseId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantityInWarehouse() { return quantityInWarehouse; }
    public boolean isDisabled() { return isDisabled; }

    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantityInWarehouse(int quantityInWarehouse) { this.quantityInWarehouse = quantityInWarehouse; }
    public void setDisabled(boolean disabled) { this.isDisabled = disabled; }

    @Override
    public String toString() {
        return productName + " - Qty: " + quantityInWarehouse;
    }
}