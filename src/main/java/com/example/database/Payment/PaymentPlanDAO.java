package com.example.database.Payment;

import com.example.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class PaymentPlanDAO {

    public static ArrayList<PaymentPlan> getPaymentPlanList() {
        String sql = "SELECT * FROM Payment_Plan ORDER BY Payment_Plan_ID";
        ArrayList<PaymentPlan> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Payment_Plan_ID");
                int periodInMonths = rs.getInt("Payment_Plan_Period_In_Months");
                Integer monthsBeforeTrial = rs.getInt("Number_Of_Months_Before_The_Legal_Trial");
                if (rs.wasNull()) monthsBeforeTrial = null;
                String description = rs.getString("Payment_Plan_Description");
                boolean isDisabled = rs.getBoolean("Disabled_Plan");

                list.add(new PaymentPlan(id, periodInMonths, monthsBeforeTrial, description, isDisabled));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getPaymentPlanList failed: " + e.getMessage(), e);
        }
    }

    public static ArrayList<PaymentPlan> getEnabledPaymentPlanList() {
        String sql = "SELECT * FROM Payment_Plan WHERE Disabled_Plan=false ORDER BY Payment_Plan_ID ";
        ArrayList<PaymentPlan> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Payment_Plan_ID");
                int periodInMonths = rs.getInt("Payment_Plan_Period_In_Months");
                Integer monthsBeforeTrial = rs.getInt("Number_Of_Months_Before_The_Legal_Trial");
                if (rs.wasNull()) monthsBeforeTrial = null;
                String description = rs.getString("Payment_Plan_Description");
                boolean isDisabled = rs.getBoolean("Disabled_Plan");

                list.add(new PaymentPlan(id, periodInMonths, monthsBeforeTrial, description, isDisabled));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getPaymentPlanList failed: " + e.getMessage(), e);
        }
    }
    public static int insertPaymentPlan(PaymentPlan plan) {
        String sql = """
            INSERT INTO Payment_Plan 
            (Payment_Plan_Period_In_Months, Number_Of_Months_Before_The_Legal_Trial, Payment_Plan_Description, Disabled_Plan)
            VALUES (?, ?, ?, ?)""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, plan.getPeriodInMonths());

            if (plan.getMonthsBeforeLegalTrial() != null)
                ps.setInt(2, plan.getMonthsBeforeLegalTrial());
            else
                ps.setNull(2, Types.INTEGER);

            if (plan.getDescription() != null && !plan.getDescription().trim().isEmpty())
                ps.setString(3, plan.getDescription().trim());
            else
                ps.setNull(3, Types.VARCHAR);

            ps.setBoolean(4, plan.isDisabled());

            if (ps.executeUpdate() == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException("insertPaymentPlan failed: " + e.getMessage(), e);
        }
    }

    public static void updatePaymentPlan(PaymentPlan plan) {
        String sql = """
            UPDATE Payment_Plan
            SET Payment_Plan_Period_In_Months = ?,
                Number_Of_Months_Before_The_Legal_Trial = ?,
                Payment_Plan_Description = ?,
                Disabled_Plan = ?
            WHERE Payment_Plan_ID = ?""";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, plan.getPeriodInMonths());

            if (plan.getMonthsBeforeLegalTrial() != null)
                ps.setInt(2, plan.getMonthsBeforeLegalTrial());
            else
                ps.setNull(2, Types.INTEGER);

            if (plan.getDescription() != null && !plan.getDescription().trim().isEmpty())
                ps.setString(3, plan.getDescription().trim());
            else
                ps.setNull(3, Types.VARCHAR);

            ps.setBoolean(4, plan.isDisabled());
            ps.setInt(5, plan.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("updatePaymentPlan failed: " + e.getMessage(), e);
        }
    }
}
