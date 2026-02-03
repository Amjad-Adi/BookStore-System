package com.example.database;

import com.example.database.Cart.Cart;

public class Session {

    private static volatile AuthDAO.CustomerSession customerSession;
    private static volatile AuthDAO.StaffSession staffSession;
    private static final Cart cart = new Cart();

    private Session() {}


    public static AuthDAO.CustomerSession getCustomerSession() { return customerSession; }
    public static void setCustomerSession(AuthDAO.CustomerSession session) { customerSession = session; }
    public static boolean isCustomerLoggedIn() { return customerSession != null; }
    public static void clearCustomer() {
        customerSession = null;
        cart.clear();
    }

    public static AuthDAO.StaffSession getStaffSession() { return staffSession; }
    public static void setStaffSession(AuthDAO.StaffSession session) { staffSession = session; }
    public static boolean isStaffLoggedIn() { return staffSession != null; }
    public static void clearStaff() {
        staffSession = null;
    }

    public static Cart getCart() {
        return cart;
    }
}
