package com.example.database.Staff;

import com.example.database.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public static List<Staff> getAllStaff() {
        String sql = "SELECT * FROM Staff ORDER BY Staff_ID";
        List<Staff> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Staff(
                        rs.getInt("Staff_ID"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Staff_Email"),
                        rs.getString("Staff_Password"),
                        rs.getDouble("Staff_Salary"),
                        toLocal(rs.getDate("Staff_Birth_Date")),
                        toLocal(rs.getDate("Staff_Hire_Date")),
                        rs.getString("Staff_Status")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getAllStaff failed: " + e.getMessage(), e);
        }
    }

    public static int insertStaff(Staff s) {
        String sql = """
            INSERT INTO Staff
            (First_Name, Last_Name, Staff_Email, Staff_Password, Staff_Salary, Staff_Birth_Date, Staff_Hire_Date, Staff_Status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPassword());
            ps.setDouble(5, s.getSalary());

            ps.setDate(6, Date.valueOf(s.getBirthDate()));
            ps.setDate(7, Date.valueOf(s.getHireDate()));

            ps.setString(8, (s.getStatus() == null || s.getStatus().isBlank()) ? "Active" : s.getStatus().trim());

            if (ps.executeUpdate() == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new RuntimeException("Staff email already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("insertStaff failed: " + e.getMessage(), e);
        }
    }

    public static boolean updateStaff(Staff s) {
        String sql = """
            UPDATE Staff
            SET First_Name=?,
                Last_Name=?,
                Staff_Email=?,
                Staff_Password=?,
                Staff_Salary=?,
                Staff_Birth_Date=?,
                Staff_Hire_Date=?,
                Staff_Status=?
            WHERE Staff_ID=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPassword());
            ps.setDouble(5, s.getSalary());
            ps.setDate(6, Date.valueOf(s.getBirthDate()));
            ps.setDate(7, Date.valueOf(s.getHireDate()));
            ps.setString(8, (s.getStatus() == null || s.getStatus().isBlank()) ? "Active" : s.getStatus().trim());
            ps.setInt(9, s.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new RuntimeException("Staff email already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("updateStaff failed: " + e.getMessage(), e);
        }
    }

    public static boolean setStaffStatus(int staffId, String status) {
        String sql = "UPDATE Staff SET Staff_Status=? WHERE Staff_ID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, staffId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("setStaffStatus failed: " + e.getMessage(), e);
        }
    }

    public static boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM Staff WHERE Staff_ID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, staffId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("deleteStaff failed: " + e.getMessage(), e);
        }
    }

    private static LocalDate toLocal(Date d) {
        return (d == null) ? null : d.toLocalDate();
    }
}
