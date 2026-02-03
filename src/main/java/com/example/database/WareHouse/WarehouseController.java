package com.example.database.WareHouse;
import com.example.database.WareHouse.WarehouseDAO;
import com.example.database.DBConnection;
import com.example.database.Products.Product;
import com.example.database.Products.ProductDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.collections.FXCollections.observableArrayList;

public class WarehouseController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private DatePicker dateOfEstablishmentPicker;
    @FXML private TextField maxStorageField;
    @FXML private TextField currentStorageField;
    @FXML private CheckBox isDisabledCheckBox;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private TableView<Warehouse> warehouseTable;
    @FXML private TableColumn<Warehouse, Boolean> statusColumn;
    @FXML private TableColumn<Warehouse, Integer> idColumn;
    @FXML private TableColumn<Warehouse, String> nameColumn;
    @FXML private TableColumn<Warehouse, String> addressColumn;
    @FXML private TableColumn<Warehouse, LocalDate> dateColumn;
    @FXML private TableColumn<Warehouse, Integer> maxStorageColumn;
    @FXML private TableColumn<Warehouse, Integer> currentStorageColumn;

    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, Boolean> itemStatusColumn;
    @FXML private TableColumn<InventoryItem, Integer> productIdColumn;
    @FXML private TableColumn<InventoryItem, String> productNameColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;

    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityTextField;
    @FXML private Slider quantitySlider;

    @FXML private Button addItemButton;
    @FXML private Button updateItemButton;
    @FXML private Button removeItemButton;

    @FXML private TableView<WarehouseContact> contactTable;
    @FXML private TableColumn<WarehouseContact, Integer> contactIdColumn;
    @FXML private TableColumn<WarehouseContact, String> contactNumberColumn;

    @FXML private TextField contactNumberField;
    @FXML private Button addContactButton;
    @FXML private Button updateContactButton;
    @FXML private Button removeContactButton;

    @FXML private Label statusLabel;
    @FXML private ComboBox<String> searchByCombo;
    @FXML private HBox searchFieldContainer;
    private TextField searchTextField;
    private DatePicker searchDatePicker;

    @FXML private Label infoWarehouseId;
    @FXML private Label infoWarehouseName;
    @FXML private Label infoMaxStorage;
    @FXML private Label infoCurrentStorage;
    @FXML private Label infoCapacityPercentage;

    private final ObservableList<Warehouse> masterList = observableArrayList();
    private final ObservableList<Warehouse> filteredList = observableArrayList();
    private final ObservableList<InventoryItem> currentInventoryItems = observableArrayList();
    private final ObservableList<WarehouseContact> currentContacts = observableArrayList();
    private final ObservableList<Product> productList = observableArrayList();

    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT = "rgba(56,189,248,0.22)";

    @FXML
    public void initialize() {
        setupWarehouseTable();
        setupInventoryTable();
        setupContactTable();
        setupComboBoxes();
        setupSearchFields();
        loadData();
        setMode(Mode.VIEW);
        setupListeners();
        setUpPhoneField(contactNumberField);
        setUpPhoneField(contactNumberField);
    }
    private void setUpPhoneField(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("\\+?\\d*")) return null;
            int digits = newText.startsWith("+") ? newText.length() - 1 : newText.length();
            return (digits <= 15 ? change : null);
        }));
    }

    private void setupWarehouseTable() {
        warehouseTable.setItems(filteredList);
        warehouseTable.setMinWidth(600);

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfEstablishment"));
        maxStorageColumn.setCellValueFactory(new PropertyValueFactory<>("maxStorage"));
        currentStorageColumn.setCellValueFactory(new PropertyValueFactory<>("currentStorage"));

        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        warehouseTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Warehouse warehouse, boolean empty) {
                super.updateItem(warehouse, empty);
                if (empty || warehouse == null) {
                    setStyle("");
                    return;
                }
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (warehouse.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: " + baseColor + ", " + SELECT_TINT + ";");
                } else {
                    setStyle("-fx-background-color: " + baseColor + ";");
                }
            }
        });
    }

    private void setupInventoryTable() {
        inventoryTable.setItems(currentInventoryItems);

        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInWarehouse"));

        inventoryTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
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

    private void setupContactTable() {
        contactTable.setItems(currentContacts);

        contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        contactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        contactTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(WarehouseContact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setStyle("");
                } else {
                    String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + baseColor + ", " + SELECT_TINT + ";");
                    } else {
                        setStyle("-fx-background-color: " + baseColor + ";");
                    }
                }
            }
        });
    }

    private void setupComboBoxes() {
        searchByCombo.setItems(observableArrayList("ID", "Name", "Address", "Date"));
        searchByCombo.getSelectionModel().select("ID");

        productComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " ($" + item.getPrice() + ") - Stock: " + item.getStock());
            }
        });
        productComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
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
        warehouseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mode == Mode.VIEW) {
                fillForm(newVal);
                loadInventoryItems(newVal.getId());
                loadWarehouseContacts(newVal.getId());
                updateInfoBar(newVal);
            }
        });

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillItemForm(newVal);
        });

        contactTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillContactForm(newVal);
        });

        productComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                quantitySlider.setMax(Math.max(1, 1000));
            }
        });

        quantitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            quantityTextField.setText(String.valueOf(newVal.intValue()));
            updateCurrentStorage();
        });

        quantityTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.matches("\\d+")) {
                int value = Integer.parseInt(newVal);
                if (value < 0) value = 0;
                if (value > 1000) value = 1000;

                if (!String.valueOf(value).equals(newVal)) {
                    quantityTextField.setText(String.valueOf(value));
                    return;
                }
                quantitySlider.setValue(value);
            }
            updateCurrentStorage();
        });

        currentInventoryItems.addListener((javafx.collections.ListChangeListener.Change<? extends InventoryItem> c) -> updateCurrentStorage());

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

    private void loadData() {
        try {
            masterList.setAll(WarehouseDAO.getWarehouseList());
            filteredList.setAll(masterList);

            productList.setAll(ProductDAO.getAllProducts());
            productComboBox.setItems(productList);

            statusLabel.setText("Loaded " + masterList.size() + " warehouses.");
        } catch (Exception e) {
            DBConnection.showError("Load Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadInventoryItems(int warehouseId) {
        try {
            currentInventoryItems.setAll(WarehouseDAO.getInventoryItems(warehouseId));
            updateCurrentStorage();
        } catch (Exception e) {
            DBConnection.showError("Load Items Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadWarehouseContacts(int warehouseId) {
        try {
            currentContacts.setAll(WarehouseDAO.getWarehouseContacts(warehouseId));
        } catch (Exception e) {
            DBConnection.showError("Load Contacts Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateInfoBar(Warehouse warehouse) {
        if (infoWarehouseId != null) infoWarehouseId.setText("#" + warehouse.getId());
        if (infoWarehouseName != null) infoWarehouseName.setText(warehouse.getName());
        if (infoMaxStorage != null) infoMaxStorage.setText(String.valueOf(warehouse.getMaxStorage()));
        if (infoCurrentStorage != null) infoCurrentStorage.setText(String.valueOf(warehouse.getCurrentStorage()));
        if (infoCapacityPercentage != null) {
            double percentage = warehouse.getCapacityPercentage();
            infoCapacityPercentage.setText(String.format("%.1f%%", percentage));
        }
    }

    private void fillForm(Warehouse w) {
        idField.setText(String.valueOf(w.getId()));
        nameField.setText(w.getName() == null ? "" : w.getName());
        addressField.setText(w.getAddress() == null ? "" : w.getAddress());
        dateOfEstablishmentPicker.setValue(w.getDateOfEstablishment());
        maxStorageField.setText(String.valueOf(w.getMaxStorage()));
        currentStorageField.setText(String.valueOf(w.getCurrentStorage()));
        isDisabledCheckBox.setSelected(w.isDisabled());
    }

    private void fillItemForm(InventoryItem item) {
        Product p = productList.stream().filter(x -> x.getId() == item.getProductId()).findFirst().orElse(null);
        productComboBox.getSelectionModel().select(p);

        quantityTextField.setText(String.valueOf(item.getQuantityInWarehouse()));
        quantitySlider.setValue(item.getQuantityInWarehouse());
    }

    private void fillContactForm(WarehouseContact contact) {
        contactNumberField.setText(contact.getContactNumber());
    }

    private void updateCurrentStorage() {
        int total = currentInventoryItems.stream()
                .filter(it -> !it.isDisabled())
                .mapToInt(InventoryItem::getQuantityInWarehouse)
                .sum();
        currentStorageField.setText(String.valueOf(total));
    }

    private boolean checkWarehouseCapacity(int additionalQuantity) {
        int maxStorage = 0;
        try {
            maxStorage = Integer.parseInt(maxStorageField.getText().trim());
        } catch (NumberFormatException e) {
            return true;
        }

        int currentStorage = currentInventoryItems.stream()
                .filter(it -> !it.isDisabled())
                .mapToInt(InventoryItem::getQuantityInWarehouse)
                .sum();

        int newTotal = currentStorage + additionalQuantity;

        if (newTotal > maxStorage) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warehouse Capacity Exceeded");
            alert.setHeaderText("Cannot add item - warehouse will be full!");
            alert.setContentText(String.format(
                    "Current storage: %d\n" +
                            "Item quantity: %d\n" +
                            "New total: %d\n" +
                            "Max capacity: %d\n\n" +
                            "This would exceed the warehouse capacity by %d units.",
                    currentStorage, additionalQuantity, newTotal, maxStorage, (newTotal - maxStorage)
            ));
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @FXML
    private void onInsert() {
        clearForm();
        currentInventoryItems.clear();
        currentContacts.clear();
        setMode(Mode.INSERT);
        dateOfEstablishmentPicker.setValue(LocalDate.now());
        statusLabel.setText("INSERT mode: fill fields and add items, then CONFIRM.");
    }

    @FXML
    private void onUpdate() {
        Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a warehouse first.");
            return;
        }
        fillForm(selected);
        loadInventoryItems(selected.getId());
        loadWarehouseContacts(selected.getId());
        setMode(Mode.UPDATE);
        statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
    }

    @FXML
    private void onConfirm() {
        try {
            if (dateOfEstablishmentPicker.getValue() == null) {
                statusLabel.setText("Date of establishment is required.");
                return;
            }
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                statusLabel.setText("Warehouse name is required.");
                return;
            }
            if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
                statusLabel.setText("Address is required.");
                return;
            }

            int maxStorage;
            int currentStorage;
            try {
                maxStorage = Integer.parseInt(maxStorageField.getText().trim());
                currentStorage = Integer.parseInt(currentStorageField.getText().trim());
            } catch (Exception ex) {
                statusLabel.setText("Invalid storage values.");
                return;
            }

            Warehouse warehouse = new Warehouse(
                    mode == Mode.INSERT ? 0 : Integer.parseInt(idField.getText()),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    dateOfEstablishmentPicker.getValue(),
                    maxStorage,
                    currentStorage,
                    isDisabledCheckBox.isSelected()
            );

            if (mode == Mode.INSERT) {
                int newId = WarehouseDAO.insertWarehouseWithInventory(
                        warehouse,
                        new ArrayList<>(currentInventoryItems),
                        new ArrayList<>(currentContacts)
                );
                if (newId > 0) {
                    statusLabel.setText("Warehouse inserted successfully with ID: " + newId);
                    loadData();
                    setMode(Mode.VIEW);
                }
            } else if (mode == Mode.UPDATE) {
                WarehouseDAO.updateWarehouseWithInventory(
                        warehouse,
                        new ArrayList<>(currentInventoryItems),
                        new ArrayList<>(currentContacts)
                );
                statusLabel.setText("Warehouse updated successfully.");
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
        currentInventoryItems.clear();
        currentContacts.clear();
        warehouseTable.getSelectionModel().clearSelection();
        setMode(Mode.VIEW);
        statusLabel.setText("");
        clearInfoBar();
    }

    private void clearInfoBar() {
        if (infoWarehouseId != null) infoWarehouseId.setText("-");
        if (infoWarehouseName != null) infoWarehouseName.setText("-");
        if (infoMaxStorage != null) infoMaxStorage.setText("-");
        if (infoCurrentStorage != null) infoCurrentStorage.setText("-");
        if (infoCapacityPercentage != null) infoCapacityPercentage.setText("-");
    }

    @FXML
    private void onAddItem() {
        try {
            Product selected = productComboBox.getValue();
            if (selected == null) {
                statusLabel.setText("Select a product first.");
                return;
            }

            int quantity = Integer.parseInt(quantityTextField.getText().trim());

            if (quantity <= 0) {
                statusLabel.setText("Quantity must be positive.");
                return;
            }

            if (!checkWarehouseCapacity(quantity)) {
                return;
            }

            InventoryItem newItem = new InventoryItem(selected.getId(), selected.getName(), quantity);
            currentInventoryItems.add(newItem);

            clearItemForm();
            statusLabel.setText("Item added.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        }
    }

    @FXML
    private void onUpdateItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an item to update.");
            return;
        }

        try {
            int newQuantity = Integer.parseInt(quantityTextField.getText().trim());
            int oldQuantity = selected.getQuantityInWarehouse();
            int difference = newQuantity - oldQuantity;

            if (!checkWarehouseCapacity(difference)) {
                return;
            }

            selected.setQuantityInWarehouse(newQuantity);

            inventoryTable.refresh();
            updateCurrentStorage();
            statusLabel.setText("Item updated.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        }
    }

    @FXML
    private void onRemoveItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an item to remove.");
            return;
        }

        currentInventoryItems.remove(selected);
        clearItemForm();
        statusLabel.setText("Item removed.");
    }

    @FXML
    private void onToggleItemStatus() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an item to toggle status.");
            return;
        }

        selected.setDisabled(!selected.isDisabled());
        inventoryTable.refresh();
        updateCurrentStorage();
        statusLabel.setText("Item " + (selected.isDisabled() ? "disabled" : "enabled") + ".");
    }

    @FXML
    private void onAddContact() {
        try {
            String contactNumber = contactNumberField.getText();
            if (contactNumber == null || contactNumber.trim().isEmpty()) {
                statusLabel.setText("Contact number is required.");
                return;
            }

            WarehouseContact newContact = new WarehouseContact(contactNumber.trim());
            currentContacts.add(newContact);

            clearContactForm();
            statusLabel.setText("Contact added.");
        } catch (Exception e) {
            statusLabel.setText("Error adding contact.");
        }
    }

    @FXML
    private void onUpdateContact() {
        WarehouseContact selected = contactTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a contact to update.");
            return;
        }

        try {
            String contactNumber = contactNumberField.getText();
            if (contactNumber == null || contactNumber.trim().isEmpty()) {
                statusLabel.setText("Contact number is required.");
                return;
            }

            selected.setContactNumber(contactNumber.trim());
            contactTable.refresh();
            statusLabel.setText("Contact updated.");
        } catch (Exception e) {
            statusLabel.setText("Error updating contact.");
        }
    }

    @FXML
    private void onRemoveContact() {
        WarehouseContact selected = contactTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a contact to remove.");
            return;
        }

        currentContacts.remove(selected);
        clearContactForm();
        statusLabel.setText("Contact removed.");
    }

    @FXML
    private void onClearContact() {
        clearContactForm();
        contactTable.getSelectionModel().clearSelection();
        statusLabel.setText("Contact form cleared.");
    }

    @FXML
    private void onDisableWarehouse() {
        Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a warehouse first.");
            return;
        }

        try {
            boolean newStatus = !selected.isDisabled();
            WarehouseDAO.toggleWarehouseStatus(selected.getId(), newStatus);

            selected.setDisabled(newStatus);
            warehouseTable.refresh();
            isDisabledCheckBox.setSelected(newStatus);

            statusLabel.setText("Warehouse " + (newStatus ? "disabled" : "enabled") + " successfully.");
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
                statusLabel.setText("Showing all warehouses.");
                return;
            }
        } else {
            key = searchTextField.getText() == null ? "" : searchTextField.getText().trim();
            if (key.isEmpty()) {
                filteredList.setAll(masterList);
                statusLabel.setText("Showing all warehouses.");
                return;
            }
        }

        List<Warehouse> out = new ArrayList<>();
        for (Warehouse w : masterList) {
            boolean match = false;
            switch (searchBy) {
                case "ID":
                    match = key.matches("\\d+") && w.getId() == Integer.parseInt(key);
                    break;
                case "Name":
                    match = w.getName() != null && w.getName().toLowerCase().contains(key.toLowerCase());
                    break;
                case "Address":
                    match = w.getAddress() != null && w.getAddress().toLowerCase().contains(key.toLowerCase());
                    break;
                case "Date":
                    match = w.getDateOfEstablishment() != null && w.getDateOfEstablishment().equals(searchDate);
                    break;
            }
            if (match) out.add(w);
        }

        filteredList.setAll(out);
        statusLabel.setText("Found " + out.size() + " warehouses.");
    }

    @FXML
    private void onRefresh() {
        loadData();
        setMode(Mode.VIEW);
        statusLabel.setText("Data refreshed.");
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        addressField.clear();
        dateOfEstablishmentPicker.setValue(null);
        maxStorageField.clear();
        currentStorageField.clear();
        isDisabledCheckBox.setSelected(false);
    }

    private void clearItemForm() {
        productComboBox.getSelectionModel().clearSelection();
        quantityTextField.clear();
        quantitySlider.setValue(1);
        inventoryTable.getSelectionModel().clearSelection();
    }

    private void clearContactForm() {
        contactNumberField.clear();
        contactTable.getSelectionModel().clearSelection();
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        boolean editable = (mode != Mode.VIEW);

        idField.setEditable(false);
        currentStorageField.setEditable(false);

        nameField.setEditable(editable);
        addressField.setEditable(editable);
        dateOfEstablishmentPicker.setDisable(!editable);
        maxStorageField.setEditable(editable);
        isDisabledCheckBox.setDisable(!editable);
        isDisabledCheckBox.setVisible(!(mode == Mode.INSERT));
        isDisabledCheckBox.setManaged(!(mode == Mode.INSERT));

        productComboBox.setDisable(!editable);
        quantityTextField.setEditable(editable);
        quantitySlider.setDisable(!editable);

        contactNumberField.setEditable(editable);

        confirmButton.setVisible(editable);
        confirmButton.setManaged(editable);
        cancelButton.setVisible(editable);
        cancelButton.setManaged(editable);

        addItemButton.setDisable(!editable);
        updateItemButton.setDisable(!editable);
        removeItemButton.setDisable(!editable);

        addContactButton.setDisable(!editable);
        updateContactButton.setDisable(!editable);
        removeContactButton.setDisable(!editable);
    }
}