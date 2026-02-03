package com.example.database.DashBoard;

import com.example.database.DBConnection;
import com.example.database.Orders.Order;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class DashboardDAO {

    public static class Stats {
        public final int customers;
        public final int orders;
        public final int products;
        public final double revenuePaid;
        public final int warehouses;

        public Stats(int customers, int orders, int products, double revenuePaid, int warehouses) {
            this.customers = customers;
            this.orders = orders;
            this.products = products;
            this.revenuePaid = revenuePaid;
            this.warehouses = warehouses;
        }
    }

    public static Stats loadStats() {
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM Customers WHERE Disabled_Customer = 0) AS customers,
                (SELECT COUNT(*) FROM `Transaction` WHERE Disabled_Transaction = 0) AS orders,
                (SELECT COUNT(*) FROM Product WHERE Disabled_Product = 0) AS products,
                (SELECT COALESCE(SUM(Amount_Paid), 0) FROM `Transaction`
                 WHERE Payment_Status = 'Paid' AND Disabled_Transaction = 0) AS revenuePaid,
                (SELECT COUNT(*) FROM Warehouse WHERE Disabled_Warehouse = 0) AS warehouses
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Stats(
                        rs.getInt("customers"),
                        rs.getInt("orders"),
                        rs.getInt("products"),
                        rs.getDouble("revenuePaid"),
                        rs.getInt("warehouses")
                );
            }
            return new Stats(0, 0, 0, 0.0, 0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dashboard stats: " + e.getMessage(), e);
        }
    }

    public static List<Order> recentOrders(int limit) {
        String sql = """
            SELECT
                t.*,
                r.Rental_Start_Date,
                r.Rental_End_Date,
                r.Return_Date,
                r.Rental_Status,
                r.Late_Fee_Per_Day
            FROM `Transaction` t
            LEFT JOIN Rental r ON r.Transaction_ID = t.Transaction_ID
            WHERE t.Disabled_Transaction = 0
            ORDER BY t.Transaction_Date DESC, t.Transaction_ID DESC
            LIMIT ?
            """;

        List<Order> orders = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date transactionDate = rs.getDate("Transaction_Date");
                    Date rentalStartDate = rs.getDate("Rental_Start_Date");
                    Date rentalEndDate = rs.getDate("Rental_End_Date");
                    Date returnDate = rs.getDate("Return_Date");

                    int custId = rs.getInt("Customer_ID");
                    Integer customerId = rs.wasNull() ? null : custId;

                    int pmId = rs.getInt("Payment_Method_Id");
                    Integer paymentMethodId = rs.wasNull() ? null : pmId;

                    int ppId = rs.getInt("Payment_Plan_ID");
                    Integer paymentPlanId = rs.wasNull() ? null : ppId;

                    double lf = rs.getDouble("Late_Fee_Per_Day");
                    Double lateFeePerDay = rs.wasNull() ? null : lf;
                    orders.add(new Order(
                            rs.getInt("Transaction_ID"),
                            transactionDate != null ? ((java.sql.Date) transactionDate).toLocalDate() : LocalDate.now(),
                            rs.getString("Transaction_Channel"),
                            rs.getString("Transaction_Information"),
                            rs.getDouble("Cost"),
                            rs.getDouble("Amount_Paid"),
                            rs.getString("Payment_Status"),
                            customerId,
                            paymentMethodId,
                            rs.getBoolean("Disabled_Transaction"),
                            paymentPlanId,
                            rs.getString("Order_Type"),
                            rentalStartDate != null? ((java.sql.Date) rentalStartDate).toLocalDate() : null,
                            rentalEndDate != null ? ((java.sql.Date) rentalEndDate).toLocalDate() : null,
                            returnDate != null ? ((java.sql.Date) returnDate).toLocalDate() : null,
                            rs.getString("Rental_Status")
                    ));
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load recent orders: " + e.getMessage(), e);
        }
    }

    public static class RevenueByMethod {
        public final String methodName;
        public final double revenue;
        public final int orderCount;

        public RevenueByMethod(String methodName, double revenue, int orderCount) {
            this.methodName = methodName;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }
    }

    public static List<RevenueByMethod> loadRevenueByPaymentMethod() {
        String sql = """
            SELECT 
                COALESCE(pm.Payment_Method_Name, 'No Method') AS method_name,
                COALESCE(SUM(t.Amount_Paid), 0) AS revenue,
                COUNT(*) AS order_count
            FROM `Transaction` t
            LEFT JOIN Payment_Method pm ON t.Payment_Method_Id = pm.Payment_Method_Id
            WHERE t.Disabled_Transaction = 0 AND t.Payment_Status = 'Paid'
            GROUP BY pm.Payment_Method_Name
            ORDER BY revenue DESC
            """;

        List<RevenueByMethod> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new RevenueByMethod(
                        rs.getString("method_name"),
                        rs.getDouble("revenue"),
                        rs.getInt("order_count")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load revenue by payment method: " + e.getMessage(), e);
        }
    }

    public static class MonthlyRevenue {
        public final String month;
        public final double revenue;

        public MonthlyRevenue(String month, double revenue) {
            this.month = month;
            this.revenue = revenue;
        }
    }

    public static List<MonthlyRevenue> loadMonthlyRevenue() {
        String sql = """
            SELECT 
                DATE_FORMAT(Transaction_Date, '%Y-%m') AS month,
                COALESCE(SUM(Amount_Paid), 0) AS revenue
            FROM `Transaction`
            WHERE Disabled_Transaction = 0 
                AND Payment_Status = 'Paid'
            GROUP BY month
            ORDER BY month
            """;

        List<MonthlyRevenue> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new MonthlyRevenue(
                        rs.getString("month"),
                        rs.getDouble("revenue")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load monthly revenue: " + e.getMessage(), e);
        }
    }

    public static class CustomerGrowth {
        public final String month;
        public final int registered;
        public final int activated;

        public CustomerGrowth(String month, int registered, int activated) {
            this.month = month;
            this.registered = registered;
            this.activated = activated;
        }
    }

    public static List<CustomerGrowth> loadCustomerGrowth() {
        String sql = """
            SELECT 
                DATE_FORMAT(Customer_Registration_Date, '%Y-%m') AS month,
                COUNT(*) AS registered,
                SUM(CASE WHEN Customer_Activation_Date IS NOT NULL THEN 1 ELSE 0 END) AS activated
            FROM Customers
            WHERE Disabled_Customer = 0
            GROUP BY month
            ORDER BY month
            """;

        List<CustomerGrowth> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new CustomerGrowth(
                        rs.getString("month"),
                        rs.getInt("registered"),
                        rs.getInt("activated")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customer growth: " + e.getMessage(), e);
        }
    }

    public static class OrderTypeStats {
        public final String orderType;
        public final int count;
        public final double revenue;

        public OrderTypeStats(String orderType, int count, double revenue) {
            this.orderType = orderType;
            this.count = count;
            this.revenue = revenue;
        }
    }

    public static List<OrderTypeStats> loadOrderTypeStats() {
        String sql = """
            SELECT 
                Order_Type,
                COUNT(*) AS count,
                COALESCE(SUM(Amount_Paid), 0) AS revenue
            FROM `Transaction`
            WHERE Disabled_Transaction = 0
            GROUP BY Order_Type
            ORDER BY count DESC
            """;

        List<OrderTypeStats> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new OrderTypeStats(
                        rs.getString("Order_Type"),
                        rs.getInt("count"),
                        rs.getDouble("revenue")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load order type stats: " + e.getMessage(), e);
        }
    }

    public static class ChannelStats {
        public final String channel;
        public final int count;

        public ChannelStats(String channel, int count) {
            this.channel = channel;
            this.count = count;
        }
    }

    public static List<ChannelStats> loadChannelStats() {
        String sql = """
            SELECT 
                Transaction_Channel AS channel,
                COUNT(*) AS count
            FROM `Transaction`
            WHERE Disabled_Transaction = 0
            GROUP BY Transaction_Channel
            ORDER BY count DESC
            """;

        List<ChannelStats> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new ChannelStats(
                        rs.getString("channel"),
                        rs.getInt("count")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load channel stats: " + e.getMessage(), e);
        }
    }

    public static Map<String, Integer> loadCategoryStats() {
        String sql = """
            SELECT 
                Product_Category,
                COUNT(*) AS count
            FROM Product
            WHERE Disabled_Product = 0
            GROUP BY Product_Category
            """;

        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(rs.getString("Product_Category"), rs.getInt("count"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load category stats: " + e.getMessage(), e);
        }
    }

    public static Map<String, Integer> loadStatusStats() {
        String sql = """
            SELECT 
                Payment_Status,
                COUNT(*) AS count
            FROM `Transaction`
            WHERE Disabled_Transaction = 0
            GROUP BY Payment_Status
            """;

        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(rs.getString("Payment_Status"), rs.getInt("count"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load status stats: " + e.getMessage(), e);
        }
    }
}