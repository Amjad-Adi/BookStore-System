package com.example.database.Payment;

import com.example.database.DBConnection;
import com.example.database.Orders.OrderController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class PaymentPlanController {

    @FXML private TextField idField;
    @FXML private TextField periodInMonthsField;
    @FXML private TextField monthsBeforeLegalTrialField;
    @FXML private TextField descriptionField;
    @FXML private CheckBox isDisabledCheckBox;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private TableView<PaymentPlan> paymentPlanTable;
    @FXML private TableColumn<PaymentPlan, Boolean> statusColumn;
    @FXML private TableColumn<PaymentPlan, Integer> idColumn;
    @FXML private TableColumn<PaymentPlan, Integer> periodInMonthsColumn;
    @FXML private TableColumn<PaymentPlan, Integer> monthsBeforeLegalTrialColumn;
    @FXML private TableColumn<PaymentPlan, String> descriptionColumn;

    private final ObservableList<PaymentPlan> paymentPlanList = observableArrayList();
    private final ObservableList<PaymentPlan> masterList = observableArrayList();

    @FXML private Label statusLabel;

    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT = "rgba(56,189,248,0.22)";

    @FXML private ComboBox<String> searchByCombo;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        paymentPlanTable.setItems(paymentPlanList);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        periodInMonthsColumn.setCellValueFactory(new PropertyValueFactory<>("periodInMonths"));
        monthsBeforeLegalTrialColumn.setCellValueFactory(new PropertyValueFactory<>("monthsBeforeLegalTrial"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));

        monthsBeforeLegalTrialColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        descriptionColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
            }
        });

        paymentPlanTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PaymentPlan plan, boolean empty) {
                super.updateItem(plan, empty);
                if (empty || plan == null) {
                    setStyle("");
                    return;
                }
                setPrefHeight(30);
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (plan.isDisabled()) {
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

        paymentPlanTable.setMinWidth(1500);
        paymentPlanTable.setPrefHeight(380);
        paymentPlanTable.setMinHeight(380);
        paymentPlanTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        statusColumn.setPrefWidth(100);
        idColumn.setPrefWidth(100);
        periodInMonthsColumn.setPrefWidth(200);
        monthsBeforeLegalTrialColumn.setPrefWidth(250);
        descriptionColumn.setPrefWidth(500);
        descriptionColumn.setResizable(true);

        paymentPlanTable.skinProperty().addListener((obs, o, n) ->
                paymentPlanTable.lookupAll(".column-header .label").forEach(
                        node -> node.setStyle("-fx-text-fill: #e5e7eb; -fx-font-weight: bold;")
                )
        );

        paymentPlanTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, plan) -> {
            if (plan != null) fillForm(plan);
        });

        paymentPlanTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node corner = paymentPlanTable.lookup(".corner");
                if (corner != null) {
                    corner.setStyle(
                            "-fx-background-color: #020617;" +
                                    "-fx-border-color: #1e293b;" +
                                    "-fx-border-width: 0 0 1 1;"
                    );
                }

                Node filler = paymentPlanTable.lookup(".filler");
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

        searchByCombo.setItems(observableArrayList(
                "ID", "Period in Months", "Months Before Trial", "Description"
        ));
        searchByCombo.getSelectionModel().select("ID");

        setupIntegerField(periodInMonthsField);
        setupIntegerField(monthsBeforeLegalTrialField);

        idField.setEditable(false);
        idField.setFocusTraversable(false);

        setMode(Mode.VIEW);
        loadPaymentPlans();
    }

    private void setupIntegerField(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        }));
    }

    private void fillForm(PaymentPlan plan) {
        idField.setText(String.valueOf(plan.getId()));
        periodInMonthsField.setText(String.valueOf(plan.getPeriodInMonths()));
        monthsBeforeLegalTrialField.setText(plan.getMonthsBeforeLegalTrial() == null ? "" : plan.getMonthsBeforeLegalTrial().toString());
        descriptionField.setText(plan.getDescription() == null ? "" : plan.getDescription());
        isDisabledCheckBox.setSelected(plan.isDisabled());
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        ChangeFieldsMode(!(mode == Mode.VIEW));
        confirmButton.setVisible(!(mode == Mode.VIEW));
        confirmButton.setManaged(!(mode == Mode.VIEW));
        cancelButton.setVisible(!(mode == Mode.VIEW));
        cancelButton.setManaged(!(mode == Mode.VIEW));
        isDisabledCheckBox.setVisible(!(mode== Mode.INSERT));
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

        periodInMonthsField.setEditable(editable);
        periodInMonthsField.setStyle(currentStyle);

        monthsBeforeLegalTrialField.setEditable(editable);
        monthsBeforeLegalTrialField.setStyle(currentStyle);

        descriptionField.setEditable(editable);
        descriptionField.setStyle(currentStyle);

        idField.setStyle(viewStyle);

        isDisabledCheckBox.setDisable(!editable);
        periodInMonthsField.setFocusTraversable(editable);
        monthsBeforeLegalTrialField.setFocusTraversable(editable);
        descriptionField.setFocusTraversable(editable);

        periodInMonthsField.setDisable(false);
        monthsBeforeLegalTrialField.setDisable(false);
        descriptionField.setDisable(false);
        idField.setDisable(false);
    }

    private void loadPaymentPlans() {
        try {
            ArrayList<PaymentPlan> plans = PaymentPlanDAO.getPaymentPlanList();
            masterList.setAll(plans);
            paymentPlanList.setAll(plans);
            statusLabel.setText("Loaded " + paymentPlanList.size() + " payment plans.");
            paymentPlanTable.refresh();
        } catch (Exception e) {
            DBConnection.showError("Database error", e.getMessage());
            statusLabel.setText("Failed to load payment plans.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onInsert() {
        clearForm();
        setMode(Mode.INSERT);
        statusLabel.setText("INSERT mode: fill fields then CONFIRM.");
    }

    private void insertPaymentPlan() {
        try {
            if (periodInMonthsField.getText() == null || periodInMonthsField.getText().trim().isEmpty()) {
                statusLabel.setText("Period in months is required.");
                return;
            }

            PaymentPlan newPlan = new PaymentPlan(
                    0,
                    Integer.parseInt(periodInMonthsField.getText().trim()),
                    monthsBeforeLegalTrialField.getText() == null || monthsBeforeLegalTrialField.getText().trim().isEmpty()
                            ? null : Integer.parseInt(monthsBeforeLegalTrialField.getText().trim()),
                    descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()
                            ? null : descriptionField.getText().trim(),
                    false
            );

            int newId = PaymentPlanDAO.insertPaymentPlan(newPlan);

            if (newId > 0) {
                newPlan.setId(newId);
                masterList.add(newPlan);
                paymentPlanList.add(newPlan);
                paymentPlanTable.getSelectionModel().select(newPlan);
                setMode(Mode.VIEW);
                statusLabel.setText("Inserted payment plan ID = " + newId);
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
        PaymentPlan selected = paymentPlanTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a payment plan first.");
        } else {
            fillForm(selected);
            setMode(Mode.UPDATE);
            statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
        }
    }

    private void updatePaymentPlan() {
        PaymentPlan selected = paymentPlanTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a payment plan first.");
        } else {
            try {
                if (periodInMonthsField.getText() == null || periodInMonthsField.getText().trim().isEmpty()) {
                    statusLabel.setText("Period in months is required.");
                    return;
                }

                selected.setPeriodInMonths(Integer.parseInt(periodInMonthsField.getText().trim()));
                selected.setMonthsBeforeLegalTrial(
                        monthsBeforeLegalTrialField.getText() == null || monthsBeforeLegalTrialField.getText().trim().isEmpty()
                                ? null : Integer.parseInt(monthsBeforeLegalTrialField.getText().trim())
                );
                selected.setDescription(
                        descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()
                                ? null : descriptionField.getText().trim()
                );
                selected.setDisabled(isDisabledCheckBox.isSelected());

                PaymentPlanDAO.updatePaymentPlan(selected);
                replaceInLists(selected);
                paymentPlanTable.getSelectionModel().select(selected);
                paymentPlanTable.refresh();
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
            insertPaymentPlan();
        } else if (mode == Mode.UPDATE) {
            updatePaymentPlan();
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
        List<PaymentPlan> filtered = new ArrayList<>();

        for (PaymentPlan plan : masterList) {
            boolean ok = false;

            if ("ID".equals(searchStandard)) {
                ok = key.matches("\\d+") && plan.getId() == Integer.parseInt(key);
            } else if ("Period in Months".equals(searchStandard)) {
                ok = key.matches("\\d+") && plan.getPeriodInMonths() == Integer.parseInt(key);
            } else if ("Months Before Trial".equals(searchStandard)) {
                if (key.matches("\\d+")) {
                    Integer months = plan.getMonthsBeforeLegalTrial();
                    ok = (months != null && months == Integer.parseInt(key));
                }
            } else if ("Description".equals(searchStandard)) {
                ok = plan.getDescription() != null && plan.getDescription().toLowerCase().contains(key.toLowerCase());
            }

            if (ok) {
                filtered.add(plan);
            }
        }

        paymentPlanList.setAll(filtered);
        statusLabel.setText("Filtered: " + filtered.size());
        paymentPlanTable.refresh();
    }

    @FXML
    private void onRefresh() {
        paymentPlanList.setAll(masterList);
        setMode(Mode.VIEW);
        statusLabel.setText("Payment plans refreshed");
        paymentPlanTable.refresh();
    }

    private void clearForm() {
        paymentPlanTable.getSelectionModel().clearSelection();
        idField.clear();
        periodInMonthsField.clear();
        monthsBeforeLegalTrialField.clear();
        descriptionField.clear();
        isDisabledCheckBox.setSelected(false);
    }

    @FXML
    private void onClear() {
        clearForm();
        setMode(Mode.VIEW);
        statusLabel.setText("");
    }

    private void replaceInLists(PaymentPlan updatedPlan) {
        for (int i = 0; i < masterList.size(); i++) {
            if (masterList.get(i).getId() == updatedPlan.getId()) {
                masterList.set(i, updatedPlan);
                break;
            }
        }
        for (int i = 0; i < paymentPlanList.size(); i++) {
            if (paymentPlanList.get(i).getId() == updatedPlan.getId()) {
                paymentPlanList.set(i, updatedPlan);
                break;
            }
        }
    }
}
