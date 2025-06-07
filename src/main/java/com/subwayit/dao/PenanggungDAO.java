package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Penanggung;
import com.subwayit.model.User; // Important for managing the parent User attributes

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PenanggungDAO {

    private UserDAO userDAO; // To handle the User portion of Penanggung

    public PenanggungDAO() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inserts a new Penanggung into the Pengguna and Penanggung tables.
     * This handles the inheritance relationship.
     * @param penanggung The Penanggung object to insert.
     */
    public void addPenanggung(Penanggung penanggung) {
        // First, add the user part to the Pengguna table
        userDAO.addUser(penanggung); // Pass Penanggung as a User object

        String sql = "INSERT INTO Penanggung(penanggung_id, jumlah_pemasukan, jumlah_pengeluaran) VALUES(?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, penanggung.getUserId()); // Use userId as penanggung_id
            pstmt.setInt(2, penanggung.getJumlahPemasukan());
            pstmt.setInt(3, penanggung.getJumlahPengeluaran());
            pstmt.executeUpdate();
            System.out.println("Penanggung added successfully: " + penanggung.getNama());
        } catch (SQLException e) {
            System.err.println("Error adding Penanggung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a Penanggung from the database by their ID.
     * @param penanggungId The ID of the Penanggung to retrieve.
     * @return The Penanggung object if found, null otherwise.
     */
    public Penanggung getPenanggungById(String penanggungId) {
        // First, get the User portion
        User user = userDAO.getUserByUserId(penanggungId);
        if (user == null || !"Penanggung".equals(user.getRole())) {
            return null; // Not found or not a Penanggung
        }

        String sql = "SELECT jumlah_pemasukan, jumlah_pengeluaran FROM Penanggung WHERE penanggung_id = ?";
        Penanggung penanggung = null;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, penanggungId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                penanggung = new Penanggung(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword()
                );
                penanggung.setJumlahPemasukan(rs.getInt("jumlah_pemasukan"));
                penanggung.setJumlahPengeluaran(rs.getInt("jumlah_pengeluaran"));
                // Note: anggotaTanggungan is not handled here, as it's a list.
                // It would require a separate query/DAO to fetch dependents.
            }
        } catch (SQLException e) {
            System.err.println("Error getting Penanggung by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return penanggung;
    }

    /**
     * Updates an existing Penanggung's information in both Pengguna and Penanggung tables.
     * @param penanggung The Penanggung object with updated information.
     */
    public void updatePenanggung(Penanggung penanggung) {
        // First, update the user part in the Pengguna table
        userDAO.updateUser(penanggung);

        String sql = "UPDATE Penanggung SET jumlah_pemasukan = ?, jumlah_pengeluaran = ? WHERE penanggung_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, penanggung.getJumlahPemasukan());
            pstmt.setInt(2, penanggung.getJumlahPengeluaran());
            pstmt.setString(3, penanggung.getUserId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Penanggung updated successfully: " + penanggung.getNama());
            } else {
                System.out.println("Penanggung not found for update: " + penanggung.getNama());
            }
        } catch (SQLException e) {
            System.err.println("Error updating Penanggung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a Penanggung from both Penanggung and Pengguna tables.
     * @param penanggungId The ID of the Penanggung to delete.
     */
    public void deletePenanggung(String penanggungId) {
        String sql = "DELETE FROM Penanggung WHERE penanggung_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, penanggungId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Penanggung deleted successfully from Penanggung table: " + penanggungId);
                // Then delete from the parent Pengguna table
                userDAO.deleteUser(penanggungId);
            } else {
                System.out.println("Penanggung not found for deletion: " + penanggungId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting Penanggung: " + e.getMessage());
            e.printStackTrace();
        }
    }
}