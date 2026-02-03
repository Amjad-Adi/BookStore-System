package com.example.database.Customer;

import com.example.database.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static ArrayList<Customer> getAllCustomers() {
        String sql = "SELECT * FROM Customers ORDER BY Customer_Id";
        ArrayList<Customer> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("Customer_Id");
                LocalDate birth = toLocal(rs.getDate("Customer_Birth_Date"));
                LocalDate reg   = toLocal(rs.getDate("Customer_Registration_Date"));
                LocalDate act   = toLocal(rs.getDate("Customer_Activation_Date"));
                LocalDate exp   = toLocal(rs.getDate("Customer_Expiration_Date"));

                double budget = rs.getDouble("Budget_In_Dollars");
                boolean isDisabled = rs.getBoolean("Disabled_Customer");
                list.add(new Customer(id, rs.getString("Customer_First_Name"), rs.getString("Customer_Second_Name"), rs.getString("Customer_Email"), rs.getString("Customer_Password"), rs.getString("Customer_Profession"), birth, reg, act, exp, budget, isDisabled));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getAllCustomers failed: " + e.getMessage(), e);
        }
    }

    public static int insertCustomer(Customer c) {
        String sql = """
            INSERT INTO Customers
            (Customer_First_Name, Customer_Second_Name, Customer_Email, Customer_Password, Customer_Profession,
             Customer_Birth_Date, Customer_Registration_Date, Customer_Activation_Date, Customer_Expiration_Date,
             Budget_In_Dollars,Disabled_Customer )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getSecondName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPassword());

            if (c.getProfession() != null) ps.setString(5, c.getProfession());
            else ps.setNull(5, Types.VARCHAR);

            if (c.getBirthDate() != null) ps.setDate(6, Date.valueOf(c.getBirthDate()));
            else ps.setNull(6, Types.DATE);

            ps.setDate(7, Date.valueOf(c.getRegisterDate()));

            if (c.getActivationDate() != null) ps.setDate(8, Date.valueOf(c.getActivationDate()));
            else ps.setNull(8, Types.DATE);

            if (c.getExpirationDate() != null) ps.setDate(9, Date.valueOf(c.getExpirationDate()));
            else ps.setNull(9, Types.DATE);

            ps.setDouble(10, c.getBudgetInDollars());
            ps.setBoolean(11, c.isDisabled()) ;
            if (ps.executeUpdate() == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new RuntimeException("Email already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("insertCustomer failed: " + e.getMessage(), e);
        }
    }

    public static void updateCustomer(Customer c) {
        String sql = """
            UPDATE Customers
            SET Customer_First_Name=?,
                Customer_Second_Name=?,
                Customer_Profession=?,
                Customer_Birth_Date=?,
                Customer_Registration_Date=?,
                Customer_Activation_Date=?,
                Customer_Expiration_Date=?,
                Budget_In_Dollars=?,
                Disabled_Customer=?
            WHERE Customer_Id=?""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getSecondName());

            if (c.getProfession() != null) ps.setString(3, c.getProfession());
            else ps.setNull(3, Types.VARCHAR);

            if (c.getBirthDate() != null) ps.setDate(4, Date.valueOf(c.getBirthDate()));
            else ps.setNull(4, Types.DATE);

            ps.setDate(5, Date.valueOf(c.getRegisterDate()));

            if (c.getActivationDate() != null) ps.setDate(6, Date.valueOf(c.getActivationDate()));
            else ps.setNull(6, Types.DATE);

            if (c.getExpirationDate() != null) ps.setDate(7, Date.valueOf(c.getExpirationDate()));
            else ps.setNull(7, Types.DATE);

            ps.setDouble(8, c.getBudgetInDollars());
            ps.setBoolean(9, c.isDisabled());
            ps.setInt(10, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateCustomer failed: " + e.getMessage(), e);
        }
    }

    private static LocalDate toLocal(Date d) {
        return (d == null) ? null : d.toLocalDate();
    }
}
