package com.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO {

    public static class StaffSession {
        public final int staffId;
        public final String role;
        public final String fullName;
        public final String encryptedPassword;
        public StaffSession(int staffId, String role, String fullName,String encryptedPassword) {
            this.staffId = staffId;
            this.role = role;
            this.fullName = fullName;
            this.encryptedPassword=encryptedPassword;
        }
    }

   public static class CustomerSession {
        public final int customerId;
        public final String fullName;
        public final String email;
        public final String encryptedPassword;

        public CustomerSession(int customerId, String fullName, String email, String encryptedPassword) {
            this.customerId = customerId;
            this.fullName = fullName;
            this.email = email;
            this.encryptedPassword = encryptedPassword;
        }
    }


    public static StaffSession loginStaff(String email) {
        String qStaff = """
        SELECT Staff_ID, First_Name, Last_Name, Staff_Email, Staff_Password
        FROM Staff
        WHERE Staff_Email=?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(qStaff)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int id = rs.getInt("Staff_ID");
                String fullName = rs.getString("First_Name") + " " + rs.getString("Last_Name");
                String staffEmail = rs.getString("Staff_Email");
                String encryptedPassword = rs.getString("Staff_Password");  // Get encrypted password
                return new StaffSession(id, fullName, staffEmail, encryptedPassword);  // Return the session with encrypted password

            }

        } catch (Exception e) {
            throw new RuntimeException("loginStaff failed: " + e.getMessage(), e);
        }
    }

    public static CustomerSession loginCustomer(String email) {
        String qCustomer = """
    SELECT Customer_Id, Customer_First_Name, Customer_Second_Name, Customer_Email, Customer_Password
    FROM Customers
    WHERE Customer_Email=?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(qCustomer)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                int id = rs.getInt("Customer_Id");
                String fullName = rs.getString("Customer_First_Name") + " " + rs.getString("Customer_Second_Name");
                String customerEmail = rs.getString("Customer_Email");
                String encryptedPassword = rs.getString("Customer_Password");  // Get encrypted password

                return new CustomerSession(id, fullName, customerEmail, encryptedPassword);  // Return the session with password

            }

        } catch (Exception e) {
            throw new RuntimeException("loginCustomer failed: " + e.getMessage(), e);
        }
    }


    private static boolean isAdmin(Connection con, int staffId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Admin WHERE Staff_ID=?")) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static boolean isWorker(Connection con, int staffId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Worker WHERE Staff_ID=?")) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}
