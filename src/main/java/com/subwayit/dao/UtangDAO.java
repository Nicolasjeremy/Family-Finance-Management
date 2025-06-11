package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Utang;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtangDAO {

    /**
     * Inserts a new debt record into the Utang table.
     * @param utang The Utang object to insert.
     */
    public void addUtang(Utang utang) {
        String sql = "INSERT INTO Utang(utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utang.getUtangId());
            pstmt.setString(2, utang.getUserId()); // Gunakan getUserId() yang baru
            pstmt.setDouble(3, utang.getJumlah());
            pstmt.setDouble(4, utang.getBunga());
            pstmt.setDate(5, Date.valueOf(utang.getTanggalJatuhTempo())); // Convert LocalDate to java.sql.Date
            pstmt.setString(6, utang.getStatus());
            pstmt.setString(7, utang.getCreditor());
            pstmt.executeUpdate();
            System.out.println("Debt added successfully for creditor: " + utang.getCreditor());
        } catch (SQLException e) {
            System.err.println("Error adding debt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all debt records for a specific Tanggungan.
     * Only Tanggungan users are responsible for debts in your schema.
     * @param userId The ID of the user whose debts to retrieve.
     * @return A list of Utang objects.
     */
    public List<Utang> getAllUtangForTanggungan(String userId) {
        String sql = "SELECT utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor FROM Utang WHERE user_id = ? ORDER BY tanggal_jatuh_tempo ASC";
        List<Utang> utangList = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                utangList.add(new Utang(
                    rs.getString("utang_id"),
                    rs.getString("user_id"), // Gunakan user_id dari database
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo").toLocalDate(), // Convert java.sql.Date to LocalDate
                    rs.getString("status"),
                    rs.getString("creditor")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting Utang for user: " + e.getMessage());
            e.printStackTrace();
        }
        return utangList;
    }

    // You can add update, delete, and getById methods for Utang later if needed.
}