package com.example.database.Customer;

import com.example.database.DBConnection;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class CustomerController {


    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> professionComboBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker registerDatePicker;
    @FXML private DatePicker activationDatePicker;
    @FXML private DatePicker expirationDatePicker;
    @FXML private TextField budgetField;
    @FXML private CheckBox isDisabledCheckBox;
    @FXML private Button confirmBtn;
    @FXML private Button cancelBtn;

    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> id;
    @FXML private TableColumn<Customer, String> firstName;
    @FXML private TableColumn<Customer, String> secondName;
    @FXML private TableColumn<Customer, String> email;
    @FXML private TableColumn<Customer, String> profession;
    @FXML private TableColumn<Customer, LocalDate> birthDate;
    @FXML private TableColumn<Customer, LocalDate> registerDate;
    @FXML private TableColumn<Customer, LocalDate> activationDate;
    @FXML private TableColumn<Customer, LocalDate> expirationDate;
    @FXML private TableColumn<Customer, Double> budget;
    @FXML private TableColumn<Customer, Boolean> statusColumn;

    private final ObservableList<Customer> customersList = observableArrayList();
    private final ObservableList<Customer> masterList = observableArrayList();

    // why we have two lists ?
    // the master list is the list that we have load the customers from the database to it
    // the customer list is the currently displayed data in the table view

    // we do that to save the original data in some other list because it's hard to restore the original data
    // if we don't save them



    @FXML private Label statusLabel;
    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_Customer = "rgba(56,189,248,0.22)";
    private static final String ACTIVE_Customer = "rgba(34,197,94,0.15)";
    private static final String EXPIRED_Customer = "rgba(239,68,68,0.15)";


    @FXML private ComboBox<String> searchByCombo;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        customersTable.setItems(customersList);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        secondName.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        profession.setCellValueFactory(new PropertyValueFactory<>("profession"));
        birthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        registerDate.setCellValueFactory(new PropertyValueFactory<>("registerDate"));
        activationDate.setCellValueFactory(new PropertyValueFactory<>("activationDate"));
        expirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        budget.setCellValueFactory(new PropertyValueFactory<>("budgetInDollars"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));

        birthDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        registerDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        activationDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        expirationDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        budget.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        customersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setStyle("");
                    return;
                }
                setPrefHeight(30);
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;

                boolean isActive = customer.getExpirationDate() != null &&
                        !customer.getExpirationDate().isBefore(LocalDate.now());

                if (customer.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else {
                    String statusCustomer = isActive ? ACTIVE_Customer : EXPIRED_Customer;

                    if (isSelected()) {
                        setStyle("-fx-background-color: " + baseColor + ", " + statusCustomer + ", " + SELECT_Customer + ";");
                    } else {
                        setStyle("-fx-background-color: " + baseColor + ", " + statusCustomer + ";");
                    }
                }
            }
        });
        registerDatePicker.setValue(LocalDate.now());
        customersTable.setMinWidth(1500);
        customersTable.setPrefHeight(400);
        customersTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        statusColumn.setPrefWidth(100);
        id.setPrefWidth(100);
        firstName.setPrefWidth(150);
        secondName.setPrefWidth(150);
        email.setPrefWidth(200);
        profession.setPrefWidth(150);
        birthDate.setPrefWidth(90);
        registerDate.setPrefWidth(90);
        activationDate.setPrefWidth(90);
        expirationDate.setPrefWidth(90);
        budget.setPrefWidth(80);




        customersTable.skinProperty().addListener((obs, o, n) ->
                customersTable.lookupAll(".column-header .label").forEach(
                        node -> node.setStyle("-fx-text-fill: #e5e7eb; -fx-font-weight: bold;")
                )
        );

        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, customer) -> {
            if (customer != null) fillForm(customer);
        });

        customersTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node corner = customersTable.lookup(".corner");
                if (corner != null) {
                    corner.setStyle(
                            "-fx-background-color: #020617;" +
                                    "-fx-border-color: #1e293b;" +
                                    "-fx-border-width: 0 0 1 1;"
                    );
                }

                Node filler = customersTable.lookup(".filler");
                if (filler != null) {
                    filler.setStyle(
                            "-fx-background-color: #020617;" +
                                    "-fx-border-color: #1e293b;" +
                                    "-fx-border-width: 0 0 1 0;"
                    );
                }
            });
        });

        searchByCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
                setStyle("-fx-text-fill: black; -fx-background-color: white;");
            }
        });

        searchByCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                setStyle(
                        "-fx-text-fill: black;" +
                                "-fx-background-color: white;" +
                                "-fx-padding: 8 10 8 10;" +
                                "-fx-border-color: #e5e7eb;" +
                                "-fx-border-width: 0 0 1 0;"
                );

                hoverProperty().addListener((obs, was, isNow) -> {
                    if (isNow) {
                        setStyle(
                                "-fx-text-fill: black;" +
                                        "-fx-background-color: #f1f5f9;" +
                                        "-fx-padding: 8 10 8 10;" +
                                        "-fx-border-color: #e5e7eb;" +
                                        "-fx-border-width: 0 0 1 0;"
                        );
                    } else {
                        setStyle(
                                "-fx-text-fill: black;" +
                                        "-fx-background-color: white;" +
                                        "-fx-padding: 8 10 8 10;" +
                                        "-fx-border-color: #e5e7eb;" +
                                        "-fx-border-width: 0 0 1 0;"
                        );
                    }
                });
            }
        });
        customersTable.setMinWidth(1500);
        customersTable.setPrefHeight(380);
        customersTable.setMinHeight(380);
        customersTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        searchByCombo.setItems(observableArrayList(
                "ID", "First Name", "Last Name", "Email", "Profession", "Active", "Expired"
        ));
        searchByCombo.getSelectionModel().select("ID");

        professionComboBox.setItems(observableArrayList(
                "Engineer", "Developer", "Teacher", "Student", "Doctor", "Nurse",
                "Pharmacist", "Accountant", "Business Owner", "Designer",
                "Graphic Designer", "Journalist", "Architect", "Researcher", "Other"
        ));
        professionComboBox.getSelectionModel().select("Other");


        professionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item);
                setStyle("-fx-text-fill: #0f172a; -fx-background-color: rgba(255,255,255,0.92);");
            }
        });

        professionComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                setStyle(
                        "-fx-text-fill: black;" +
                                "-fx-background-color: white;" +
                                "-fx-padding: 8 10 8 10;" +
                                "-fx-border-color: #e5e7eb;" +
                                "-fx-border-width: 0 0 1 0;"
                );
                hoverProperty().addListener((obs, was, isNow) -> {
                    if (isNow) {
                        setStyle(
                                "-fx-text-fill: black;" +
                                        "-fx-background-color: #f1f5f9;" +
                                        "-fx-padding: 8 10 8 10;" +
                                        "-fx-border-color: #e5e7eb;" +
                                        "-fx-border-width: 0 0 1 0;"
                        );
                    } else {
                        setStyle(
                                "-fx-text-fill: black;" +
                                        "-fx-background-color: white;" +
                                        "-fx-padding: 8 10 8 10;" +
                                        "-fx-border-color: #e5e7eb;" +
                                        "-fx-border-width: 0 0 1 0;"
                        );
                    }
                });
            }
        });

        idField.setEditable(false);
        idField.setFocusTraversable(false);

        setMode(Mode.VIEW);
        loadCustomers();
    }

    private void fillForm(Customer customer) {
        idField.setText(String.valueOf(customer.getId()));
        firstNameField.setText(customer.getFirstName() == null ? "" : customer.getFirstName());
        lastNameField.setText(customer.getSecondName() == null ? "" : customer.getSecondName());
        emailField.setText(customer.getEmail() == null ? "" : customer.getEmail());
        professionComboBox.setValue(customer.getProfession() == null ? "Other" : customer.getProfession());
        birthDatePicker.setValue(customer.getBirthDate());
        registerDatePicker.setValue(customer.getRegisterDate());
        activationDatePicker.setValue(customer.getActivationDate());
        expirationDatePicker.setValue(customer.getExpirationDate());
        budgetField.setText(customer.getBudgetInDollars() == null ? "0.0" : String.valueOf(customer.getBudgetInDollars()));
        isDisabledCheckBox.setSelected(customer.isDisabled());
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        ChangeFieldsMode(!(mode == Mode.VIEW));
        confirmBtn.setVisible(!(mode == Mode.VIEW));
        confirmBtn.setManaged(!(mode == Mode.VIEW));
        cancelBtn.setVisible(!(mode == Mode.VIEW));
        cancelBtn.setManaged(!(mode == Mode.VIEW));
        isDisabledCheckBox.setVisible(!(mode==Mode.INSERT));
        isDisabledCheckBox.setManaged(!(mode == Mode.INSERT));
    }

    private void ChangeFieldsMode(boolean editable) {

        String editableStyle =
                "-fx-control-inner-background: rgba(255,255,255,0.92);" +
                        "-fx-background-color: rgba(255,255,255,0.92);" +
                        "-fx-text-fill: #0f172a;" +
                        "-fx-prompt-text-fill: rgba(15,23,42,0.55);" +
                        "-fx-border-color: #1e293b;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;";


        String viewStyle =
                "-fx-control-inner-background: #0f172a;" +
                        "-fx-background-color: #0f172a;" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-border-color: #1e293b;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;";

        String currentStyle = editable ? editableStyle : viewStyle;


        firstNameField.setEditable(editable);
        firstNameField.setStyle(currentStyle);

        lastNameField.setEditable(editable);
        lastNameField.setStyle(currentStyle);

        emailField.setEditable(editable);
        emailField.setStyle(currentStyle);
        professionComboBox.setDisable(!editable);
        if (editable) {
            professionComboBox.setStyle(editableStyle);
        } else {
            professionComboBox.setStyle(viewStyle);
        }


        budgetField.setEditable(editable);
        budgetField.setStyle(currentStyle);

        birthDatePicker.setEditable(editable);
        birthDatePicker.setDisable(!editable);
        birthDatePicker.setStyle(currentStyle);

        registerDatePicker.setEditable(editable);
        registerDatePicker.setDisable(!editable);
        registerDatePicker.setStyle(currentStyle);

        activationDatePicker.setEditable(editable);
        activationDatePicker.setDisable(!editable);
        activationDatePicker.setStyle(currentStyle);

        expirationDatePicker.setEditable(editable);
        expirationDatePicker.setDisable(!editable);
        expirationDatePicker.setStyle(currentStyle);


        idField.setStyle(viewStyle);

        isDisabledCheckBox.setDisable(!editable);
        firstNameField.setFocusTraversable(editable);
        lastNameField.setFocusTraversable(editable);
        emailField.setFocusTraversable(editable);

        professionComboBox.setFocusTraversable(editable);
        budgetField.setFocusTraversable(editable);

        firstNameField.setDisable(false);
        lastNameField.setDisable(false);
        emailField.setDisable(false);
        professionComboBox.setDisable(false);
        budgetField.setDisable(false);
        idField.setDisable(false);
    }

    private void loadCustomers() {
        try {
            ArrayList<Customer> customers = CustomerDAO.getAllCustomers();
            masterList.setAll(customers);
            customersList.setAll(customers);
            statusLabel.setText("Loaded " + customersList.size() + " customers.");
            customersTable.refresh();
        } catch (Exception e) {
            DBConnection.showError("Database error", e.getMessage());
            statusLabel.setText("Failed to load customers.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onInsert() {
        clearForm();
        setMode(Mode.INSERT);
        registerDatePicker.setValue(LocalDate.now());
        statusLabel.setText("INSERT mode: fill fields then CONFIRM.");
    }

    private void insertCustomer() {
        try {
            if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
                statusLabel.setText("First name is required.");
                return;
            }

            Customer newCustomer = new Customer(
                    0,
                    firstNameField.getText().trim(),
                    lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()
                            ? null : lastNameField.getText().trim(),
                    emailField.getText() == null || emailField.getText().trim().isEmpty()
                            ? null : emailField.getText().trim(),
                    null,
                    professionComboBox.getValue() == null ? "Other" : professionComboBox.getValue(),
                    birthDatePicker.getValue(),
                    registerDatePicker.getValue(),
                    activationDatePicker.getValue(),
                    expirationDatePicker.getValue(),
                    budgetField.getText() == null || budgetField.getText().trim().isEmpty()
                            ? 0.0 : Double.parseDouble(budgetField.getText().trim()),
                    false
            );

            int newId = CustomerDAO.insertCustomer(newCustomer);

            if (newId > 0) {
                newCustomer.setId(newId);
                masterList.add(newCustomer);
                customersList.add(newCustomer);
                customersTable.getSelectionModel().select(newCustomer);
                setMode(Mode.VIEW);
                statusLabel.setText("Inserted customer ID = " + newId);
            } else {
                statusLabel.setText("Insert failed.");
            }
        } catch (Exception e) {
            DBConnection.showError("Insert error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdate() {
        registerDatePicker.setValue(LocalDate.now());
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a customer first.");
        } else {
            fillForm(selected);
            setMode(Mode.UPDATE);
            statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
        }
    }

    private void updateCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a customer first.");
        } else {
            try {
                if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
                    statusLabel.setText("First name is required.");
                    return;
                }

                selected.setFirstName(firstNameField.getText().trim());
                selected.setSecondName(lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()
                        ? null : lastNameField.getText().trim());
                selected.setEmail(emailField.getText() == null || emailField.getText().trim().isEmpty()
                        ? null : emailField.getText().trim());
                selected.setProfession(professionComboBox.getValue() == null ? "Other" : professionComboBox.getValue());
                selected.setBirthDate(birthDatePicker.getValue());
                selected.setRegisterDate(registerDatePicker.getValue());
                selected.setActivationDate(activationDatePicker.getValue());
                selected.setExpirationDate(expirationDatePicker.getValue());
                selected.setBudgetInDollars(budgetField.getText() == null || budgetField.getText().trim().isEmpty()
                        ? 0.0 : Double.parseDouble(budgetField.getText().trim()));
                selected.setDisabled(isDisabledCheckBox.isSelected());

                CustomerDAO.updateCustomer(selected);
                replaceInLists(selected);
                customersTable.getSelectionModel().select(selected);
                customersTable.refresh();
                setMode(Mode.VIEW);
                statusLabel.setText("Updated successfully.");
            } catch (Exception e) {
                DBConnection.showError("Update error", e.getMessage());
            }
        }
    }

    @FXML
    private void onConfirm() {
        if (mode == Mode.INSERT) {
            insertCustomer();
        } else if (mode == Mode.UPDATE) {
            updateCustomer();
        }
    }

    @FXML
    private void onCancel() {
        setMode(Mode.VIEW);
        statusLabel.setText("Canceled.");
    }

    @FXML
    private void onSearch() {
        String searchStandard = searchByCombo.getValue();
        String key = (searchField.getText() == null) ? "" : searchField.getText().trim();
        List<Customer> filtered = new ArrayList<>();

        for (Customer customer : masterList) {
            boolean ok = false;

            if ("ID".equals(searchStandard)) {
                ok = key.matches("\\d+") && customer.getId() == Integer.parseInt(key);
            } else if ("First Name".equals(searchStandard)) {
                ok = customer.getFirstName() != null && customer.getFirstName().toLowerCase().contains(key.toLowerCase());
            } else if ("Last Name".equals(searchStandard)) {
                ok = customer.getSecondName() != null && customer.getSecondName().toLowerCase().contains(key.toLowerCase());
            } else if ("Email".equals(searchStandard)) {
                ok = customer.getEmail() != null && customer.getEmail().toLowerCase().contains(key.toLowerCase());
            } else if ("Profession".equals(searchStandard)) {
                ok = customer.getProfession() != null && customer.getProfession().toLowerCase().contains(key.toLowerCase());
            } else if ("Active".equals(searchStandard)) {
                ok = customer.getExpirationDate() != null && !customer.getExpirationDate().isBefore(LocalDate.now());
            } else if ("Expired".equals(searchStandard)) {
                ok = customer.getExpirationDate() == null || customer.getExpirationDate().isBefore(LocalDate.now());
            }

            if (ok) {
                filtered.add(customer);
            }
        }

        customersList.setAll(filtered);
        statusLabel.setText("Filtered: " + filtered.size());
        customersTable.refresh();
    }

    @FXML
    private void onRefresh() {
        customersList.setAll(masterList);
        setMode(Mode.VIEW);
        statusLabel.setText("Customers refreshed");
        customersTable.refresh();
    }

    private void clearForm() {
        customersTable.getSelectionModel().clearSelection();
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        professionComboBox.setValue(null);
        birthDatePicker.setValue(null);
        registerDatePicker.setValue(null);
        activationDatePicker.setValue(null);
        expirationDatePicker.setValue(null);
        budgetField.clear();
        isDisabledCheckBox.setSelected(false);
    }

    @FXML
    private void onClear() {
        clearForm();
        setMode(Mode.VIEW);
        statusLabel.setText("");
    }

    private void replaceInLists(Customer updatedCustomer) {
        for (int i = 0; i < masterList.size(); i++) {
            if (masterList.get(i).getId() == updatedCustomer.getId()) {
                masterList.set(i, updatedCustomer);
                break;
            }
        }
        for (int i = 0; i < customersList.size(); i++) {
            if (customersList.get(i).getId() == updatedCustomer.getId()) {
                customersList.set(i, updatedCustomer);
                break;
            }
        }
    }
}