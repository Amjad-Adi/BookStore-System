package com.example.database.Customer;
import com.example.database.AESCrypto;
import com.example.database.DBConnection;

import java.time.LocalDate;
import java.security.NoSuchAlgorithmException;

public class Customer {
    private int id;
    private String firstName;
    private String secondName;
    private String email;
    private String password;
    private String profession;
    private LocalDate birthDate;
    private LocalDate registerDate;
    private LocalDate activationDate;
    private LocalDate expirationDate;
    private Double budgetInDollars;
    private boolean isDisabled;

    public Customer(int id, String firstName, String secondName, String email, String password, String profession, LocalDate birthDate, LocalDate registerDate, LocalDate activationDate, LocalDate expirationDate, double budgetInDollars, boolean isDisabled) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        setPassword(password);
        this.profession = profession;
        this.birthDate = birthDate;
        this.registerDate = registerDate;
        this.activationDate = activationDate;
        this.expirationDate = expirationDate;
        this.budgetInDollars = budgetInDollars;
        this.isDisabled = isDisabled;
    }


    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getSecondName() { return secondName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfession() { return profession; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDate getRegisterDate() { return registerDate; }
    public LocalDate getActivationDate() { return activationDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public Double getBudgetInDollars() { return budgetInDollars; }
    public boolean isDisabled() { return isDisabled; }


    public void setId(int id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }
    public void setEmail(String email) { this.email = email; }


    public void setPassword(String password) {
        try {
            String key = AESCrypto.sha256(DBConnection.id).substring(0, 16);
            String iv = AESCrypto.md5(DBConnection.id).substring(0, 16);
            this.password = AESCrypto.aesEncrypt(password, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProfession(String profession) { this.profession = profession; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setRegisterDate(LocalDate registerDate) { this.registerDate = registerDate; }
    public void setActivationDate(LocalDate activationDate) { this.activationDate = activationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public void setBudgetInDollars(double budgetInDollars) { this.budgetInDollars = budgetInDollars; }
    public void setDisabled(boolean isDisabled) { this.isDisabled = isDisabled; }

    @Override
    public String toString() {
        return firstName + " " + secondName;
    }
}
