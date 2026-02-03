package com.example.database.Orders;

import com.example.database.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
//
//    public static class Stats {
//        public final double totalRevenue;
//        public final double paidRevenue;
//        public Stats(double total, double paid) {
//            this.totalRevenue = total;
//            this.paidRevenue = paid;
//        }
//    }
//
//    public static class OrderStatusStats {
//        public final String status;
//        public final int count;
//        public OrderStatusStats(String status, int count) {
//            this.status = status;
//            this.count = count;
//        }
//    }
//
//    public static class OrdersOverTimeStats {
//        public final String month;
//        public final int count;
//        public OrdersOverTimeStats(String month, int count) {
//            this.month = month;
//            this.count = count;
//        }
//    }
//
//    public static Stats loadRevenueStats() {
//        String sql = "SELECT COALESCE(SUM(Cost), 0) AS TotalRev, " +
//                "COALESCE(SUM(CASE WHEN Payment_Status = 'Paid' THEN Amount_Paid ELSE 0 END), 0) AS PaidRev " +
//                "FROM `Transaction` WHERE Disabled_Transaction = 0";
//        try (Connection con = DBConnection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) return new Stats(rs.getDouble("TotalRev"), rs.getDouble("PaidRev"));
//            return new Stats(0, 0);
//        } catch (SQLException e) {
//            throw new RuntimeException("loadRevenueStats failed: " + e.getMessage(), e);
//        }
//    }
//
//    public static List<OrderStatusStats> loadOrderStatusStats() {
//        String sql = "SELECT Payment_Status, COUNT(*) AS OrderCount FROM `Transaction` " +
//                "WHERE Disabled_Transaction = 0 GROUP BY Payment_Status";
//        List<OrderStatusStats> list = new ArrayList<>();
//        try (Connection con = DBConnection.getConnection();
//             ResultSet rs = con.createStatement().executeQuery(sql)) {
//            while (rs.next()) list.add(new OrderStatusStats(rs.getString(1), rs.getInt(2)));
//        } catch (SQLException e) {
//            throw new RuntimeException("loadOrderStatusStats failed: " + e.getMessage(), e);
//        }
//        return list;
//    }
//
//    public static List<OrdersOverTimeStats> loadOrdersOverTimeStats() {
//        String sql = "SELECT DATE_FORMAT(Transaction_Date, '%Y-%m') AS Month, COUNT(*) AS OrderCount " +
//                "FROM `Transaction` WHERE Disabled_Transaction = 0 GROUP BY Month ORDER BY Month";
//        List<OrdersOverTimeStats> list = new ArrayList<>();
//        try (Connection con = DBConnection.getConnection();
//             ResultSet rs = con.createStatement().executeQuery(sql)) {
//            while (rs.next()) list.add(new OrdersOverTimeStats(rs.getString(1), rs.getInt(2)));
//        } catch (SQLException e) {
//            throw new RuntimeException("loadOrdersOverTimeStats failed: " + e.getMessage(), e);
//        }
//        return list;
//    }

    public static ArrayList<Order> getOrderList() {
        String sql = """
        SELECT t.*,
               r.Rental_Start_Date, r.Rental_End_Date, r.Return_Date,
               r.Rental_Status,
               s.Transaction_ID as Sale_ID
        FROM `Transaction` t
        LEFT JOIN Rental r ON t.Transaction_ID = r.Transaction_ID
        LEFT JOIN Sale s ON t.Transaction_ID = s.Transaction_ID
        WHERE t.Disabled_Transaction = 0
        ORDER BY t.Transaction_ID DESC""";

        ArrayList<Order> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int transactionId = rs.getInt("Transaction_ID");

                Date sqlDate = rs.getDate("Transaction_Date");
                LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : LocalDate.now();

                String channel = rs.getString("Transaction_Channel");
                String info = rs.getString("Transaction_Information");
                double cost = rs.getDouble("Cost");
                double amountPaid = rs.getDouble("Amount_Paid");
                String paymentStatus = rs.getString("Payment_Status");

                int custId = rs.getInt("Customer_ID");
                Integer customerId = rs.wasNull() ? null : custId;

                int pmId = rs.getInt("Payment_Method_Id");
                Integer paymentMethodId = rs.wasNull() ? null : pmId;

                boolean disabled = rs.getBoolean("Disabled_Transaction");

                int ppId = rs.getInt("Payment_Plan_ID");
                Integer paymentPlanId = rs.wasNull() ? null : ppId;

                String orderType = "Sale";
                LocalDate rentalStart = null, rentalEnd = null, returnDate = null;
                String rentalStatus = null;

                if (rs.getObject("Rental_Start_Date") != null) {
                    orderType = "Rental";

                    Date rsDate = rs.getDate("Rental_Start_Date");
                    rentalStart = rsDate != null ? rsDate.toLocalDate() : null;

                    Date reDate = rs.getDate("Rental_End_Date");
                    rentalEnd = reDate != null ? reDate.toLocalDate() : null;

                    Date rtDate = rs.getDate("Return_Date");
                    returnDate = rtDate != null ? rtDate.toLocalDate() : null;

                    rentalStatus = rs.getString("Rental_Status");
                }

                list.add(new Order(
                        transactionId, date, channel, info, cost, amountPaid, paymentStatus,
                        customerId, paymentMethodId, disabled, paymentPlanId, orderType,
                        rentalStart, rentalEnd, returnDate, rentalStatus
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getOrderList failed: " + e.getMessage(), e);
        }
    }

    public static List<OrderItem> getOrderItems(int orderId) {
        String sql = "SELECT ti.*, p.Product_Name FROM Transaction_Items ti " +
                "JOIN Product p ON p.Product_ID = ti.Product_ID WHERE ti.Transaction_ID = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                            rs.getInt("Transaction_ID"),
                            rs.getInt("Transaction_ID"),
                            rs.getInt("Product_ID"),
                            rs.getString("Product_Name"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Price_At_Time"),
                            rs.getBoolean("Is_Disabled")
                    ));
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("getOrderItems failed: " + e.getMessage(), e);
        }
    }

    public static int insertOrderWithItems(Order header, List<OrderItem> items) {
        String sqlOrder = """
            INSERT INTO `Transaction` (
            Transaction_Date, Transaction_Channel, Transaction_Information,
            Cost, Amount_Paid, Payment_Status, Customer_ID, Payment_Method_Id, 
            Disabled_Transaction, Payment_Plan_Id, Order_Type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)""";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(header.getDate()));
                ps.setString(2, header.getChannel());
                ps.setString(3, header.getInformation());
                ps.setDouble(4, header.getCost());
                ps.setDouble(5, header.getAmountPaid());
                ps.setString(6, header.getPaymentStatus());

                if (header.getCustomerId() != null) ps.setInt(7, header.getCustomerId());
                else ps.setNull(7, Types.INTEGER);

                if (header.getPaymentMethodId() != null) ps.setInt(8, header.getPaymentMethodId());
                else ps.setNull(8, Types.INTEGER);

                ps.setBoolean(9, header.isDisabled());
                ps.setInt(10, header.getPaymentPlanId() != null ? header.getPaymentPlanId() : 1);
                ps.setString(11, header.getOrderType());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("ID Generation Failed");
                int orderId = rs.getInt(1);

                if ("Rental".equals(header.getOrderType())) {
                    insertRentalDetails(con, orderId, header);
                } else {
                    insertSaleDetails(con, orderId);
                }

                if (items != null && !items.isEmpty()) {
                    saveItems(con, orderId, items);
                }

                return orderId;
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("insertOrderWithItems failed: " + e.getMessage(), e);
        }
    }

    private static void insertSaleDetails(Connection con, int transactionId) throws SQLException {
        String sql = "INSERT INTO Sale (Transaction_ID) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.executeUpdate();
        }
    }

    private static void insertRentalDetails(Connection con, int transactionId, Order order) throws SQLException {
        String sql = """
            INSERT INTO Rental 
            (Transaction_ID, Rental_Start_Date, Rental_End_Date, Return_Date, Rental_Status)
            VALUES (?, ?, ?, ?, ?)""";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.setDate(2, Date.valueOf(order.getRentalStartDate()));
            ps.setDate(3, Date.valueOf(order.getRentalEndDate()));

            if (order.getReturnDate() != null) ps.setDate(4, Date.valueOf(order.getReturnDate()));
            else ps.setNull(4, Types.DATE);

            ps.setString(5, order.getRentalStatus() != null ? order.getRentalStatus() : "Active");

            ps.executeUpdate();
        }
    }

    public static void updateOrderWithItems(Order header, List<OrderItem> items) {
        String sqlUpdate = """
            UPDATE `Transaction` SET 
            Transaction_Date=?, Transaction_Channel=?, Transaction_Information=?, 
            Cost=?, Amount_Paid=?, Payment_Status=?, Disabled_Transaction=?, 
            Customer_ID=?, Payment_Method_Id=?, Payment_Plan_Id=?, Order_Type=?
            WHERE Transaction_ID=?""";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setDate(1, Date.valueOf(header.getDate()));
                ps.setString(2, header.getChannel());
                ps.setString(3, header.getInformation());
                ps.setDouble(4, header.getCost());
                ps.setDouble(5, header.getAmountPaid());
                ps.setString(6, header.getPaymentStatus());
                ps.setBoolean(7, header.isDisabled());

                if (header.getCustomerId() != null) ps.setInt(8, header.getCustomerId());
                else ps.setNull(8, Types.INTEGER);

                if (header.getPaymentMethodId() != null) ps.setInt(9, header.getPaymentMethodId());
                else ps.setNull(9, Types.INTEGER);

                ps.setInt(10, header.getPaymentPlanId() != null ? header.getPaymentPlanId() : 1);
                ps.setString(11, header.getOrderType());
                ps.setInt(12, header.getId());

                ps.executeUpdate();

                deleteSaleDetails(con, header.getId());
                deleteRentalDetails(con, header.getId());

                if ("Rental".equals(header.getOrderType())) {
                    insertRentalDetails(con, header.getId(), header);
                } else {
                    insertSaleDetails(con, header.getId());
                }

                try (PreparedStatement del = con.prepareStatement(
                        "DELETE FROM Transaction_Items WHERE Transaction_ID=?")) {
                    del.setInt(1, header.getId());
                    del.executeUpdate();
                }

                if (items != null && !items.isEmpty()) {
                    saveItems(con, header.getId(), items);
                }

            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("updateOrderWithItems failed: " + e.getMessage(), e);
        }
    }

    private static void deleteSaleDetails(Connection con, int transactionId) throws SQLException {
        String sql = "DELETE FROM Sale WHERE Transaction_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.executeUpdate();
        }
    }

    private static void deleteRentalDetails(Connection con, int transactionId) throws SQLException {
        String sql = "DELETE FROM Rental WHERE Transaction_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            ps.executeUpdate();
        }
    }

    private static void saveItems(Connection con, int orderId, List<OrderItem> items) throws SQLException {
        String sqlItem = "INSERT INTO Transaction_Items " +
                "(Transaction_ID, Product_ID, Quantity, Price_At_Time, Is_Disabled) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
            for (OrderItem it : items) {
                psItem.setInt(1, orderId);
                psItem.setInt(2, it.getProductId());
                psItem.setInt(3, it.getQuantity());
                psItem.setDouble(4, it.getPriceAtTime());
                psItem.setBoolean(5, it.isDisabled());
                psItem.addBatch();
            }
            psItem.executeBatch();
        }
    }

    public static boolean deleteOrder(int orderId) {
        try (Connection con = DBConnection.getConnection()) {
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM Transaction_Items WHERE Transaction_ID=?")) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }

                deleteSaleDetails(con, orderId);
                deleteRentalDetails(con, orderId);

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM `Transaction` WHERE Transaction_ID=?")) {
                    ps.setInt(1, orderId);
                    int affected = ps.executeUpdate();
                    return affected > 0;
                }
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("deleteOrder failed: " + e.getMessage(), e);
        }
    }

    public static void toggleOrderStatus(int orderId, boolean disabled) {
        String sql = "UPDATE `Transaction` SET Disabled_Transaction = ? WHERE Transaction_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, disabled);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("toggleOrderStatus failed: " + e.getMessage(), e);
        }
    }
}