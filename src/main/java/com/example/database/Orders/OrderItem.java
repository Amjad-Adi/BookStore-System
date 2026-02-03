package com.example.database.Orders;

public class OrderItem {
    private int transactionItemId;
    private int transactionId;
    private int productId;
    private String productName;
    private int quantity;
    private double priceAtTime;
    private double lineTotal;
    private boolean isDisabled;

    public OrderItem(int productId, String productName, int quantity, double priceAtTime) {
        this(0, 0, productId, productName, quantity, priceAtTime, false);
    }

    public OrderItem(int transactionItemId, int transactionId, int productId, String productName,
                     int quantity, double priceAtTime, boolean isDisabled) {
        this.transactionItemId = transactionItemId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.isDisabled = isDisabled;
        this.lineTotal = quantity * priceAtTime;
    }

    public int getTransactionItemId() { return transactionItemId; }
    public int getTransactionId() { return transactionId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPriceAtTime() { return priceAtTime; }
    public double getLineTotal() { return lineTotal; }
    public boolean isDisabled() { return isDisabled; }

    public void setTransactionItemId(int transactionItemId) { this.transactionItemId = transactionItemId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public void setProductId(int productId) { this.productId = productId; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateLineTotal();
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
        updateLineTotal();
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    private void updateLineTotal() {
        this.lineTotal = this.quantity * this.priceAtTime;
    }

    public String getProductName() {
        return productName;
    }

//    @Override
//    public String toString() {
//        return productId + " (x" + quantity + ") - $" + String.format("%.2f", lineTotal);
//    }
}