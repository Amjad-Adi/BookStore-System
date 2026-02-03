package com.example.database.Cart;
import com.example.database.Cart.CartItem;
import com.example.database.Products.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class Cart {
    private final List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    // just see if the product it's already in the cart increase the quantity
    // OR it's a new product to the cart
    public void add(Product product, int quantity) {
        if (product == null || quantity <= 0) return;

        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.addQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    // just iterate over the list and remove the matched product id
    public void remove(int productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId() == productId) {
                items.remove(i);
                return;
            }
        }
    }

    // assign the product quantity to other quantity
    public void updateQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            remove(productId);
            return;
        }

        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }


    // count the quantity of all the products
    public int getQuantityInCart(int productId) {
        int total = 0;
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                total += item.getQuantity();
            }
        }
        return total;
    }


    public double getTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }


    public int getItemCount() {
        return items.size();
    }
    public void clear() {
        items.clear();
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return String.format("Cart{items=%d, total=$%.2f}", items.size(), getTotal());
    }
}