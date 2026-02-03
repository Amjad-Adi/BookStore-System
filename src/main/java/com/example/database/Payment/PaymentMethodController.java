package com.example.database.Payment;
import com.example.database.Orders.OrderController;
import com.example.database.Payment.*;
import com.example.database.DBConnection;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import static javafx.collections.FXCollections.observableArrayList;

public class PaymentMethodController{
    @FXML private Label paymentMethodIDLabel;
    @FXML private Label paymentMethodNameLabel;
    @FXML private Label paymentMethodDescriptionLabel;

    @FXML private TextField paymentMethodIDField;
    @FXML private TextField paymentMethodNameField;
    @FXML private TextField paymentMethodDescriptionField;
    @FXML private CheckBox isDisabledCheckBox;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private TableView<PaymentMethod> paymentMethodTable;
    @FXML private TableColumn<PaymentMethod, Integer> paymentMethodIDColumn;
    @FXML private TableColumn<PaymentMethod, String> paymentMethodNameColumn;
    @FXML private TableColumn<PaymentMethod, String> paymentMethodDescriptionColumn;
    @FXML private TableColumn<PaymentMethod, Boolean> paymentMethodStatusColumn;
    private final ObservableList<PaymentMethod> paymentMethodList = observableArrayList();
    private final ObservableList<PaymentMethod> masterList        = observableArrayList();

    @FXML private Label statusLabel;

    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR    = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR   = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT      = "rgba(56,189,248,0.22)";

    @FXML private ComboBox<String> searchByCombo;
    @FXML private TextField searchField;

