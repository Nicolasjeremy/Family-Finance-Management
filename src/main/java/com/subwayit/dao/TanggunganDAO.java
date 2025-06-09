package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TanggunganDAO {

    private UserDAO userDAO;

    public TanggunganDAO() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inserts a new Tanggungan into the Pengguna and Tanggungan tables.
     * @param tanggungan The Tanggungan object to insert.
     */
    public void addTanggungan(Tanggungan tanggungan) {
        // First, add the user part to the Pengguna table
        userDAO.addUser(tanggungan);

        String sql = "INSERT INTO Tanggungan(tanggungan_id, posisi, nama, umur, pendidikan, pekerjaan) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tanggungan.getUserId()); // Use userId as tanggungan_id
            pstmt.setString(2, tanggungan.getPosisiKeluarga());
            pstmt.setString(3, tanggungan.getNama()); // As per doc, nama in Tanggungan table [cite: 153]
            pstmt.setInt(4, tanggungan.getUmur());   // As per doc, umur in Tanggungan table [cite: 153]
            pstmt.setString(5, tanggungan.getPendidikan());
            pstmt.setString(6, tanggungan.getPekerjaan());
            pstmt.executeUpdate();
            System.out.println("Tanggungan added successfully: " + tanggungan.getNama());
        } catch (SQLException e) {
            System.err.println("Error adding Tanggungan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a Tanggungan from the database by their ID.
     * @param tanggunganId The ID of the Tanggungan to retrieve.
     * @return The Tanggungan object if found, null otherwise.
     */
    public Tanggungan getTanggunganById(String tanggunganId) {
        // First, get the User portion
        User user = userDAO.getUserByUserId(tanggunganId);
        if (user == null || !"Tanggungan".equals(user.getRole())) {
            return null; // Not found or not a Tanggungan
        }

        String sql = "SELECT posisi, nama, umur, pendidikan, pekerjaan FROM Tanggungan WHERE tanggungan_id = ?";
        Tanggungan tanggungan = null;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tanggunganId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tanggungan = new Tanggungan(
                        user.getUserId(),
                        user.getNama(), // Get from User object or rs.getString("nama")
                        user.getUmur(),   // Get from User object or rs.getInt("umur")
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("posisi"),
                        rs.getString("pendidikan"),
                        rs.getString("pekerjaan")
                );
                // Re-set name and umur from Tanggungan table if they differ (doc seems to have them on both)
                tanggungan.setNama(rs.getString("nama"));
                tanggungan.setUmur(rs.getInt("umur"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting Tanggungan by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return tanggungan;
    }

    /**
     * Updates an existing Tanggungan's information in both Pengguna and Tanggungan tables.
     * @param tanggungan The Tanggungan object with updated information.
     */
    public void updateTanggungan(Tanggungan tanggungan) {
        // First, update the user part in the Pengguna table
        userDAO.updateUser(tanggungan);

        String sql = "UPDATE Tanggungan SET posisi = ?, nama = ?, umur = ?, pendidikan = ?, pekerjaan = ? WHERE tanggungan_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tanggungan.getPosisiKeluarga());
            pstmt.setString(2, tanggungan.getNama());
            pstmt.setInt(3, tanggungan.getUmur());
            pstmt.setString(4, tanggungan.getPendidikan());
            pstmt.setString(5, tanggungan.getPekerjaan());
            pstmt.setString(6, tanggungan.getUserId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Tanggungan updated successfully: " + tanggungan.getNama());
            } else {
                System.out.println("Tanggungan not found for update: " + tanggungan.getNama());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Tanggungan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a Tanggungan from both Tanggungan and Pengguna tables.
     * @param tanggunganId The ID of the Tanggungan to delete.
     */
    public void deleteTanggungan(String tanggunganId) {
        String sql = "DELETE FROM Tanggungan WHERE tanggungan_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tanggunganId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Tanggungan deleted successfully from Tanggungan table: " + tanggunganId);
                // Then delete from the parent Pengguna table
                userDAO.deleteUser(tanggunganId);
            } else {
                System.out.println("Tanggungan not found for deletion: " + tanggunganId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting Tanggungan: " + e.getMessage());
            e.printStackTrace();
        }
    }

        public List<Tanggungan> getAllTanggunan() {
        String sql = "SELECT tanggungan_id, posisi, nama, umur, pendidikan, pekerjaan FROM Tanggungan";
        List<Tanggungan> tanggungans = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement(); // Make sure java.sql.Statement is imported
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // You'll need to fetch the User details for these Tanggungan objects
                // A simpler way is to fetch user first, then Tanggungan specific details.
                // Or, if UserDAO has a getUserById method:
                User user = new UserDAO().getUserByUserId(rs.getString("tanggungan_id"));
                if (user != null) {
                     Tanggungan tanggungan = new Tanggungan(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("posisi"),
                        rs.getString("pendidikan"),
                        rs.getString("pekerjaan")
                     );
                     tanggungans.add(tanggungan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all Tanggungan: " + e.getMessage());
            e.printStackTrace();
        }
        return tanggungans;
    }


}