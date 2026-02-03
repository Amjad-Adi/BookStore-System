package com.example.database.Products;

import com.example.database.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.database.DBConnection.showError;

public class ProductDAO {

    public static int insertProduct(Product product) {
        String productSql = """
            INSERT INTO Product
            (Product_Name, Product_Description, Product_Category, Product_Company,
             Product_Price, Product_Stock, Disabled_Product)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getCompany());
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getStock());
            ps.setBoolean(7, product.isDisabled());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Insert product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1);

                    if ("Book".equalsIgnoreCase(product.getCategory())) {
                        insertBookData(con, productId, product);
                    } else if ("Stationery".equalsIgnoreCase(product.getCategory())) {
                        insertStationeryData(con, productId, product);
                    }

                    return productId;
                } else {
                    throw new SQLException("Insert product failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("insertProduct failed: " + e.getMessage(), e);
        }
    }

    private static void insertBookData(Connection con, int productId, Product product) throws SQLException {
        String sql = """
            INSERT INTO Book
            (Product_ID, Book_ISBN, Book_Author_First_Name, Book_Author_Last_Name,
             Book_Publication_Year, Book_Genre, Book_Language, Book_Edition, Book_Number_Of_Pages)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, product.getBookISBN());
            ps.setString(3, product.getBookAuthorFirstName());
            ps.setString(4, product.getBookAuthorLastName());

            if (product.getBookPublicationYear() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setObject(5, product.getBookPublicationYear());
            }

            ps.setString(6, product.getBookGenre());
            ps.setString(7, product.getBookLanguage());
            ps.setString(8, product.getBookEdition());

            if (product.getBookNumberOfPages() == null) {
                ps.setNull(9, Types.INTEGER);
            } else {
                ps.setInt(9, product.getBookNumberOfPages());
            }

