package com.example.database;

import com.example.database.DBConnection;
import java.sql.*;

public class CustomerAccountDAO {

    public static double getBudget(int customerId) {
        String sql = "SELECT Budget_In_Dollars FROM Customers WHERE Customer_Id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("Budget_In_Dollars") : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching budget: " + e.getMessage());
        }
    }

    public static void updateBudget(int customerId, double newAmount) {
        String sql = "UPDATE Customers SET Budget_In_Dollars = ? WHERE Customer_Id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newAmount);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating budget: " + e.getMessage());
        }
    }

    public static boolean deductBudget(int customerId, double amount) {
        String sql = "UPDATE Customers SET Budget_In_Dollars = Budget_In_Dollars - ? " +
                "WHERE Customer_Id = ? AND Budget_In_Dollars >= ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, customerId);
            ps.setDouble(3, amount);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Deduction failed: " + e.getMessage());
        }
    }
}