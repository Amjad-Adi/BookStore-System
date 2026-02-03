package com.example.database.Orders;

import java.time.LocalDate;

public class Order {
    private int id;
    private LocalDate date;
    private String channel;
    private String information;
    private double cost;
    private boolean isDisabled;
    private double amountPaid;
    private String paymentStatus;
    private Integer paymentPlanId;
    private Integer customerId;
    private Integer paymentMethodId;

    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private LocalDate returnDate;
    private String rentalStatus;

    private String orderType;

    public Order(LocalDate date, String channel, String information,
                 double cost, double amountPaid, String paymentStatus,
                 Integer customerId, Integer paymentMethodId, boolean isDisabled,
                 Integer paymentPlanId, String orderType) {
        this(0, date, channel, information, cost, amountPaid, paymentStatus,
                customerId, paymentMethodId, isDisabled, paymentPlanId, orderType,
                null, null, null, null);
    }

    public Order(int id, LocalDate date, String channel, String information,
                 double cost, double amountPaid, String paymentStatus,
                 Integer customerId, Integer paymentMethodId, boolean isDisabled,
                 Integer paymentPlanId, String orderType,
                 LocalDate rentalStartDate, LocalDate rentalEndDate, LocalDate returnDate,
                 String rentalStatus) {
        this.id = id;
        this.date = date;
        this.channel = channel;
        this.information = information;
        this.cost = cost;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
        this.customerId = customerId;
        this.paymentMethodId = paymentMethodId;
        this.paymentPlanId = paymentPlanId;
        this.isDisabled = isDisabled;
        this.orderType = (orderType == null || orderType.isBlank()) ? "Sale" : orderType;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.returnDate = returnDate;
        this.rentalStatus = rentalStatus;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getChannel() { return channel; }
    public String getInformation() { return information; }
    public double getCost() { return cost; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentStatus() { return paymentStatus; }
    public Integer getCustomerId() { return customerId; }
    public Integer getPaymentMethodId() { return paymentMethodId; }
    public Integer getPaymentPlanId() { return paymentPlanId; }
    public boolean isDisabled() { return isDisabled; }
    public String getOrderType() { return orderType; }
    public LocalDate getRentalStartDate() { return rentalStartDate; }
    public LocalDate getRentalEndDate() { return rentalEndDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getRentalStatus() { return rentalStatus; }

    public void setId(int id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setChannel(String channel) { this.channel = channel; }
    public void setInformation(String information) { this.information = information; }
    public void setCost(double cost) { this.cost = cost; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public void setPaymentPlanId(Integer paymentPlanId) { this.paymentPlanId = paymentPlanId; }
    public void setDisabled(boolean disabled) { isDisabled = disabled; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public void setRentalStartDate(LocalDate rentalStartDate) { this.rentalStartDate = rentalStartDate; }
    public void setRentalEndDate(LocalDate rentalEndDate) { this.rentalEndDate = rentalEndDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setRentalStatus(String rentalStatus) { this.rentalStatus = rentalStatus; }
}