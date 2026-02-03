package com.example.database.WareHouse;

import com.example.database.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAO {

    public static class WarehouseStats {
        public final int totalWarehouses;
        public final int totalCapacity;
        public final int totalUsed;

        public WarehouseStats(int total, int capacity, int used) {
            this.totalWarehouses = total;
            this.totalCapacity = capacity;
            this.totalUsed = used;
        }
    }

    public static WarehouseStats loadWarehouseStats() {
        String sql = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(Warehouse_Max_Storage), 0) AS capacity,
                COALESCE(SUM(Warehouse_Current_Storage), 0) AS used
            FROM Warehouse
            WHERE Disabled_Warehouse = 0
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new WarehouseStats(
                        rs.getInt("total"),
                        rs.getInt("capacity"),
                        rs.getInt("used")
                );
            }
            return new WarehouseStats(0, 0, 0);

        } catch (SQLException e) {
            throw new RuntimeException("loadWarehouseStats failed: " + e.getMessage(), e);
        }
    }

    public static ArrayList<Warehouse> getWarehouseList() {
        String sql = """
            SELECT
                Warehouse_ID,
                Warehouse_Name,
                Warehouse_Address,
                WarehouseDate_Of_Establishment,
                Warehouse_Max_Storage,
                Warehouse_Current_Storage,
                Disabled_Warehouse
            FROM Warehouse
            ORDER BY Warehouse_ID DESC
        """;

        ArrayList<Warehouse> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("WarehouseDate_Of_Establishment");
                LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : LocalDate.now();

                list.add(new Warehouse(
                        rs.getInt("Warehouse_ID"),
                        rs.getString("Warehouse_Name"),
                        rs.getString("Warehouse_Address"),
                        date,
                        rs.getInt("Warehouse_Max_Storage"),
                        rs.getInt("Warehouse_Current_Storage"),
                        rs.getBoolean("Disabled_Warehouse")
                ));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getWarehouseList failed: " + e.getMessage(), e);
        }
    }

    public static List<InventoryItem> getInventoryItems(int warehouseId) {
        String sql = """
            SELECT
                i.Warehouse_ID,
                i.Product_ID,
                p.Product_Name,
                i.Quantity_In_Warehouse,
                i.Is_Disabled
            FROM Inventory i
            JOIN Product p ON p.Product_ID = i.Product_ID
            WHERE i.Warehouse_ID = ?
        """;

        List<InventoryItem> items = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, warehouseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new InventoryItem(
                            0,
                            rs.getInt("Warehouse_ID"),
                            rs.getInt("Product_ID"),
                            rs.getString("Product_Name"),
                            rs.getInt("Quantity_In_Warehouse"),
                            rs.getBoolean("Is_Disabled")
                    ));
                }
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("getInventoryItems failed: " + e.getMessage(), e);
        }
    }

    public static List<WarehouseContact> getWarehouseContacts(int warehouseId) {
        String sql = """
            SELECT
                Contact_ID,
                Warehouse_ID,
                Contact_Number
            FROM Warehouse_Contact
            WHERE Warehouse_ID = ?
            ORDER BY Contact_ID
        """;

        List<WarehouseContact> contacts = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, warehouseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new WarehouseContact(
                            rs.getInt("Contact_ID"),
                            rs.getInt("Warehouse_ID"),
                            rs.getString("Contact_Number")
                    ));
                }
            }

            return contacts;

        } catch (SQLException e) {
            throw new RuntimeException("getWarehouseContacts failed: " + e.getMessage(), e);
        }
    }

    public static int insertWarehouseWithInventory(Warehouse warehouse, List<InventoryItem> items, List<WarehouseContact> contacts) {
        String sqlWarehouse = """
            INSERT INTO Warehouse (
                Warehouse_Name,
                Warehouse_Address,
                WarehouseDate_Of_Establishment,
                Warehouse_Max_Storage,
                Warehouse_Current_Storage,
                Disabled_Warehouse
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(sqlWarehouse, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, warehouse.getName());
                ps.setString(2, warehouse.getAddress());
                ps.setDate(3, Date.valueOf(warehouse.getDateOfEstablishment()));
                ps.setInt(4, warehouse.getMaxStorage());
                ps.setInt(5, warehouse.getCurrentStorage());
                ps.setBoolean(6, warehouse.isDisabled());

                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();

                if (!rs.next()) {
                    throw new SQLException("Warehouse ID generation failed");
                }

                int warehouseId = rs.getInt(1);

                if (items != null && !items.isEmpty()) {
                    saveInventoryItems(con, warehouseId, items);
                    updateProductStockFromInventory(con, items, true);
                }

                if (contacts != null && !contacts.isEmpty()) {
                    saveWarehouseContacts(con, warehouseId, contacts);
                }

                return warehouseId;

            } catch (SQLException e) {
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("insertWarehouseWithInventory failed: " + e.getMessage(), e);
        }
    }

    public static void updateWarehouseWithInventory(Warehouse warehouse, List<InventoryItem> items, List<WarehouseContact> contacts) {
        String sqlUpdate = """
            UPDATE Warehouse SET
                Warehouse_Name = ?,
                Warehouse_Address = ?,
                WarehouseDate_Of_Establishment = ?,
                Warehouse_Max_Storage = ?,
                Warehouse_Current_Storage = ?,
                Disabled_Warehouse = ?
            WHERE Warehouse_ID = ?
        """;

        try (Connection con = DBConnection.getConnection()) {

            List<InventoryItem> oldItems = getInventoryItems(warehouse.getId());

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, warehouse.getName());
                ps.setString(2, warehouse.getAddress());
                ps.setDate(3, Date.valueOf(warehouse.getDateOfEstablishment()));
                ps.setInt(4, warehouse.getMaxStorage());
                ps.setInt(5, warehouse.getCurrentStorage());
                ps.setBoolean(6, warehouse.isDisabled());
                ps.setInt(7, warehouse.getId());

                ps.executeUpdate();

                updateProductStockFromInventory(con, oldItems, false);

                try (PreparedStatement del = con.prepareStatement(
                        "DELETE FROM Inventory WHERE Warehouse_ID = ?")) {
                    del.setInt(1, warehouse.getId());
                    del.executeUpdate();
                }

                if (items != null && !items.isEmpty()) {
                    saveInventoryItems(con, warehouse.getId(), items);
                    updateProductStockFromInventory(con, items, true);
                }

                try (PreparedStatement delContacts = con.prepareStatement(
                        "DELETE FROM Warehouse_Contact WHERE Warehouse_ID = ?")) {
                    delContacts.setInt(1, warehouse.getId());
                    delContacts.executeUpdate();
                }

                if (contacts != null && !contacts.isEmpty()) {
                    saveWarehouseContacts(con, warehouse.getId(), contacts);
                }

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateWarehouseWithInventory failed: " + e.getMessage(), e);
        }
    }

    private static void saveInventoryItems(Connection con, int warehouseId, List<InventoryItem> items) throws SQLException {
        String sqlItem = """
            INSERT INTO Inventory (
                Warehouse_ID,
                Product_ID,
                Quantity_In_Warehouse,
                Is_Disabled
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
            for (InventoryItem item : items) {
                psItem.setInt(1, warehouseId);
                psItem.setInt(2, item.getProductId());
                psItem.setInt(3, item.getQuantityInWarehouse());
                psItem.setBoolean(4, item.isDisabled());
                psItem.addBatch();
            }
            psItem.executeBatch();
        }
    }

    private static void saveWarehouseContacts(Connection con, int warehouseId, List<WarehouseContact> contacts) throws SQLException {
        String sqlContact = """
            INSERT INTO Warehouse_Contact (
                Warehouse_ID,
                Contact_Number
            ) VALUES (?, ?)
        """;

        try (PreparedStatement psContact = con.prepareStatement(sqlContact)) {
            for (WarehouseContact contact : contacts) {
                psContact.setInt(1, warehouseId);
                psContact.setString(2, contact.getContactNumber());
                psContact.addBatch();
            }
            psContact.executeBatch();
        }
    }

    private static void updateProductStockFromInventory(Connection con, List<InventoryItem> items, boolean add) throws SQLException {
        String sql = """
            UPDATE Product
            SET Product_Stock = Product_Stock + ?
            WHERE Product_ID = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (InventoryItem item : items) {
                if (!item.isDisabled()) {
                    int delta = add ? item.getQuantityInWarehouse() : -item.getQuantityInWarehouse();
                    ps.setInt(1, delta);
                    ps.setInt(2, item.getProductId());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public static boolean deleteWarehouse(int warehouseId) {
        try (Connection con = DBConnection.getConnection()) {

            List<InventoryItem> items = getInventoryItems(warehouseId);
            updateProductStockFromInventory(con, items, false);

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Inventory WHERE Warehouse_ID = ?")) {
                ps.setInt(1, warehouseId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Warehouse_Contact WHERE Warehouse_ID = ?")) {
                ps.setInt(1, warehouseId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM Warehouse WHERE Warehouse_ID = ?")) {
                ps.setInt(1, warehouseId);
                int affected = ps.executeUpdate();
                return affected > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("deleteWarehouse failed: " + e.getMessage(), e);
        }
    }

    public static void toggleWarehouseStatus(int warehouseId, boolean disabled) {
        String sql = "UPDATE Warehouse SET Disabled_Warehouse = ? WHERE Warehouse_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, disabled);
            ps.setInt(2, warehouseId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("toggleWarehouseStatus failed: " + e.getMessage(), e);
        }
    }
}