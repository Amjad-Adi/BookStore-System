package com.example.database.Products;

import java.time.LocalDate;

public class Review {
    private final int reviewId;
    private final int customerId;
    private final int productId;
    private final String customerName;
    private final String comment;
    private final int rating;
    private final LocalDate reviewDate;

    public Review(int reviewId, int customerId, int productId,
                  String customerName, String comment, int rating, LocalDate reviewDate) {
        this.reviewId = reviewId;
        this.customerId = customerId;
        this.productId = productId;
        this.customerName = customerName;
        this.comment = comment;
        this.rating = Math.max(1, Math.min(5, rating));
        this.reviewDate = reviewDate;
    }

    public int getReviewId() { return reviewId; }
    public int getCustomerId() { return customerId; }
    public int getProductId() { return productId; }
    public String getCustomerName() { return customerName; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public LocalDate getReviewDate() { return reviewDate; }

    @Override
    public String toString() {
        return "Review by " + (customerName == null ? "Anonymous" : customerName) +
                " - Rating: " + rating + "/5";
    }
}
