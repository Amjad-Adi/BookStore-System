package com.example.database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void onLogin(ActionEvent event) {
        String email = (emailField.getText() == null) ? "" : emailField.getText().trim();
        String pass  = (passwordField.getText() == null) ? "" : passwordField.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Enter email and password.");
            return;
        }
        if ("hi".equals(email)) {
            Session.setStaffSession(new AuthDAO.StaffSession(0, "Admin", "hi", pass));  // Dummy admin session, no password check
            statusLabel.setText("Welcome Admin");
            SceneUtil.switchTo(event, "main_layout.fxml", "Book Store - Admin");
            return;
        }
        try {
            String key = AESCrypto.sha256(DBConnection.id).substring(0, 16);  // 16 bytes key from SHA-256
            String iv = AESCrypto.md5(DBConnection.id).substring(0, 16);  // 16 bytes IV from MD5

            AuthDAO.CustomerSession customerSession = AuthDAO.loginCustomer(email);
            if (customerSession != null) {
                String decryptedCustomerPassword = AESCrypto.aesDecrypt(customerSession.encryptedPassword, key, iv);

                if (decryptedCustomerPassword.equals(pass)) {
                    Session.setCustomerSession(customerSession);
                    statusLabel.setText("Welcome " + customerSession.fullName);
                    SceneUtil.switchTo(event, "customer_page_1.fxml", "Book Store - Customer");
                    return;
                } else {
                    statusLabel.setText("Invalid credentials.");
                }
            }

            AuthDAO.StaffSession staffSession = AuthDAO.loginStaff(email);
            if (staffSession != null) {
                String decryptedStaffPassword = AESCrypto.aesDecrypt(staffSession.encryptedPassword, key, iv);

                if (decryptedStaffPassword.equals(pass)) {
                    Session.setStaffSession(staffSession);
                    statusLabel.setText("Welcome " + staffSession.role + ": " + staffSession.fullName);
                    SceneUtil.switchTo(event, "main_layout.fxml", "Book Store - " + staffSession.role);
                    return;
                } else {
                    statusLabel.setText("Invalid credentials.");
                }
            }

            statusLabel.setText("Invalid credentials.");

        } catch (Exception e) {
            statusLabel.setText("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void onGoRegister(ActionEvent event) {
        SceneUtil.switchTo(event, "register.fxml", "Register");
    }
}
