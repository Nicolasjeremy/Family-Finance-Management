package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Transaksi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <--- IMPORTANT: Ensure this is imported
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk mengelola operasi CRUD terhadap tabel Transaksi.
 */
public class TransaksiDAO {

    /**
     * Inserts a new transaction into the Transaksi table.
     */
    public void addTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO Transaksi(transaksi_id, user_id, jenis, kategori, nominal, tanggal_transaksi, bukti_transaksi, is_rutin, deskripsi) "
                   + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksi.getTransaksiId());
            pstmt.setString(2, transaksi.getUserId());
            pstmt.setString(3, transaksi.getJenis());
            pstmt.setString(4, transaksi.getKategori());
            pstmt.setDouble(5, transaksi.getNominal());
            pstmt.setDate(6, Date.valueOf(transaksi.getTanggalTransaksi()));
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
     * Retrieves all transactions for a specific user, ordered by date descending.
     */
    public List<Transaksi> getAllTransactionsForUser(String userId) {
        String sql = "SELECT transaksi_id, user_id, jenis, kategori, nominal, tanggal_transaksi, bukti_transaksi, is_rutin, deskripsi "
                   + "FROM Transaksi WHERE user_id = ? ORDER BY tanggal_transaksi DESC";
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
                    rs.getDate("tanggal_transaksi").toLocalDate(),
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

    /**
     * Retrieves ALL transactions from the Transaksi table, ordered by date descending.
     * This method is used by Penanggung to see all transactions (simplified for now).
     * @return A list of all Transaksi objects.
     */
    public List<Transaksi> getAllTransactions() { // <--- This is the method that was missing
        String sql = "SELECT transaksi_id, user_id, jenis, kategori, nominal, tanggal_transaksi, bukti_transaksi, is_rutin, deskripsi "
                   + "FROM Transaksi ORDER BY tanggal_transaksi DESC";
        List<Transaksi> transactions = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement(); // Use Statement as there are no parameters
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaksi(
                    rs.getString("transaksi_id"),
                    rs.getString("user_id"),
                    rs.getString("jenis"),
                    rs.getString("kategori"),
                    rs.getDouble("nominal"),
                    rs.getDate("tanggal_transaksi").toLocalDate(),
                    rs.getString("bukti_transaksi"),
                    rs.getBoolean("is_rutin"),
                    rs.getString("deskripsi")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaksi> getTransactionsForMultipleUsers(List<String> userIds) {
    List<Transaksi> transactions = new ArrayList<>();
    if (userIds == null || userIds.isEmpty()) {
        return transactions; // Return empty list if no users are provided
    }

    // Build a dynamic query with the correct number of placeholders for the IN clause
    StringBuilder placeholders = new StringBuilder();
    for (int i = 0; i < userIds.size(); i++) {
        placeholders.append("?");
        if (i < userIds.size() - 1) {
            placeholders.append(",");
        }
    }

    String sql = "SELECT * FROM Transaksi WHERE user_id IN (" + placeholders.toString() + ") ORDER BY tanggal_transaksi DESC";

    try (Connection conn = DatabaseManager.connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Set the user IDs as parameters for the PreparedStatement
        for (int i = 0; i < userIds.size(); i++) {
            pstmt.setString(i + 1, userIds.get(i));
        }

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            transactions.add(new Transaksi(
                rs.getString("transaksi_id"),
                rs.getString("user_id"),
                rs.getString("jenis"),
                rs.getString("kategori"),
                rs.getDouble("nominal"),
                rs.getDate("tanggal_transaksi").toLocalDate(),
                rs.getString("bukti_transaksi"),
                rs.getBoolean("is_rutin"),
                rs.getString("deskripsi")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error getting transactions for multiple users: " + e.getMessage());
        e.printStackTrace();
    }
    return transactions;
}

    /**
     * Updates an existing transaction in the Transaksi table.
     * Matches by transaksi_id.
     */
    public void updateTransaksi(Transaksi transaksi) {
        String sql = "UPDATE Transaksi SET jenis = ?, kategori = ?, nominal = ?, tanggal_transaksi = ?, "
                   + "bukti_transaksi = ?, is_rutin = ?, deskripsi = ? WHERE transaksi_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksi.getJenis());
            pstmt.setString(2, transaksi.getKategori());
            pstmt.setDouble(3, transaksi.getNominal());
            pstmt.setDate(4, Date.valueOf(transaksi.getTanggalTransaksi()));
            pstmt.setString(5, transaksi.getBuktiTransaksi());
            pstmt.setBoolean(6, transaksi.isRutin());
            pstmt.setString(7, transaksi.getDeskripsi());
            pstmt.setString(8, transaksi.getTransaksiId());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Transaction updated successfully: " + transaksi.getTransaksiId());
            } else {
                System.out.println("No transaction found with ID: " + transaksi.getTransaksiId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a transaction from the Transaksi table by its ID.
     */
    public void deleteTransaksi(String transaksiId) {
        String sql = "DELETE FROM Transaksi WHERE transaksi_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksiId);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Transaction deleted successfully: " + transaksiId);
            } else {
                System.out.println("No transaction found with ID: " + transaksiId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}