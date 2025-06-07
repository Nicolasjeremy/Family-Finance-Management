package com.subwayit.model;

/**
 * Represents an administrator of the SubwayIT system.
 * Extends the base User class.
 */
public class Admin extends User {
    // Attributes specific to Admin, based on Section 2.3.4 of the document
    private String adminId; // Document lists adminID[cite: 158], but userId from parent can also serve this.
                            // Keeping for direct mapping to doc.

    // Constructor
    // Calls the superclass (User) constructor first
    public Admin(String userId, String nama, int umur, String email, String password, String adminId) {
        // Admin role is fixed
        super(userId, nama, umur, email, password, "Admin");
        this.adminId = adminId;
    }

    // --- Operations (Methods) specific to Admin, based on Section 2.3.4 of the document ---

    /**
     * Allows the admin to view all users in the system. [cite: 155]
     */
    public void viewAllUsers() {
        System.out.println(this.getNama() + " (Admin) is viewing all users.");
        // Actual implementation will involve interacting with UserDAO
    }

    /**
     * Allows the admin to manage user access (e.g., changing roles, permissions). [cite: 158]
     */
    public void manageAccess() {
        System.out.println(this.getNama() + " (Admin) is managing user access.");
        // Actual implementation will involve modifying user roles/permissions
    }

    // --- Getters and Setters for Admin-specific Attributes ---

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}