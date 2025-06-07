package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class UserDAO {

    /**
     * Inserts a new user into the Pengguna table.
     * @param user The User object to insert.
     */
    public void addUser(User user) {
        String sql = "INSERT INTO Pengguna(user_id, nama, umur, e_mail, password, role) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNama());
            pstmt.setInt(3, user.getUmur());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            pstmt.executeUpdate();
            System.out.println("User added successfully: " + user.getNama());
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a user from the Pengguna table by their user_id.
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, null otherwise.
     */
    public User getUserByUserId(String userId) {
        String sql = "SELECT user_id, nama, umur, e_mail, password, role FROM Pengguna WHERE user_id = ?";
        User user = null;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("user_id"),
                        rs.getString("nama"),
                        rs.getInt("umur"),
                        rs.getString("e_mail"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Updates an existing user's information in the Pengguna table.
     * @param user The User object with updated information.
     */
    public void updateUser(User user) {
        String sql = "UPDATE Pengguna SET nama = ?, umur = ?, e_mail = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNama());
            pstmt.setInt(2, user.getUmur());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getUserId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User updated successfully: " + user.getNama());
            } else {
                System.out.println("User not found for update: " + user.getNama());
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the Pengguna table by their user_id.
     * @param userId The ID of the user to delete.
     */
    public void deleteUser(String userId) {
        String sql = "DELETE FROM Pengguna WHERE user_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted successfully: " + userId);
            } else {
                System.out.println("User not found for deletion: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all users from the Pengguna table.
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        String sql = "SELECT user_id, nama, umur, e_mail, password, role FROM Pengguna";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("nama"),
                        rs.getInt("umur"),
                        rs.getString("e_mail"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
}