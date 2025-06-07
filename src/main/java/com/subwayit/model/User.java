package com.subwayit.model;

/**
 * Represents a generic user in the SubwayIT financial management system.
 * This class serves as the base (parent) for specific user roles like Penanggung, Tanggungan, and Admin.
 */
public class User {
    // Attributes based on Section 2.3.1 of the document [cite: 146]
    private String userId;
    private String nama;
    private int umur;
    private String email;
    private String password;
    private String role; // e.g., "Penanggung", "Tanggungan", "Admin" [cite: 146]

    // Constructor
    public User(String userId, String nama, int umur, String email, String password, String role) {
        this.userId = userId;
        this.nama = nama;
        this.umur = umur;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- Operations (Methods) based on Section 2.3.1 of the document ---

    /**
     * Authenticates and logs the user into the system. [cite: 146]
     * @return true if login is successful, false otherwise.
     */
    public boolean login() {
        // Placeholder for login logic (e.g., check credentials against database)
        System.out.println(this.nama + " is attempting to log in.");
        // Actual implementation will involve checking password, user ID, etc.
        return false;
    }

    /**
     * Logs the user out of the system. [cite: 146]
     */
    public void logout() {
        System.out.println(this.nama + " has logged out.");
    }

    /**
     * Allows the user to view their compiled report (income, expenses, financial ratio). [cite: 146]
     * This method will likely call into a reporting service or controller.
     */
    public void viewReport() {
        System.out.println(this.nama + " is viewing a financial report.");
    }

    /**
     * Allows the user to fill/update their personal data. [cite: 146]
     */
    public void fillPersonalData() {
        System.out.println(this.nama + " is filling/updating personal data.");
    }

    // --- Getters and Setters for Attributes ---

    public String getUserId() {
        return userId;
    }

    // Setter for userId is usually not needed as it's typically set during creation.
    // public void setUserId(String userId) { this.userId = userId; }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}