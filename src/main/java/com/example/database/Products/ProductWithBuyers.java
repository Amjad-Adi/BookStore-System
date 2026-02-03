package com.example.database.Products;

import java.util.HashSet;
import java.util.Set;

public class ProductWithBuyers {
    private final Product product;
    private final int buyersCount;

    public ProductWithBuyers(Product product, int buyersCount) {
        this.product = product;
        this.buyersCount = buyersCount ;
    }


    public Product getProduct() { return product ; }
    public int getBuyersCount() { return buyersCount ; }
}
