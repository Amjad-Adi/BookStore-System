package com.example.database.Staff;

import com.example.database.AESCrypto;
import com.example.database.DBConnection;

import java.time.LocalDate;

public class Staff {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private double salary;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String status;

    public Staff(String firstName, String lastName, String email, String password,
                 double salary, LocalDate birthDate, LocalDate hireDate, String status) {
        this(0, firstName, lastName, email, password, salary, birthDate, hireDate, status);
    }

    public Staff(int id, String firstName, String lastName, String email, String password,
                 double salary, LocalDate birthDate, LocalDate hireDate, String status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        setPassword(password);
        this.salary    = salary;
        this.birthDate = birthDate;
        this.hireDate  = hireDate;
        this.status    = status;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getSalary() { return salary; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDate getHireDate() { return hireDate; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
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

    public void setSalary(double salary) { this.salary = salary; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return firstName + " " + lastName;  // Display First and Last Name in ComboBox
    }
}