            ps.executeUpdate();
        }
    }

    private static void insertStationeryData(Connection con, int productId, Product product) throws SQLException {
        String sql = """
            INSERT INTO Stationery_Item
            (Product_ID, Stationery_Color, Stationery_Material,
             Stationery_Dimensions, Stationery_Production_Year)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, product.getStationeryColor());
            ps.setString(3, product.getStationeryMaterial());
            ps.setString(4, product.getStationeryDimensions());
            if (product.getStationeryProductionYear() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setObject(5, product.getStationeryProductionYear());
            }
            ps.executeUpdate();
        }
    }


    public static boolean updateProduct(Product product) {
        String productSql = """
            UPDATE Product SET
                Product_Name = ?,
                Product_Description = ?,
                Product_Category = ?,
                Product_Company = ?,
                Product_Price = ?,
                Product_Stock = ?,
                Disabled_Product = ?
            WHERE Product_ID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(productSql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getCompany());
            ps.setDouble(5, product.getPrice());
            ps.setInt(6, product.getStock());
            ps.setBoolean(7, product.isDisabled());
            ps.setInt(8, product.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // we delete first because it maybe changed it's category
                deleteBookData(con, product.getId());
                deleteStationeryData(con, product.getId());

                if ("Book".equalsIgnoreCase(product.getCategory())) {
                    insertBookData(con, product.getId(), product);
                } else if ("Stationery".equalsIgnoreCase(product.getCategory())) {
                    insertStationeryData(con, product.getId(), product);
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("updateProduct failed: " + e.getMessage(), e);
        }
    }

    private static void deleteBookData(Connection con, int productId) throws SQLException {
        String sql = "DELETE FROM Book WHERE Product_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    private static void deleteStationeryData(Connection con, int productId) throws SQLException {
        String sql = "DELETE FROM Stationery_Item WHERE Product_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public static ArrayList<Product> getAllProducts() {
        String sql = """
        SELECT
        p.*,

        b.Book_ISBN              AS Book_ISBN,
        b.Book_Author_First_Name AS Book_Author_First_Name,
        b.Book_Author_Last_Name  AS Book_Author_Last_Name,
        b.Book_Publication_Year  AS Book_Publication_Year,
        b.Book_Genre             AS Book_Genre,
        b.Book_Language          AS Book_Language,
        b.Book_Edition           AS Book_Edition,
        b.Book_Number_Of_Pages   AS Book_Number_Of_Pages,

        s.Stationery_Color           AS Stationery_Color,
        s.Stationery_Material        AS Stationery_Material,
        s.Stationery_Dimensions      AS Stationery_Dimensions,
        s.Stationery_Production_Year AS Stationery_Production_Year

        FROM Product p
        LEFT JOIN Book b ON b.Product_ID = p.Product_ID
        LEFT JOIN Stationery_Item s ON s.Product_ID = p.Product_ID
        ORDER BY p.Product_ID
        """;


        ArrayList<Product> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("Product_ID"),
                        rs.getString("Product_Name"),
                        rs.getString("Product_Description"),
                        rs.getString("Product_Category"),
                        rs.getString("Product_Company"),
                        rs.getDouble("Product_Price"),
                        rs.getInt("Product_Stock"),
                        rs.getBoolean("Disabled_Product")
                );

                if ("Book".equalsIgnoreCase(p.getCategory())) {
                    p.setBookISBN(rs.getString("Book_ISBN"));
                    p.setBookAuthorFirstName(rs.getString("Book_Author_First_Name"));
                    p.setBookAuthorLastName(rs.getString("Book_Author_Last_Name"));

                    Object year = rs.getObject("Book_Publication_Year");
                    p.setBookPublicationYear((Object) year);

                    p.setBookGenre(rs.getString("Book_Genre"));
                    p.setBookLanguage(rs.getString("Book_Language"));
                    p.setBookEdition(rs.getString("Book_Edition"));

                    Integer pages = (Integer) rs.getObject("Book_Number_Of_Pages");
                    p.setBookNumberOfPages(pages);
                } else {
                    p.setStationeryColor(rs.getString("Stationery_Color"));
                    p.setStationeryMaterial(rs.getString("Stationery_Material"));
                    p.setStationeryDimensions(rs.getString("Stationery_Dimensions"));

                    Object sy = rs.getObject("Stationery_Production_Year");
                    p.setStationeryProductionYear(sy);
                }

                list.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("getAllProducts failed: " + e.getMessage(), e);
        }
        return list;
    }

    public static Product getProductById(int productId) {
        String sql = """
        SELECT
         p.*,

        b.Book_ISBN              AS Book_ISBN,
        b.Book_Author_First_Name AS Book_Author_First_Name,
        b.Book_Author_Last_Name  AS Book_Author_Last_Name,
        b.Book_Publication_Year  AS Book_Publication_Year,
        b.Book_Genre             AS Book_Genre,
        b.Book_Language          AS Book_Language,
        b.Book_Edition           AS Book_Edition,
        b.Book_Number_Of_Pages   AS Book_Number_Of_Pages,

        s.Stationery_Color           AS Stationery_Color,
        s.Stationery_Material        AS Stationery_Material,
        s.Stationery_Dimensions      AS Stationery_Dimensions,
        s.Stationery_Production_Year AS Stationery_Production_Year
        FROM Product p
        LEFT JOIN Book b ON b.Product_ID = p.Product_ID
        LEFT JOIN Stationery_Item s ON s.Product_ID = p.Product_ID
        WHERE p.Product_ID = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Product p = new Product(
                        rs.getInt("Product_ID"),
                        rs.getString("Product_Name"),
                        rs.getString("Product_Description"),
                        rs.getString("Product_Category"),
                        rs.getString("Product_Company"),
                        rs.getDouble("Product_Price"),
                        rs.getInt("Product_Stock"),
                        rs.getBoolean("Disabled_Product")
                );

                if ("Book".equalsIgnoreCase(p.getCategory())) {
                    p.setBookISBN(rs.getString("Book_ISBN"));
                    p.setBookAuthorFirstName(rs.getString("Book_Author_First_Name"));
                    p.setBookAuthorLastName(rs.getString("Book_Author_Last_Name"));

                    int year = rs.getInt("Book_Publication_Year");
                    p.setBookPublicationYear(rs.wasNull() ? null : year);

                    p.setBookGenre(rs.getString("Book_Genre"));
                    p.setBookLanguage(rs.getString("Book_Language"));
                    p.setBookEdition(rs.getString("Book_Edition"));

                    int pages = rs.getInt("Book_Number_Of_Pages");
                    p.setBookNumberOfPages(rs.wasNull() ? null : pages);

                } else {
                    p.setStationeryColor(rs.getString("Stationery_Color"));
                    p.setStationeryMaterial(rs.getString("Stationery_Material"));
                    p.setStationeryDimensions(rs.getString("Stationery_Dimensions"));

                    int sy = rs.getInt("Stationery_Production_Year");
                    p.setStationeryProductionYear(rs.wasNull() ? null : sy);
                }

                return p;
            }

        } catch (SQLException e) {
            throw new RuntimeException("getProductById failed: " + e.getMessage(), e);
        }
    }

    public static List<ProductWithBuyers> getAllProductsWithBuyersCount() {
        String sql = """
            SELECT
              p.Product_ID,
              p.Product_Name,
              p.Product_Description,
              p.Product_Category,
              p.Product_Company,
              p.Product_Price,
              p.Product_Stock,
              p.Disabled_Product,
              COUNT(DISTINCT t.Customer_ID) AS buyers
              FROM Product p
              LEFT JOIN Transaction_Items ti ON ti.Product_ID = p.Product_ID
              LEFT JOIN `Transaction` t ON t.Transaction_ID = ti.Transaction_ID
              GROUP BY p.Product_ID
              ORDER BY p.Product_ID
        """;

        List<ProductWithBuyers> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("Product_ID"),
                        rs.getString("Product_Name"),
                        rs.getString("Product_Description"),
                        rs.getString("Product_Category"),
                        rs.getString("Product_Company"),
                        rs.getDouble("Product_Price"),
                        rs.getInt("Product_Stock"),
                        rs.getBoolean("Disabled_Product")
                );

                int buyers = rs.getInt("buyers");
                list.add(new ProductWithBuyers(p, buyers));
            }

        } catch (SQLException e) {
            throw new RuntimeException("getAllProductsWithBuyersCount failed: " + e.getMessage(), e);
        }
        return list;
    }

    public static boolean updateProductStock(int productId, int newStock) {
        String sql = "UPDATE Product SET Product_Stock = ? WHERE Product_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Math.max(0, newStock));
            ps.setInt(2, productId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updateProductStock failed: " + e.getMessage(), e);
        }
    }

    public static boolean addReview(int customerId, int productId, int rating, String comment) {
        String sql = """
            INSERT INTO Review
            (Customer_ID, Product_ID, Rating, Comment, Review_Date)
            VALUES (?, ?, ?, ?, CURRENT_DATE)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.setInt(3, Math.max(1, Math.min(5, rating)));

            if (comment == null || comment.isBlank()) ps.setNull(4, Types.VARCHAR);
            else ps.setString(4, comment.trim());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("addReview failed: " + e.getMessage(), e);
        }
    }

    //
    public static ArrayList<Review> getReviewsForProduct(int productId) {
        String sql = """
            SELECT
              r.Review_Id,
              r.Customer_ID,
              r.Product_ID,
              CONCAT(c.Customer_First_Name,' ',c.Customer_Second_Name) AS customerName,
              r.Comment,
              r.Rating,
              r.Review_Date
            FROM Review r
            JOIN Customers c ON c.Customer_Id = r.Customer_ID
            WHERE r.Product_ID = ?
            ORDER BY r.Review_Id DESC
        """;

        ArrayList<Review> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("Review_Date");
                    LocalDate ld = (d == null) ? null : d.toLocalDate();

                    list.add(new Review(
                            rs.getInt("Review_Id"),
                            rs.getInt("Customer_ID"),
                            rs.getInt("Product_ID"),
                            rs.getString("customerName"),
                            rs.getString("Comment"),
                            rs.getInt("Rating"),
                            ld
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("getReviewsForProduct failed: " + e.getMessage(), e);
        }

        return list;
    }



    public static List<ProductWithBuyers> getTopPopularProducts(int limit) {
        String sql = """
        SELECT
          p.Product_ID,
          p.Product_Name,
          p.Product_Description,
          p.Product_Category,
          p.Product_Company,
          p.Product_Price,
          p.Product_Stock,
          p.Disabled_Product,
          COUNT(DISTINCT t.Customer_ID) AS buyers
        FROM Product p
        LEFT JOIN Transaction_Items ti ON ti.Product_ID = p.Product_ID
        LEFT JOIN `Transaction` t ON t.Transaction_ID = ti.Transaction_ID
        WHERE p.Product_Stock > 0
        GROUP BY p.Product_ID
        ORDER BY buyers DESC
        LIMIT ?
    """;

        List<ProductWithBuyers> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("Product_ID"),
                            rs.getString("Product_Name"),
                            rs.getString("Product_Description"),
                            rs.getString("Product_Category"),
                            rs.getString("Product_Company"),
                            rs.getDouble("Product_Price"),
                            rs.getInt("Product_Stock"),
                            rs.getBoolean("Disabled_Product")
                    );

                    int buyers = rs.getInt("buyers");
                    list.add(new ProductWithBuyers(p, buyers));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("getTopPopularProducts failed: " + e.getMessage(), e);
        }

        return list;
    }
}