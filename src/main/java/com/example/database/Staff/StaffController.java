package com.example.database.Staff;

import com.example.database.DBConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffController {

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Integer> colId;
    @FXML private TableColumn<Staff, String> colFirstName;
    @FXML private TableColumn<Staff, String> colLastName;
    @FXML private TableColumn<Staff, String> colEmail;
    @FXML private TableColumn<Staff, Double> colSalary;
    @FXML private TableColumn<Staff, LocalDate> colBirthDate;
    @FXML private TableColumn<Staff, LocalDate> colHireDate;
    @FXML private TableColumn<Staff, String> colStatus;

    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField salaryField;
    @FXML private DatePicker birthDatePicker;
    @FXML private DatePicker hireDatePicker;
    @FXML private ComboBox<String> statusCombo;

    @FXML private ComboBox<String> searchByCombo;
    @FXML private TextField searchField;

    @FXML private Button confirmBtn;
    @FXML private Button cancelBtn;

    @FXML private Label statusLabel;

    private final ObservableList<Staff> masterList = FXCollections.observableArrayList();
    private final ObservableList<Staff> tableList  = FXCollections.observableArrayList();

    private enum Mode { VIEW, INSERT, UPDATE }
    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR  = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT    = "rgba(56,189,248,0.22)";

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        staffTable.setItems(tableList);

        staffTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Staff s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setStyle(""); return; }
                String base = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (isSelected()) setStyle("-fx-background-color: " + base + ", " + SELECT_TINT + ";");
                else setStyle("-fx-background-color: " + base + ";");
            }
        });

        staffTable.skinProperty().addListener((obs, o, n) ->
                staffTable.lookupAll(".column-header .label").forEach(node ->
                        node.setStyle("-fx-text-fill: #e5e7eb; -fx-font-weight: bold;"))
        );

        staffTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node corner = staffTable.lookup(".corner");
                if (corner != null) corner.setStyle("-fx-background-color:#020617; -fx-border-color:#1e293b; -fx-border-width:0 0 1 1;");
                Node filler = staffTable.lookup(".filler");
                if (filler != null) filler.setStyle("-fx-background-color:#020617; -fx-border-color:#1e293b; -fx-border-width:0 0 1 0;");
            });
        });

        staffTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, s) -> {
            if (s != null) fillForm(s);
        });

        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        statusCombo.getSelectionModel().select("Active");

        searchByCombo.setItems(FXCollections.observableArrayList("ID", "First Name", "Last Name", "Email", "Status"));
        searchByCombo.getSelectionModel().select("First Name");

        idField.setEditable(false);
        hireDatePicker.setValue(LocalDate.now());

        loadOnce();
        setMode(Mode.VIEW);
    }

    private void loadOnce() {
        try {
            List<Staff> all = StaffDAO.getAllStaff();
            masterList.setAll(all);
            tableList.setAll(all);
            setStatus("Loaded " + all.size() + " staff.");
        } catch (Exception e) {
            DBConnection.showError("DB error", e.getMessage());
            setStatus("Failed to load staff.");
            e.printStackTrace();
        }
    }

    private void fillForm(Staff s) {
        idField.setText(String.valueOf(s.getId()));
        firstNameField.setText(nte(s.getFirstName()));
        lastNameField.setText(nte(s.getLastName()));
        emailField.setText(nte(s.getEmail()));
        passwordField.setText(nte(s.getPassword()));
        salaryField.setText(String.format("%.1f", s.getSalary()));
        birthDatePicker.setValue(s.getBirthDate());
        hireDatePicker.setValue(s.getHireDate());
        statusCombo.getSelectionModel().select(s.getStatus() == null ? "Active" : s.getStatus());
    }

    private void clearForm() {
        staffTable.getSelectionModel().clearSelection();
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        salaryField.clear();
        birthDatePicker.setValue(null);
        hireDatePicker.setValue(LocalDate.now());
        statusCombo.getSelectionModel().select("Active");
    }

    private void setMode(Mode m) {
        mode = m;
        boolean editing = (m == Mode.INSERT || m == Mode.UPDATE);

        idField.setDisable(true);
        firstNameField.setDisable(!editing);
        lastNameField.setDisable(!editing);
        emailField.setDisable(!editing);
        passwordField.setDisable(!editing);
        salaryField.setDisable(!editing);
        birthDatePicker.setDisable(!editing);
        hireDatePicker.setDisable(!editing);
        statusCombo.setDisable(!editing);

        confirmBtn.setVisible(editing);
        confirmBtn.setManaged(editing);
        cancelBtn.setVisible(editing);
        cancelBtn.setManaged(editing);

        if (m == Mode.INSERT) clearForm();
    }


    @FXML
    private void onSearch() {
        String by = searchByCombo.getValue();
        String key = (searchField.getText() == null) ? "" : searchField.getText().trim();

        List<Staff> filtered = new ArrayList<>();
        for (Staff s : masterList) {
            boolean ok;

            if ("ID".equals(by)) {
                ok = key.matches("\\d+") && s.getId() == Integer.parseInt(key);
            } else if ("First Name".equals(by)) {
                ok = containsIgnoreCase(s.getFirstName(), key);
            } else if ("Last Name".equals(by)) {
                ok = containsIgnoreCase(s.getLastName(), key);
            } else if ("Email".equals(by)) {
                ok = containsIgnoreCase(s.getEmail(), key);
            } else if ("Status".equals(by)) {
                ok = s.getStatus() != null && s.getStatus().equalsIgnoreCase(key);
            } else ok = true;

            if (ok) filtered.add(s);
        }

        tableList.setAll(filtered);
        setStatus("Filtered: " + filtered.size());
    }

    @FXML
    private void onRefresh() {
        tableList.setAll(masterList);
        setMode(Mode.VIEW);
        setStatus("Staff refreshed.");
    }

    @FXML
    private void onClear() {
        clearForm();
        setMode(Mode.VIEW);
        setStatus("");
    }


    @FXML
    private void onInsert() {
        setMode(Mode.INSERT);
        setStatus("INSERT mode: fill fields then CONFIRM.");
    }

    @FXML
    private void onUpdate() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus("Select a staff member first."); return; }
        fillForm(selected);
        setMode(Mode.UPDATE);
        setStatus("UPDATE mode: edit fields then CONFIRM.");
    }

    @FXML
    private void onDelete() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus("Select a staff member first."); return; }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Staff");
        alert.setHeaderText("Delete staff ID: " + selected.getId() + "?");
        alert.setContentText("Are you sure? This cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            setStatus("Delete canceled.");
            return;
        }

        try {
            boolean ok = StaffDAO.deleteStaff(selected.getId());
            if (ok) {
                masterList.removeIf(s -> s.getId() == selected.getId());
                tableList.removeIf(s -> s.getId() == selected.getId());
                clearForm();
                setMode(Mode.VIEW);
                setStatus("Deleted successfully.");
            } else setStatus("Delete failed.");
        } catch (Exception e) {
            DBConnection.showError("Delete error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onToggleStatus() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus("Select a staff member first."); return; }

        String newStatus = "Active".equalsIgnoreCase(selected.getStatus()) ? "Inactive" : "Active";

        try {
            boolean ok = StaffDAO.setStaffStatus(selected.getId(), newStatus);
            if (!ok) { setStatus("Failed to update status."); return; }

            Staff updated = new Staff(selected.getId(),
                    selected.getFirstName(), selected.getLastName(),
                    selected.getEmail(), selected.getPassword(),
                    selected.getSalary(), selected.getBirthDate(), selected.getHireDate(),
                    newStatus);

            replaceInLists(updated);
            staffTable.getSelectionModel().select(updated);
            staffTable.refresh();
            setStatus("Status changed to " + newStatus + ".");
        } catch (Exception e) {
            DBConnection.showError("Status update error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onConfirm() {
        if (mode == Mode.INSERT) doInsert();
        else if (mode == Mode.UPDATE) doUpdate();
    }

    @FXML
    private void onCancel() {
        setMode(Mode.VIEW);
        setStatus("Canceled.");
    }

    private void doInsert() {
        Staff built = buildFromForm(0);
        if (built == null) return;

        try {
            int newId = StaffDAO.insertStaff(built);
            if (newId > 0) {
                Staff inserted = new Staff(newId,
                        built.getFirstName(), built.getLastName(),
                        built.getEmail(), built.getPassword(),
                        built.getSalary(), built.getBirthDate(), built.getHireDate(),
                        built.getStatus());

                masterList.add(inserted);
                tableList.add(inserted);
                staffTable.getSelectionModel().select(inserted);
                setMode(Mode.VIEW);
                setStatus("Inserted staff ID = " + newId);
            } else setStatus("Insert failed.");
        } catch (Exception e) {
            DBConnection.showError("Insert error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void doUpdate() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus("Select a staff member first."); return; }

        Staff built = buildFromForm(selected.getId());
        if (built == null) return;

        try {
            boolean ok = StaffDAO.updateStaff(built);
            if (ok) {
                replaceInLists(built);
                staffTable.getSelectionModel().select(built);
                setMode(Mode.VIEW);
                setStatus("Updated successfully.");
            } else setStatus("Update failed.");
        } catch (Exception e) {
            DBConnection.showError("Update error", e.getMessage());
            e.printStackTrace();
        }
    }

    private Staff buildFromForm(int id) {
        String first = firstNameField.getText();
        String last  = lastNameField.getText();
        String email = emailField.getText();
        String pass  = passwordField.getText();
        String salS  = salaryField.getText();
        LocalDate birth = birthDatePicker.getValue();
        LocalDate hire  = hireDatePicker.getValue();
        String status = statusCombo.getValue();

        if (isBlank(first) || isBlank(last) || isBlank(email) || isBlank(pass) || isBlank(salS) || birth == null || hire == null) {
            setStatus("Fill: First, Last, Email, Password, Salary, Birth Date, Hire Date.");
            return null;
        }
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            setStatus("Invalid email format.");
            return null;
        }

        double salary;
        try { salary = Double.parseDouble(salS.trim()); }
        catch (Exception e) { setStatus("Salary must be a number."); return null; }

        if (salary < 0) { setStatus("Salary must be >= 0."); return null; }

        return new Staff(id,
                first.trim(), last.trim(),
                email.trim(), pass,
                salary, birth, hire,
                (status == null || status.isBlank()) ? "Active" : status.trim());
    }

    private void replaceInLists(Staff updated) {
        for (int i = 0; i < masterList.size(); i++) {
            if (masterList.get(i).getId() == updated.getId()) { masterList.set(i, updated); break; }
        }
        for (int i = 0; i < tableList.size(); i++) {
            if (tableList.get(i).getId() == updated.getId()) { tableList.set(i, updated); break; }
        }
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String nte(String s) { return s == null ? "" : s; }

    private boolean containsIgnoreCase(String value, String key) {
        if (key == null || key.isBlank()) return true;
        if (value == null) return false;
        return value.toLowerCase().contains(key.toLowerCase());
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}
