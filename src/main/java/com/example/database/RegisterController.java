package com.example.database;

import com.example.database.Customer.Customer;
import com.example.database.Customer.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField secondNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> professionCombo;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        professionCombo.setItems(FXCollections.observableArrayList(
                "Engineer","Developer","Teacher","Student","Doctor","Nurse","Pharmacist","Accountant",
                "Business Owner","Designer","Graphic Designer","Journalist","Architect","Researcher","Other"
        ));
        professionCombo.getSelectionModel().select("Other");
        statusLabel.setText("");
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        String first  = txt(firstNameField);
        String second = txt(secondNameField);
        String email  = txt(emailField);
        String pass   = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (first.isEmpty() || second.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Fill: First name, Second name, Email, Password.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            statusLabel.setText("Invalid email format.");
            return;
        }

        String profession = (professionCombo.getValue() == null) ? "Other" : professionCombo.getValue();

        try {
            LocalDate now = LocalDate.now();

            Customer c = new Customer(0, first, second, email, pass, profession, birthDatePicker.getValue(), now, null,  null,0.0,false);

            int newId = CustomerDAO.insertCustomer(c);

            if (newId > 0) {
                statusLabel.setText("Account created. Please login.");
                SceneUtil.switchTo(event, "login.fxml", "Login");
            } else {
                statusLabel.setText("Register failed.");
            }

        } catch (Exception e) {
            statusLabel.setText("Register error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGoLogin(ActionEvent event) {
        SceneUtil.switchTo(event, "login.fxml", "Login"); // ✅ fixed
    }

    private String txt(TextField t) {
        return t.getText() == null ? "" : t.getText().trim();
    }
}
