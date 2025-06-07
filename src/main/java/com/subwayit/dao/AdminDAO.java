package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Admin;
import com.subwayit.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    private UserDAO userDAO;

    public AdminDAO() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inserts a new Admin into the Pengguna and Admin tables.
     * @param admin The Admin object to insert.
     */
    public void addAdmin(Admin admin) {
        // First, add the user part to the Pengguna table
        userDAO.addUser(admin);

        String sql = "INSERT INTO Admin(admin_id) VALUES(?)"; // Assuming only admin_id is specific to Admin table
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getAdminId()); // Use adminId from Admin object
            pstmt.executeUpdate();
            System.out.println("Admin added successfully: " + admin.getNama());
        } catch (SQLException e) {
            System.err.println("Error adding Admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an Admin from the database by their user_id (which is often also the admin_id).
     * @param adminId The ID of the Admin to retrieve.
     * @return The Admin object if found, null otherwise.
     */
    public Admin getAdminById(String adminId) {
        // First, get the User portion
        User user = userDAO.getUserByUserId(adminId);
        if (user == null || !"Admin".equals(user.getRole())) {
            return null; // Not found or not an Admin
        }

        // If the Admin table only contains admin_id (which is also user_id), no separate query is strictly needed
        // unless there are admin-specific columns. The document lists adminID and email for Admin class attributes.
        // Email is in User. adminID is the only distinct one.
        // Assuming admin_id in Admin table is same as user_id.
        String sql = "SELECT admin_id FROM Admin WHERE admin_id = ?";
        Admin admin = null;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                admin = new Admin(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("admin_id") // Get admin_id from the Admin table
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting Admin by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return admin;
    }


    /**
     * Updates an existing Admin's information in both Pengguna and Admin tables.
     * @param admin The Admin object with updated information.
     */
    public void updateAdmin(Admin admin) {
        // First, update the user part in the Pengguna table
        userDAO.updateUser(admin);

        // If Admin table has no other specific updateable fields beyond admin_id (which matches user_id),
        // then no separate update statement for Admin table is strictly needed after userDAO.updateUser.
        // But if there were, you'd add:
        /*
        String sql = "UPDATE Admin SET some_admin_specific_field = ? WHERE admin_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getSomeAdminSpecificField());
            pstmt.setString(2, admin.getAdminId());
            pstmt.executeUpdate();
            System.out.println("Admin-specific fields updated.");
        } catch (SQLException e) {
            System.err.println("Error updating Admin-specific fields: " + e.getMessage());
            e.printStackTrace();
        }
        */
        System.out.println("Admin updated successfully: " + admin.getNama());
    }

    /**
     * Deletes an Admin from both Admin and Pengguna tables.
     * @param adminId The ID of the Admin to delete.
     */
    public void deleteAdmin(String adminId) {
        String sql = "DELETE FROM Admin WHERE admin_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Admin deleted successfully from Admin table: " + adminId);
                // Then delete from the parent Pengguna table
                userDAO.deleteUser(adminId);
            } else {
                System.out.println("Admin not found for deletion: " + adminId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting Admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}