package com.example.database.Orders;

import com.example.database.Customer.Customer;
import com.example.database.Customer.CustomerDAO;
import com.example.database.DBConnection;
import com.example.database.Payment.PaymentMethod;
import com.example.database.Payment.PaymentMethodDAO;
import com.example.database.Payment.PaymentPlan;
import com.example.database.Payment.PaymentPlanDAO;
import com.example.database.Products.Product;
import com.example.database.Products.ProductDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class OrderController {

    @FXML private TextField idField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> channelCombo;
    @FXML private TextField costField;
    @FXML private TextField amountPaidField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea infoArea;
    @FXML private CheckBox isDisabledCheckBox;

    @FXML private ComboBox<PaymentMethod> paymentMethodCombo;
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<PaymentPlan> paymentPlanCombo;

    @FXML private RadioButton saleRadioButton;
    @FXML private RadioButton rentalRadioButton;
    @FXML private ToggleGroup orderTypeGroup;

    @FXML private DatePicker rentalStartDatePicker;
    @FXML private DatePicker rentalEndDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private ComboBox<String> rentalStatusCombo;
    @FXML private VBox rentalFieldsContainer;

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Boolean> statusColumn;
    @FXML private TableColumn<Order, Integer> idColumn;
    @FXML private TableColumn<Order, LocalDate> dateColumn;
    @FXML private TableColumn<Order, Double> costColumn;
    @FXML private TableColumn<Order, String> paymentStatusColumn;

    @FXML private TableView<OrderItem> orderItemTable;
    @FXML private TableColumn<OrderItem, Boolean> itemStatusColumn;
    @FXML private TableColumn<OrderItem, Integer> productIdColumn;
    @FXML private TableColumn<OrderItem, String> productNameColumn;
    @FXML private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML private TableColumn<OrderItem, Double> priceAtTimeColumn;
    @FXML private TableColumn<OrderItem, Double> lineTotalColumn;

    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityTextField;
    @FXML private Slider quantitySlider;
    @FXML private TextField priceAtTimeTextField;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Button addItemButton;
    @FXML private Button updateItemButton;
    @FXML private Button removeItemButton;
    @FXML private Label infoOrderId;
    @FXML private Label infoCustomer;
    @FXML private Label infoDate;
    @FXML private Label infoChannel;
    @FXML private Label infoPaymentMethod;
    @FXML private Label infoPaymentPlan;
    @FXML private Label infoPaymentStatus;
    @FXML private Label infoTotalCost;
    @FXML private Label infoAmountPaid;

    private final ObservableList<Order> masterList = observableArrayList();
    private final ObservableList<Order> filteredList = observableArrayList();
    private final ObservableList<OrderItem> currentOrderItems = observableArrayList();
    private final ObservableList<PaymentMethod> methodList = observableArrayList();
    private final ObservableList<PaymentPlan> paymentPlanList = observableArrayList();
    private final ObservableList<Product> productList = observableArrayList();
    private final ObservableList<Customer> customerList = observableArrayList();

    @FXML private Label statusLabel;
    @FXML private ComboBox<String> searchByCombo;
    @FXML private HBox searchFieldContainer;
    private TextField searchTextField;
    private DatePicker searchDatePicker;

    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT = "rgba(56,189,248,0.22)";

    @FXML
    public void initialize() {
        setupOrderTable();
        setupOrderItemTable();
        setupComboBoxes();
        setupSearchFields();
        loadData();
        setMode(Mode.VIEW);
        setupListeners();
    }

    private void setupOrderTable() {
        orderTable.setItems(filteredList);
        orderTable.setMinWidth(900);

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        orderTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setStyle("");
                    return;
                }
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (order.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: " + baseColor + ", " + SELECT_TINT + ";");
                } else {
                    setStyle("-fx-background-color: " + baseColor + ";");
                }
            }
        });
    }

    private void setupOrderItemTable() {
        orderItemTable.setItems(currentOrderItems);

        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        itemStatusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(itemStatusColumn));

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceAtTimeColumn.setCellValueFactory(new PropertyValueFactory<>("priceAtTime"));
        lineTotalColumn.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));

        orderItemTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(OrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setupComboBoxes() {
        channelCombo.setItems(observableArrayList("Online", "In store"));
        statusCombo.setItems(observableArrayList("Pending", "Paid", "Failed", "Refunded"));
        rentalStatusCombo.setItems(observableArrayList("Active", "Returned", "Late", "Cancelled"));

        searchByCombo.setItems(observableArrayList(
                "ID", "Date", "Channel", "Payment Status", "Customer ID", "Order Type"
        ));
        searchByCombo.getSelectionModel().select("ID");

        if (saleRadioButton != null) saleRadioButton.setSelected(true);

        paymentMethodCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (ID: " + item.getID() + ")");
            }
        });
        paymentMethodCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (ID: " + item.getID() + ")");
            }
        });

        paymentPlanCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(PaymentPlan item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String desc = item.getDescription();
                    setText(desc != null ? desc + " (" + item.getPeriodInMonths() + " months)"
                            : "Plan " + item.getId() + " (" + item.getPeriodInMonths() + " months)");
                }
            }
        });

        paymentPlanCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(PaymentPlan item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String desc = item.getDescription();
                    setText(desc != null ? desc + " (" + item.getPeriodInMonths() + " months)"
                            : "Plan " + item.getId() + " (" + item.getPeriodInMonths() + " months)");
                }
            }
        });

        customerCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFirstName() + " " + item.getSecondName() + " (ID: " + item.getId() + ")");
            }
        });
        customerCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFirstName() + " " + item.getSecondName() + " (ID: " + item.getId() + ")");
            }
        });

        productComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " ($" + item.getPrice() + ") - Stock: " + item.getStock());
            }
        });
        productComboBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " ($" + item.getPrice() + ") - Stock: " + item.getStock());
            }
        });
    }

    private void setupSearchFields() {
        searchTextField = new TextField();
        searchTextField.setPromptText("Search...");

        searchDatePicker = new DatePicker();
        searchDatePicker.setPromptText("Select date...");

        if (searchFieldContainer != null) {
            searchFieldContainer.getChildren().clear();
            searchFieldContainer.getChildren().add(searchTextField);
        }
    }

    private void setupListeners() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mode == Mode.VIEW) {
                fillForm(newVal);
                loadOrderItems(newVal.getId());
                updateInfoBar(newVal);
            }
        });

        orderItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillItemForm(newVal);
        });

        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (priceAtTimeTextField.isEditable()) {
                    priceAtTimeTextField.setText(String.valueOf(newVal.getPrice()));
                }
                quantitySlider.setMax(Math.max(1, newVal.getStock()));
            }
        });

        quantitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            quantityTextField.setText(String.valueOf(newVal.intValue()));
            updateTotalCost();
        });

        quantityTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.matches("\\d+")) {
                int value = Integer.parseInt(newVal);
                Product selected = productComboBox.getValue();
                int maxStock = selected != null ? selected.getStock() : 100;

                if (value < 0) value = 0;
                if (value > maxStock) value = maxStock;

                if (!String.valueOf(value).equals(newVal)) {
                    quantityTextField.setText(String.valueOf(value));
                    return;
                }
                quantitySlider.setValue(value);
            }
            updateTotalCost();
        });

        currentOrderItems.addListener((javafx.collections.ListChangeListener.Change<? extends OrderItem> c) -> updateTotalCost());

        if (orderTypeGroup != null) {
            orderTypeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
                filterCustomersByOrderType();
                toggleRentalFields();
            });
        }

        searchByCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (searchFieldContainer != null) {
                searchFieldContainer.getChildren().clear();
                if ("Date".equals(newVal)) {
                    searchFieldContainer.getChildren().add(searchDatePicker);
                } else {
                    searchFieldContainer.getChildren().add(searchTextField);
                }
            }
        });
    }

    private void toggleRentalFields() {
        boolean isRental = rentalRadioButton != null && rentalRadioButton.isSelected();
        if (rentalFieldsContainer != null) {
            rentalFieldsContainer.setVisible(isRental);
            rentalFieldsContainer.setManaged(isRental);
        }
    }

    private void filterCustomersByOrderType() {
        Customer currentSelection = customerCombo.getValue();

        if (saleRadioButton.isSelected()) {
            customerCombo.setItems(customerList);
        } else {
            ObservableList<Customer> activeCustomers = observableArrayList();
            LocalDate today = LocalDate.now();
            for (Customer c : customerList) {
                if (!c.isDisabled()
                        && c.getExpirationDate() != null
                        && !c.getExpirationDate().isBefore(today)) {
                    activeCustomers.add(c);
                }
            }
            customerCombo.setItems(activeCustomers);
        }

        if (currentSelection != null && customerCombo.getItems().contains(currentSelection)) {
            customerCombo.setValue(currentSelection);
        } else {
            customerCombo.getSelectionModel().clearSelection();
        }
    }

    private void loadData() {
        try {
            masterList.setAll(OrderDAO.getOrderList());
            filteredList.setAll(masterList);

            methodList.setAll(PaymentMethodDAO.getPaymentMethodList());
            paymentPlanList.setAll(PaymentPlanDAO.getPaymentPlanList());
            productList.setAll(ProductDAO.getAllProducts());
            customerList.setAll(CustomerDAO.getAllCustomers());

            paymentMethodCombo.setItems(methodList);
            paymentPlanCombo.setItems(paymentPlanList);
            productComboBox.setItems(productList);

            filterCustomersByOrderType();

            statusLabel.setText("Loaded " + masterList.size() + " orders.");
        } catch (Exception e) {
            DBConnection.showError("Load Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOrderItems(int orderId) {
        try {
            currentOrderItems.setAll(OrderDAO.getOrderItems(orderId));
            updateTotalCost();
        } catch (Exception e) {
            DBConnection.showError("Load Items Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateInfoBar(Order order) {
        if (infoOrderId != null) infoOrderId.setText("#" + order.getId());
        if (infoDate != null) infoDate.setText(order.getDate() == null ? "N/A" : order.getDate().toString());
        if (infoChannel != null) infoChannel.setText(order.getChannel());
        if (infoPaymentStatus != null) infoPaymentStatus.setText(order.getPaymentStatus());
        if (infoTotalCost != null) infoTotalCost.setText("$" + String.format("%.2f", order.getCost()));
        if (infoAmountPaid != null) infoAmountPaid.setText("$" + String.format("%.2f", order.getAmountPaid()));

        if (infoCustomer != null) {
            if (order.getCustomerId() != null) {
                Customer c = customerList.stream()
                        .filter(x -> x.getId() == order.getCustomerId())
                        .findFirst()
                        .orElse(null);
                infoCustomer.setText(c != null ? c.getFirstName() + " " + c.getSecondName() : "N/A");
            } else {
                infoCustomer.setText("N/A");
            }
        }

        if (infoPaymentMethod != null) {
            if (order.getPaymentMethodId() != null) {
                PaymentMethod pm = methodList.stream()
                        .filter(m -> m.getID() == order.getPaymentMethodId())
                        .findFirst()
                        .orElse(null);
                infoPaymentMethod.setText(pm != null ? pm.getName() : "N/A");
            } else {
                infoPaymentMethod.setText("N/A");
            }
        }

        if (infoPaymentPlan != null) {
            if (order.getPaymentPlanId() != null) {
                PaymentPlan pp = paymentPlanList.stream()
                        .filter(p -> p.getId() == order.getPaymentPlanId())
                        .findFirst()
                        .orElse(null);
                if (pp != null) {
                    String desc = pp.getDescription();
                    infoPaymentPlan.setText(desc != null ? desc : "Plan " + pp.getId());
                } else {
                    infoPaymentPlan.setText("N/A");
                }
            } else {
                infoPaymentPlan.setText("N/A");
            }
        }
    }

    private void fillForm(Order o) {
        idField.setText(String.valueOf(o.getId()));
        datePicker.setValue(o.getDate());
        channelCombo.setValue(o.getChannel());
        costField.setText(String.format("%.2f", o.getCost()));
        amountPaidField.setText(String.format("%.2f", o.getAmountPaid()));
        statusCombo.setValue(o.getPaymentStatus());
        infoArea.setText(o.getInformation() == null ? "" : o.getInformation());
        isDisabledCheckBox.setSelected(o.isDisabled());

        if ("Rental".equals(o.getOrderType())) {
            rentalRadioButton.setSelected(true);
            if (rentalStartDatePicker != null) rentalStartDatePicker.setValue(o.getRentalStartDate());
            if (rentalEndDatePicker != null) rentalEndDatePicker.setValue(o.getRentalEndDate());
            if (returnDatePicker != null) returnDatePicker.setValue(o.getReturnDate());
            if (rentalStatusCombo != null) rentalStatusCombo.setValue(o.getRentalStatus());
        } else {
            saleRadioButton.setSelected(true);
        }

        Integer pmId = o.getPaymentMethodId();
        if (pmId == null) {
            paymentMethodCombo.getSelectionModel().clearSelection();
        } else {
            PaymentMethod pm = methodList.stream()
                    .filter(m -> m != null && m.getID() == pmId)
                    .findFirst()
                    .orElse(null);
            paymentMethodCombo.getSelectionModel().select(pm);
        }

        Integer ppId = o.getPaymentPlanId();
        if (ppId == null) {
            paymentPlanCombo.getSelectionModel().clearSelection();
        } else {
            PaymentPlan pp = paymentPlanList.stream()
                    .filter(p -> p != null && p.getId() == ppId)
                    .findFirst()
                    .orElse(null);
            paymentPlanCombo.getSelectionModel().select(pp);
        }

        Integer cId = o.getCustomerId();
        if (cId == null) {
            customerCombo.getSelectionModel().clearSelection();
        } else {
            Customer c = customerList.stream()
                    .filter(x -> x != null && x.getId() == cId)
                    .findFirst()
                    .orElse(null);
            customerCombo.getSelectionModel().select(c);
        }
    }

    private void fillItemForm(OrderItem item) {
        Product p = productList.stream().filter(x -> x.getId() == item.getProductId()).findFirst().orElse(null);
        productComboBox.getSelectionModel().select(p);

        quantityTextField.setText(String.valueOf(item.getQuantity()));
        quantitySlider.setValue(item.getQuantity());
        priceAtTimeTextField.setText(String.format("%.2f", item.getPriceAtTime()));
    }

    private void updateTotalCost() {
        double total = currentOrderItems.stream()
                .filter(it -> !it.isDisabled())
                .mapToDouble(OrderItem::getLineTotal)
                .sum();
        costField.setText(String.format("%.2f", total));
    }

    @FXML
    private void onInsert() {
        clearForm();
        currentOrderItems.clear();
        setMode(Mode.INSERT);
        datePicker.setValue(LocalDate.now());
        if (rentalStartDatePicker != null) rentalStartDatePicker.setValue(LocalDate.now());
        statusLabel.setText("INSERT mode: fill fields and add items, then CONFIRM.");
    }

    @FXML
    private void onUpdate() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an order first.");
            return;
        }
        fillForm(selected);
        loadOrderItems(selected.getId());
        setMode(Mode.UPDATE);
        statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
    }

    @FXML
    private void onConfirm() {
        try {
            if (datePicker.getValue() == null) { statusLabel.setText("Date is required."); return; }
            if (channelCombo.getValue() == null || channelCombo.getValue().trim().isEmpty()) {
                statusLabel.setText("Channel is required."); return;
            }
            if (statusCombo.getValue() == null || statusCombo.getValue().trim().isEmpty()) {
                statusLabel.setText("Payment status is required."); return;
            }

            double cost = currentOrderItems.stream()
                    .filter(it -> !it.isDisabled())
                    .mapToDouble(OrderItem::getLineTotal)
                    .sum();

            double amountPaid;
            try { amountPaid = Double.parseDouble(amountPaidField.getText().trim()); }
            catch (Exception ex) { statusLabel.setText("Invalid amount paid."); return; }

            PaymentMethod pm = paymentMethodCombo.getValue();
            Integer paymentMethodId = (pm == null) ? null : pm.getID();

            PaymentPlan pp = paymentPlanCombo.getValue();
            Integer paymentPlanId = (pp == null) ? null : pp.getId();

            Customer c = customerCombo.getValue();
            Integer customerId = (c == null) ? null : c.getId();

            String orderType = saleRadioButton.isSelected() ? "Sale" : "Rental";

            LocalDate rentalStart = null, rentalEnd = null, returnDate = null;
            String rentalStatus = null;

            if ("Rental".equals(orderType)) {
                if (rentalStartDatePicker.getValue() == null || rentalEndDatePicker.getValue() == null) {
                    statusLabel.setText("Rental start and end dates are required.");
                    return;
                }
                rentalStart = rentalStartDatePicker.getValue();
                rentalEnd = rentalEndDatePicker.getValue();
                returnDate = returnDatePicker.getValue();
                rentalStatus = rentalStatusCombo.getValue();
            }

            Order order = new Order(
                    mode == Mode.INSERT ? 0 : Integer.parseInt(idField.getText()),
                    datePicker.getValue(),
                    channelCombo.getValue(),
                    (infoArea.getText() == null || infoArea.getText().trim().isEmpty()) ? null : infoArea.getText().trim(),
                    cost,
                    amountPaid,
                    statusCombo.getValue(),
                    customerId,
                    paymentMethodId,
                    isDisabledCheckBox.isSelected(),
                    paymentPlanId,
                    orderType,
                    rentalStart,
                    rentalEnd,
                    returnDate,
                    rentalStatus
            );

            if (mode == Mode.INSERT) {
                int newId = OrderDAO.insertOrderWithItems(order, new ArrayList<>(currentOrderItems));
                if (newId > 0) {
                    statusLabel.setText("Order inserted successfully with ID: " + newId);
                    loadData();
                    setMode(Mode.VIEW);
                }
            } else if (mode == Mode.UPDATE) {
                OrderDAO.updateOrderWithItems(order, new ArrayList<>(currentOrderItems));
                statusLabel.setText("Order updated successfully.");
                loadData();
                setMode(Mode.VIEW);
            }

        } catch (Exception e) {
            DBConnection.showError("Save Error", e.getMessage());
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        setMode(Mode.VIEW);
        statusLabel.setText("Canceled.");
    }

    @FXML
    private void onClear() {
        clearForm();
        currentOrderItems.clear();
        orderTable.getSelectionModel().clearSelection();
        setMode(Mode.VIEW);
        statusLabel.setText("");
        clearInfoBar();
    }

    private void clearInfoBar() {
        if (infoOrderId != null) infoOrderId.setText("-");
        if (infoCustomer != null) infoCustomer.setText("-");
        if (infoDate != null) infoDate.setText("-");
        if (infoChannel != null) infoChannel.setText("-");
        if (infoPaymentMethod != null) infoPaymentMethod.setText("-");
        if (infoPaymentPlan != null) infoPaymentPlan.setText("-");
        if (infoPaymentStatus != null) infoPaymentStatus.setText("-");
        if (infoTotalCost != null) infoTotalCost.setText("-");
        if (infoAmountPaid != null) infoAmountPaid.setText("-");
    }

    @FXML
    private void onAddItem() {
        try {
            Product selected = productComboBox.getValue();
            if (selected == null) { statusLabel.setText("Select a product first."); return; }

            int quantity = Integer.parseInt(quantityTextField.getText().trim());
            double price = Double.parseDouble(priceAtTimeTextField.getText().trim());

            if (quantity <= 0) { statusLabel.setText("Quantity must be positive."); return; }

            if (quantity > selected.getStock()) {
                statusLabel.setText("Quantity exceeds available stock (" + selected.getStock() + ").");
                return;
            }

            OrderItem newItem = new OrderItem(selected.getId(), selected.getName(), quantity, price);
            currentOrderItems.add(newItem);

            clearItemForm();
            statusLabel.setText("Item added.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity or price.");
        }
    }

    @FXML
    private void onUpdateItem() {
        OrderItem selected = orderItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select an item to update."); return; }

        try {
            int quantity = Integer.parseInt(quantityTextField.getText().trim());
            double price = Double.parseDouble(priceAtTimeTextField.getText().trim());

            Product p = productList.stream().filter(x -> x.getId() == selected.getProductId()).findFirst().orElse(null);
            if (p != null && quantity > p.getStock()) {
                statusLabel.setText("Quantity exceeds available stock (" + p.getStock() + ").");
                return;
            }

            selected.setQuantity(quantity);
            selected.setPriceAtTime(price);

            orderItemTable.refresh();
            updateTotalCost();
            statusLabel.setText("Item updated.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity or price.");
        }
    }

    @FXML
    private void onRemoveItem() {
        OrderItem selected = orderItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select an item to remove."); return; }

        currentOrderItems.remove(selected);
        clearItemForm();
        statusLabel.setText("Item removed.");
    }

    @FXML
    private void onToggleItemStatus() {
        OrderItem selected = orderItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select an item to toggle status."); return; }

        selected.setDisabled(!selected.isDisabled());
        orderItemTable.refresh();
        updateTotalCost();
        statusLabel.setText("Item " + (selected.isDisabled() ? "disabled" : "enabled") + ".");
    }

    @FXML
    private void onDisableOrder() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an order first.");
            return;
        }

        try {
            boolean newStatus = !selected.isDisabled();
            OrderDAO.toggleOrderStatus(selected.getId(), newStatus);

            selected.setDisabled(newStatus);
            orderTable.refresh();
            isDisabledCheckBox.setSelected(newStatus);

            statusLabel.setText("Order " + (newStatus ? "disabled" : "enabled") + " successfully.");
        } catch (Exception e) {
            DBConnection.showError("Toggle Status Error", e.getMessage());
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearch() {
        String searchBy = searchByCombo.getValue();
        String key = "";
        LocalDate searchDate = null;

        if ("Date".equals(searchBy)) {
            searchDate = searchDatePicker.getValue();
            if (searchDate == null) {
                filteredList.setAll(masterList);
                statusLabel.setText("Showing all orders.");
                return;
            }
        } else {
            key = searchTextField.getText() == null ? "" : searchTextField.getText().trim();
            if (key.isEmpty()) {
                filteredList.setAll(masterList);
                statusLabel.setText("Showing all orders.");
                return;
            }
        }

        List<Order> out = new ArrayList<>();
        for (Order o : masterList) {
            boolean match = false;
            switch (searchBy) {
                case "ID":
                    match = key.matches("\\d+") && o.getId() == Integer.parseInt(key);
                    break;
                case "Date":
                    match = o.getDate() != null && o.getDate().equals(searchDate);
                    break;
                case "Channel":
                    match = o.getChannel() != null && o.getChannel().equalsIgnoreCase(key);
                    break;
                case "Payment Status":
                    match = o.getPaymentStatus() != null && o.getPaymentStatus().equalsIgnoreCase(key);
                    break;
                case "Customer ID":
                    match = key.matches("\\d+") && o.getCustomerId() != null && o.getCustomerId() == Integer.parseInt(key);
                    break;
                case "Order Type":
                    match = o.getOrderType() != null && o.getOrderType().equalsIgnoreCase(key);
                    break;
            }
            if (match) out.add(o);
        }

        filteredList.setAll(out);
        statusLabel.setText("Found " + out.size() + " orders.");
    }

    @FXML
    private void onRefresh() {
        loadData();
        setMode(Mode.VIEW);
        statusLabel.setText("Data refreshed.");
    }

    private void clearForm() {
        idField.clear();
        costField.clear();
        amountPaidField.clear();
        infoArea.clear();
        datePicker.setValue(null);

        channelCombo.getSelectionModel().clearSelection();
        statusCombo.getSelectionModel().clearSelection();
        paymentMethodCombo.getSelectionModel().clearSelection();
        customerCombo.getSelectionModel().clearSelection();
        paymentPlanCombo.getSelectionModel().clearSelection();

        isDisabledCheckBox.setSelected(false);

        if (rentalStartDatePicker != null) rentalStartDatePicker.setValue(null);
        if (rentalEndDatePicker != null) rentalEndDatePicker.setValue(null);
        if (returnDatePicker != null) returnDatePicker.setValue(null);
        if (rentalStatusCombo != null) rentalStatusCombo.getSelectionModel().clearSelection();
    }

    private void clearItemForm() {
        productComboBox.getSelectionModel().clearSelection();
        quantityTextField.clear();
        quantitySlider.setValue(1);
        priceAtTimeTextField.clear();
        orderItemTable.getSelectionModel().clearSelection();
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        boolean editable = (mode != Mode.VIEW);

        idField.setEditable(false);
        costField.setEditable(false);

        amountPaidField.setEditable(editable);
        infoArea.setEditable(editable);
        isDisabledCheckBox.setVisible(!(mode==Mode.INSERT));
        isDisabledCheckBox.setManaged(!(mode == Mode.INSERT));
        datePicker.setDisable(!editable);
        channelCombo.setDisable(!editable);
        statusCombo.setDisable(!editable);
        paymentMethodCombo.setDisable(!editable);
        customerCombo.setDisable(!editable);
        paymentPlanCombo.setDisable(!editable);
        isDisabledCheckBox.setDisable(!editable);

        saleRadioButton.setDisable(!editable);
        rentalRadioButton.setDisable(!editable);

        if (rentalStartDatePicker != null) rentalStartDatePicker.setDisable(!editable);
        if (rentalEndDatePicker != null) rentalEndDatePicker.setDisable(!editable);
        if (returnDatePicker != null) returnDatePicker.setDisable(!editable);
        if (rentalStatusCombo != null) rentalStatusCombo.setDisable(!editable);

        productComboBox.setDisable(!editable);
        quantityTextField.setEditable(editable);
        quantitySlider.setDisable(!editable);
        priceAtTimeTextField.setEditable(editable);

        confirmButton.setVisible(editable);
        confirmButton.setManaged(editable);
        cancelButton.setVisible(editable);
        cancelButton.setManaged(editable);

        addItemButton.setDisable(!editable);
        updateItemButton.setDisable(!editable);
        removeItemButton.setDisable(!editable);
    }
}