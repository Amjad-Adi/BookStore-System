module com.example.database {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.database to javafx.fxml;
    opens com.example.database.DashBoard to javafx.fxml,javafx.base;
    opens com.example.database.Customer to javafx.fxml,javafx.base;
    opens com.example.database.Orders to javafx.fxml,javafx.base;
    opens com.example.database.Payment to javafx.fxml,javafx.base;
    opens com.example.database.Products to javafx.fxml,javafx.base;
    opens com.example.database.WareHouse to javafx.fxml,javafx.base;
    opens com.example.database.Staff to javafx.fxml,javafx.base;

    exports com.example.database;
}
