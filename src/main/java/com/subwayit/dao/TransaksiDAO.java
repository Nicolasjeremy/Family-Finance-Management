package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Transaksi;

import java.sql.Connection;
import java.sql.Date; // For converting LocalDate to java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    /**
     * Inserts a new transaction into the Transaksi table.
     * @param transaksi The Transaksi object to insert.
     */
    public void addTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO Transaksi(transaksi_id, user_id, jenis, kategori, nominal, tanggal_transaksi, bukti_transaksi, is_rutin, deskripsi) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksi.getTransaksiId());
            pstmt.setString(2, transaksi.getUserId());
            pstmt.setString(3, transaksi.getJenis());
            pstmt.setString(4, transaksi.getKategori());
            pstmt.setDouble(5, transaksi.getNominal());
            pstmt.setDate(6, Date.valueOf(transaksi.getTanggalTransaksi())); // Convert LocalDate to java.sql.Date
            pstmt.setString(7, transaksi.getBuktiTransaksi());
            pstmt.setBoolean(8, transaksi.isRutin());
            pstmt.setString(9, transaksi.getDeskripsi());
            pstmt.executeUpdate();
            System.out.println("Transaction added successfully: " + transaksi.getDeskripsi());
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all transactions for a specific user.
     * @param userId The ID of the user whose transactions to retrieve.
     * @return A list of Transaksi objects.
     */
    public List<Transaksi> getAllTransactionsForUser(String userId) {
        String sql = "SELECT transaksi_id, user_id, jenis, kategori, nominal, tanggal_transaksi, bukti_transaksi, is_rutin, deskripsi FROM Transaksi WHERE user_id = ? ORDER BY tanggal_transaksi DESC";
        List<Transaksi> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaksi(
                        rs.getString("transaksi_id"),
                        rs.getString("user_id"),
                        rs.getString("jenis"),
                        rs.getString("kategori"),
                        rs.getDouble("nominal"),
                        rs.getDate("tanggal_transaksi").toLocalDate(), // Convert java.sql.Date to LocalDate
                        rs.getString("bukti_transaksi"),
                        rs.getBoolean("is_rutin"),
                        rs.getString("deskripsi")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions for user: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    // Add update and delete methods for Transaksi later.
}