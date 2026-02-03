package com.example.database.Payment;

import com.example.database.DBConnection;

import java.sql.*;
import java.util.*;

public class PaymentMethodDAO {
    public static ArrayList<PaymentMethod> getPaymentMethodList() {
        String sql = "Select * from Payment_Method order by Payment_Method_Id";
        try (Connection databaseConnection = DBConnection.getConnection();
             PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql);
             ResultSet sqlExecutionResult = preparedStatement.executeQuery()) {
            ArrayList<PaymentMethod> list = new ArrayList<>();
            while (sqlExecutionResult.next()) {
                list.add(new PaymentMethod(
                        sqlExecutionResult.getInt("Payment_Method_Id"),
                        sqlExecutionResult.getString("Payment_Method_Name"),
                        sqlExecutionResult.getString("Method_Description"),
                        sqlExecutionResult.getBoolean("Disabled_Payment_Method")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getting Payment Method List failed: " + e.getMessage(), e);
        }
    }

    public static ArrayList<PaymentMethod> getEnabledPaymentMethods() {
        String sql = "Select * from Payment_Method Where Disabled_Payment_Method=false order by Payment_Method_Id";
        try (Connection databaseConnection = DBConnection.getConnection();
             PreparedStatement ps = databaseConnection.prepareStatement(sql);
             ResultSet sqlExecutionResult = ps.executeQuery()) {
            ArrayList<PaymentMethod> list = new ArrayList<>();
            while (sqlExecutionResult.next()) {
                list.add(new PaymentMethod(
                        sqlExecutionResult.getInt("Payment_Method_Id"),
                        sqlExecutionResult.getString("Payment_Method_Name"),
                        sqlExecutionResult.getString("Method_Description"),
                        sqlExecutionResult.getBoolean("Disabled_Payment_Method")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getting Payment Method List failed: " + e.getMessage(), e);
        }
    }
    public static int insertPaymentMethod(PaymentMethod paymentMethod) {
        String sql = """
            Insert into Payment_Method (Payment_Method_Name, Method_Description, Disabled_Payment_Method)
            values(?,?,?)""";
        try (Connection databaseConnection = DBConnection.getConnection();
             PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, paymentMethod.getName().trim());
            if (paymentMethod.getDescription() != null && !paymentMethod.getDescription().trim().isEmpty())
                preparedStatement.setString(2, paymentMethod.getDescription().trim());
            else
                preparedStatement.setNull(2, Types.VARCHAR);
            preparedStatement.setBoolean(3, paymentMethod.isDisabled());
            if (preparedStatement.executeUpdate() == 0) return -1;
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new RuntimeException("Payment method name already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("Inserting Payment Method failed: " + e.getMessage(), e);
        }
    }

    public static void updatePaymentMethod(PaymentMethod paymentMethod) {
        String sql = """
            UPDATE Payment_Method
            SET Payment_Method_Name=?,
                Method_Description=?,
                Disabled_Payment_Method=?
            WHERE Payment_Method_Id=?""";
        try (Connection databaseConnection = DBConnection.getConnection();
             PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql)) {
            preparedStatement.setString(1, paymentMethod.getName().trim());
            if (paymentMethod.getDescription() != null && !paymentMethod.getDescription().trim().isEmpty())
                preparedStatement.setString(2, paymentMethod.getDescription().trim());
            else
                preparedStatement.setNull(2, Types.VARCHAR);
            preparedStatement.setBoolean(3, paymentMethod.isDisabled());
            preparedStatement.setInt(4, paymentMethod.getID());
            preparedStatement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new RuntimeException("Payment method name already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("updatePaymentMethod failed: " + e.getMessage(), e);
        }
    }
}
