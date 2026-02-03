package com.example.database.DashBoard;

import com.example.database.Orders.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label customersLabel;
    @FXML private Label ordersLabel;
    @FXML private Label revenueLabel;
    @FXML private Label statusLabel;
    @FXML private Label recentCountLabel;

    @FXML private TableView<Order> recentOrdersTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, LocalDate> colDate;
    @FXML private TableColumn<Order, String> colChannel;
    @FXML private TableColumn<Order, Double> colCost;
    @FXML private TableColumn<Order, Double> colPaid;
    @FXML private TableColumn<Order, String> colStatus;

    @FXML private BarChart<String, Number> revenueChart;
    @FXML private LineChart<String, Number> ordersLineChart;
    @FXML private PieChart categoryPieChart;
    @FXML private PieChart statusPieChart;
    @FXML private LineChart<String, Number> customerGrowthChart;
    @FXML private BarChart<String, Number> orderTypeChart;
    @FXML private AreaChart<String, Number> revenueMonthlyChart;
    @FXML private PieChart channelDistributionChart;

    private final ObservableList<Order> recent = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        recentOrdersTable.setItems(recent);
        loadAll();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colChannel.setCellValueFactory(new PropertyValueFactory<>("channel"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        colDate.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : df.format(item));
            }
        });

        colCost.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("$%.2f", item));
            }
        });

        colPaid.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("$%.2f", item));
            }
        });

        colStatus.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    setStyle("");
                    return;
                }
                setText(item);
                String base = "-fx-text-fill:#e5e7eb; -fx-font-weight:900;";
                String bg;
                switch (item) {
                    case "Paid" -> bg = "rgba(34,197,94,0.18)";
                    case "Pending" -> bg = "rgba(245,158,11,0.18)";
                    case "Failed" -> bg = "rgba(244,63,94,0.18)";
                    case "Refunded" -> bg = "rgba(56,189,248,0.18)";
                    default -> bg = "rgba(148,163,184,0.12)";
                }
                setStyle(base + " -fx-background-color:" + bg + "; -fx-background-radius:8; -fx-alignment:CENTER;");
            }
        });
    }

    @FXML
    private void onRefresh() {
        loadAll();
    }

    private void loadAll() {
        setStatus("Loading…");

        Task<Void> task = new Task<>() {
            DashboardDAO.Stats stats;
            List<Order> rec;
            List<DashboardDAO.RevenueByMethod> revenueByMethod;
            List<DashboardDAO.MonthlyRevenue> monthlyRevenue;
            List<DashboardDAO.CustomerGrowth> customerGrowth;
            List<DashboardDAO.OrderTypeStats> orderTypeStats;
            List<DashboardDAO.ChannelStats> channelStats;
            Map<String, Integer> categoryStats;
            Map<String, Integer> statusStats;

            @Override
            protected Void call() {
                stats = DashboardDAO.loadStats();
                rec = DashboardDAO.recentOrders(12);
                revenueByMethod = DashboardDAO.loadRevenueByPaymentMethod();
                monthlyRevenue = DashboardDAO.loadMonthlyRevenue();
                customerGrowth = DashboardDAO.loadCustomerGrowth();
                orderTypeStats = DashboardDAO.loadOrderTypeStats();
                channelStats = DashboardDAO.loadChannelStats();
                categoryStats = DashboardDAO.loadCategoryStats();
                statusStats = DashboardDAO.loadStatusStats();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    updateKPIs(stats);
                    updateRecentOrders(rec);
                    updateRevenueChart(revenueByMethod);
                    updateMonthlyRevenueChart(monthlyRevenue);
                    updateCustomerGrowthChart(customerGrowth);
                    updateOrderTypeChart(orderTypeStats);
                    updateChannelChart(channelStats);
                    updateCategoryPieChart(categoryStats);
                    updateStatusPieChart(statusStats);
                    setStatus("Up to date");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> setStatus("Failed to load dashboard"));
                getException().printStackTrace();
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void updateKPIs(DashboardDAO.Stats stats) {
        customersLabel.setText(String.valueOf(stats.customers));
        ordersLabel.setText(String.valueOf(stats.orders));
        revenueLabel.setText(String.format("$%.2f", stats.revenuePaid));
    }

    private void updateRecentOrders(List<Order> rec) {
        recent.setAll(rec);
        recentCountLabel.setText("Showing " + rec.size() + " latest");
    }

    private void updateRevenueChart(List<DashboardDAO.RevenueByMethod> data) {
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue by Payment Method");

        for (DashboardDAO.RevenueByMethod item : data) {
            series.getData().add(new XYChart.Data<>(item.methodName, item.revenue));
        }

        revenueChart.getData().add(series);
    }

    private void updateMonthlyRevenueChart(List<DashboardDAO.MonthlyRevenue> data) {
        if (revenueMonthlyChart == null) return;

        revenueMonthlyChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        for (DashboardDAO.MonthlyRevenue item : data) {
            series.getData().add(new XYChart.Data<>(item.month, item.revenue));
        }

        revenueMonthlyChart.getData().add(series);
    }

    private void updateCustomerGrowthChart(List<DashboardDAO.CustomerGrowth> data) {
        if (customerGrowthChart == null) return;

        customerGrowthChart.getData().clear();

        XYChart.Series<String, Number> registeredSeries = new XYChart.Series<>();
        registeredSeries.setName("Registered");

        XYChart.Series<String, Number> activatedSeries = new XYChart.Series<>();
        activatedSeries.setName("Activated");

        for (DashboardDAO.CustomerGrowth item : data) {
            registeredSeries.getData().add(new XYChart.Data<>(item.month, item.registered));
            activatedSeries.getData().add(new XYChart.Data<>(item.month, item.activated));
        }

        customerGrowthChart.getData().addAll(registeredSeries, activatedSeries);
    }

    private void updateOrderTypeChart(List<DashboardDAO.OrderTypeStats> data) {
        if (orderTypeChart == null) return;

        orderTypeChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders by Type");

        for (DashboardDAO.OrderTypeStats item : data) {
            series.getData().add(new XYChart.Data<>(item.orderType, item.count));
        }

        orderTypeChart.getData().add(series);
    }

    private void updateChannelChart(List<DashboardDAO.ChannelStats> data) {
        if (channelDistributionChart == null) return;

        channelDistributionChart.getData().clear();

        for (DashboardDAO.ChannelStats item : data) {
            PieChart.Data slice = new PieChart.Data(
                    item.channel + " (" + item.count + ")",
                    item.count
            );
            channelDistributionChart.getData().add(slice);
        }
    }

    private void updateCategoryPieChart(Map<String, Integer> data) {
        categoryPieChart.getData().clear();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + ")",
                    entry.getValue()
            );
            categoryPieChart.getData().add(slice);
        }
    }

    private void updateStatusPieChart(Map<String, Integer> data) {
        statusPieChart.getData().clear();

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    entry.getKey() + " (" + entry.getValue() + ")",
                    entry.getValue()
            );
            statusPieChart.getData().add(slice);
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}