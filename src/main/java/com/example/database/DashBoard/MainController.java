package com.example.database.DashBoard;

import com.example.database.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;

public class MainController {

    @FXML private ToggleGroup navGroup;
    @FXML private ToggleButton navDashboard, navCustomers, navOrders, navProducts,
            navSuppliers, navWarehouses, navPaymentPlans,
            navPaymentMethods, navStaff;

    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubLabel;
    @FXML private Label globalStatusLabel;
    @FXML private Label usernameLabel;
    @FXML private StackPane contentPane;

    private String currentFxml  = "Dashboard.fxml";
    private String currentTitle = "Dashboard";
    private String currentSub   = "Overview of your system";

    private static class NavItem {
        final String fxml, title, sub;
        NavItem(String fxml, String title, String sub) {
            this.fxml = fxml;
            this.title = title;
            this.sub = sub;
        }
    }

    @FXML
    public void initialize() {
        bind(navDashboard,      "Dashboard.fxml",           "Dashboard",       "Overview of your system");
        bind(navCustomers,      "customers.fxml",      "Customers",       "Manage customers");
        bind(navOrders,         "orders-view.fxml",    "Orders",          "Manage transactions");
        bind(navProducts,       "products-view.fxml",  "Products",        "Manage product catalog");
        bind(navWarehouses,     "warehouse-view.fxml", "Warehouses",      "Manage storage");
        bind(navPaymentPlans,   "payment_plan.fxml",   "Payment Plans",   "Manage payment plans");
        bind(navPaymentMethods, "payment_method.fxml", "Payment Methods", "Manage Payment Methods");
        bind(navStaff,          "staff-view.fxml",     "Staff",           "Manage staff");

        usernameLabel.setText("Admin");
        navGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) {
                if (oldT != null) oldT.setSelected(true);
                return;
            }
            NavItem item = (NavItem) newT.getUserData();
            open(item.fxml, item.title, item.sub);
        });

        navDashboard.setSelected(true);
    }

    private void bind(ToggleButton btn, String fxml, String title, String sub) {
        if (btn == null) return;
        btn.setUserData(new NavItem(fxml, title, sub));
    }

    private void open(String fxml, String title, String sub) {
        try {
            SceneUtil.loadInto(contentPane, fxml);

            currentFxml = fxml;
            currentTitle = title;
            currentSub = sub;

            pageTitleLabel.setText(title);
            pageSubLabel.setText(sub);
            setGlobalStatus("Loaded: " + title);
        } catch (Exception ex) {
            setGlobalStatus("Error loading: " + fxml);
        }
    }

    @FXML
    private void onRefreshCurrent() {
        open(currentFxml, currentTitle, currentSub);
    }

    @FXML
    private void onLogout(ActionEvent event) {
        SceneUtil.switchTo(event, "login.fxml", "Login");
    }

    private void setGlobalStatus(String msg) {
        if (globalStatusLabel != null) globalStatusLabel.setText(msg);
    }
}