    @FXML
    public void initialize(){
        paymentMethodTable.setItems(paymentMethodList);
        paymentMethodIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        paymentMethodNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        paymentMethodDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        paymentMethodStatusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        paymentMethodStatusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(paymentMethodStatusColumn));
        paymentMethodDescriptionColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
            }
        });
        paymentMethodTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PaymentMethod paymentMethod, boolean empty) {
                super.updateItem(paymentMethod, empty);
                if (empty || paymentMethod == null) {
                    setStyle("");
                    return;
                }
                setPrefHeight(30);
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (paymentMethod.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else {
                    if (isSelected()) {
                        setStyle("-fx-background-color: " + baseColor + ", " + SELECT_TINT + ";");
                    } else {
                        setStyle("-fx-background-color: " + baseColor + ";");
                    }
                }
            }
        });

        paymentMethodTable.setMinWidth(1500);
        paymentMethodTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        paymentMethodStatusColumn.setPrefWidth(90);
        paymentMethodIDColumn.setPrefWidth(90);
        paymentMethodNameColumn.setPrefWidth(200);
        paymentMethodDescriptionColumn.setPrefWidth(750);
        paymentMethodDescriptionColumn.setResizable(true);
        paymentMethodTable.skinProperty().addListener((obs, o, n) -> paymentMethodTable.lookupAll(".column-header .label").forEach(
                node -> node.setStyle("-fx-text-fill: #e5e7eb; -fx-font-weight: bold;")));
        paymentMethodTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, paymentMethod) -> {
            if (paymentMethod != null) fillForm(paymentMethod);
        });

        paymentMethodTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node corner = paymentMethodTable.lookup(".corner");
                if (corner != null) {
                    corner.setStyle(
                            "-fx-background-color: #020617;" +
                                    "-fx-border-color: #1e293b;" +
                                    "-fx-border-width: 0 0 1 1;"
                    );
                }

                Node filler = paymentMethodTable.lookup(".filler");
                if (filler != null) {
                    filler.setStyle(
                            "-fx-background-color: #020617;" +
                                    "-fx-border-color: #1e293b;" +
                                    "-fx-border-width: 0 0 1 0;"
                    );
                }
            });
        });

        // ComboBox: white bg + black text + separators + hover highlight
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
                setStyle("-fx-text-fill: black;" + "-fx-background-color: white;" + "-fx-padding: 8 10 8 10;" + "-fx-border-color: #e5e7eb;" + "-fx-border-width: 0 0 1 0;");
                hoverProperty().addListener((obs, was, isNow) -> {
                    if (isNow)
                        setStyle("-fx-text-fill: black;" + "-fx-background-color: #f1f5f9;" + "-fx-padding: 8 10 8 10;" + "-fx-border-color: #e5e7eb;" + "-fx-border-width: 0 0 1 0;");
                    else
                        setStyle("-fx-text-fill: black;" + "-fx-background-color: white;" + "-fx-padding: 8 10 8 10;" + "-fx-border-color: #e5e7eb;" + "-fx-border-width: 0 0 1 0;");
                });
            }
        });

        searchByCombo.setItems(observableArrayList("ID", "Name", "Description"));
        searchByCombo.getSelectionModel().select("ID");

        paymentMethodIDField.setEditable(false);
        paymentMethodIDField.setFocusTraversable(false);

        setMode(Mode.VIEW);
        loadPaymentMethods();
    }

    private void fillForm(PaymentMethod paymentMethod) {
        paymentMethodIDField.setText(String.valueOf(paymentMethod.getID()));
        paymentMethodNameField.setText(paymentMethod.getName() == null ? "" : paymentMethod.getName());
        paymentMethodDescriptionField.setText(paymentMethod.getDescription() == null ? "" : paymentMethod.getDescription());
        isDisabledCheckBox.setSelected(paymentMethod.isDisabled());
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        setNode(paymentMethodIDLabel, !(mode == Mode.INSERT));
        setNode(paymentMethodIDField, !(mode == Mode.INSERT));
        ChangeTextFieldMode(!(mode == Mode.VIEW));
        setNode(confirmButton, !(mode == Mode.VIEW));
        setNode(cancelButton, !(mode == Mode.VIEW));
    }
    private void ChangeTextFieldMode(boolean editable) {
        // Standard white background for input
        String whiteStyle = "-fx-control-inner-background: white; -fx-background-color: white; -fx-text-fill: black; -fx-border-color: #1e293b; -fx-border-radius: 6; -fx-background-radius: 6;";

        String darkViewStyle = "-fx-control-inner-background: #0f172a; -fx-background-color: #0f172a; -fx-text-fill: #e5e7eb; -fx-border-color: #1e293b; -fx-border-radius: 6; -fx-background-radius: 6;";

        String currentStyle = editable ? whiteStyle : darkViewStyle;

        paymentMethodNameField.setEditable(editable);
        paymentMethodNameField.setStyle(currentStyle);

        paymentMethodDescriptionField.setEditable(editable);
        paymentMethodDescriptionField.setStyle(currentStyle);

        paymentMethodIDField.setStyle(darkViewStyle);

        isDisabledCheckBox.setDisable(!editable);
        paymentMethodNameField.setFocusTraversable(editable);
        paymentMethodDescriptionField.setFocusTraversable(editable);
        isDisabledCheckBox.setVisible(!(mode==Mode.INSERT));
        isDisabledCheckBox.setManaged(!(mode == Mode.INSERT));
        paymentMethodNameField.setDisable(false);
        paymentMethodDescriptionField.setDisable(false);
        paymentMethodIDField.setDisable(false);
    }

    private void setNode(Control c, boolean visible) {
        c.setVisible(visible);
        c.setManaged(visible);
    }

    private void loadPaymentMethods() {
        try {
            ArrayList<PaymentMethod> paymentMethods = PaymentMethodDAO.getPaymentMethodList();
            masterList.setAll(paymentMethods);
            paymentMethodList.setAll(paymentMethods);
            statusLabel.setText("Loaded " + paymentMethodList.size() + " payment methods.");
            paymentMethodTable.refresh();
        } catch (Exception e) {
            DBConnection.showError("Database error", e.getMessage());
            statusLabel.setText("Failed to load payment methods.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onInsert() {
        clearForm();
        setMode(Mode.INSERT);
        statusLabel.setText("INSERT mode: fill fields then CONFIRM.");
    }

    private void insertPaymentMethod() {
        try {
            if (paymentMethodNameField.getText() == null || paymentMethodNameField.getText().trim().isEmpty()) {
                statusLabel.setText("Payment method name is required.");
                return;
            }
            PaymentMethod newPaymentMethod = new PaymentMethod(0, paymentMethodNameField.getText().trim(), (paymentMethodDescriptionField.getText() == null || paymentMethodDescriptionField.getText().trim().isEmpty()) ? null : paymentMethodDescriptionField.getText().trim(),false);
            int newId = PaymentMethodDAO.insertPaymentMethod(newPaymentMethod);
            if (newId > 0) {
                PaymentMethod inserted = new PaymentMethod(newId, newPaymentMethod.getName(), newPaymentMethod.getDescription(),false);
                masterList.add(inserted);
                paymentMethodList.add(inserted);
                paymentMethodTable.getSelectionModel().select(inserted);
                setMode(Mode.VIEW);
                statusLabel.setText("Inserted payment method ID = " + newId);
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
        PaymentMethod selected = paymentMethodTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a payment method first.");
        }
        else {
            fillForm(selected);
            setMode(Mode.UPDATE);
            statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
        }
    }

    private void updatePaymentMethod() {
        PaymentMethod selected = paymentMethodTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            statusLabel.setText("Select a payment method first.");
        else {
            try {
                if (paymentMethodNameField.getText() == null || paymentMethodNameField.getText().trim().isEmpty()) {
                    statusLabel.setText("Payment method name is required.");
                    return;
                }
                selected.setDisabled(isDisabledCheckBox.isSelected());
                PaymentMethod updatedPaymentMethod = new PaymentMethod(Integer.parseInt(paymentMethodIDField.getText().trim()), paymentMethodNameField.getText().trim(), (paymentMethodDescriptionField.getText() == null || paymentMethodDescriptionField.getText().trim().isEmpty()) ? null : paymentMethodDescriptionField.getText().trim(),selected.isDisabled());
                PaymentMethodDAO.updatePaymentMethod(updatedPaymentMethod);
                replaceInLists(updatedPaymentMethod);
                paymentMethodTable.getSelectionModel().select(updatedPaymentMethod);
                setMode(Mode.VIEW);
                statusLabel.setText("Updated successfully.");
            } catch (Exception e) {
                DBConnection.showError("Update error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onConfirm() {
        if (mode == Mode.INSERT) {
            insertPaymentMethod();
        } else if (mode == Mode.UPDATE) {
            updatePaymentMethod();
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
        List<PaymentMethod> filtered = new ArrayList<>();

        for (PaymentMethod paymentMethod : masterList) {
            boolean ok = false;

            if ("ID".equals(searchStandard)) {
                ok = key.matches("\\d+") && paymentMethod.getID() == Integer.parseInt(key);
            }
            else if ("Name".equals(searchStandard)) {
                ok = paymentMethod.getName() != null && paymentMethod.getName().equalsIgnoreCase(key);
            }
            else if ("Description".equals(searchStandard)) {
                ok = paymentMethod.getDescription() != null && paymentMethod.getDescription().equalsIgnoreCase(key);
            }

            if (ok)
                filtered.add(paymentMethod);
        }

        paymentMethodList.setAll(filtered);
        statusLabel.setText("Filtered: " + filtered.size());
        paymentMethodTable.refresh();
    }

    @FXML
    private void onRefresh() {
        paymentMethodList.setAll(masterList);
        setMode(Mode.VIEW);
        statusLabel.setText("Payment methods refreshed");
        paymentMethodTable.refresh();
    }

    private void clearForm() {
        paymentMethodTable.getSelectionModel().clearSelection();
        paymentMethodIDField.clear();
        paymentMethodNameField.clear();
        paymentMethodDescriptionField.clear();
    }

    @FXML
    private void onClear() {
        clearForm();
        setMode(Mode.VIEW);
        statusLabel.setText("");
    }

    private void replaceInLists(PaymentMethod updatedPaymentMethod) {
        for (int i = 0; i < masterList.size(); i++) {
            if (masterList.get(i).getID() == updatedPaymentMethod.getID()) {
                masterList.set(i, updatedPaymentMethod);
                break;
            }
        }
        for (int i = 0; i < paymentMethodList.size(); i++) {
            if (paymentMethodList.get(i).getID() == updatedPaymentMethod.getID()) {
                paymentMethodList.set(i, updatedPaymentMethod);
                break;
            }
        }
    }
}
