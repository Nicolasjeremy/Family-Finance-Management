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
        String sql = "INSERT INTO Utang(utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor, sisa_utang) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utang.getUtangId());
            pstmt.setString(2, utang.getUserId());
            pstmt.setDouble(3, utang.getJumlah());
            pstmt.setDouble(4, utang.getBunga());
            pstmt.setDate(5, Date.valueOf(utang.getTanggalJatuhTempo()));
            pstmt.setString(6, utang.getStatus());
            pstmt.setString(7, utang.getCreditor());
            pstmt.setDouble(8, utang.getSisaUtang());
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
        String sql = "SELECT utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor, COALESCE(sisa_utang, jumlah) as sisa_utang FROM Utang WHERE user_id = ? ORDER BY tanggal_jatuh_tempo ASC";
        List<Utang> utangList = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                utangList.add(new Utang(
                    rs.getString("utang_id"),
                    rs.getString("user_id"),
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("creditor"),
                    rs.getDouble("sisa_utang")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting Utang for user: " + e.getMessage());
            e.printStackTrace();
        }
        return utangList;
    }

    /**
     * Retrieves all debt records for ALL family members (for Penanggung to see all debts)
     * @return A list of all Utang objects with user information.
     */
    public List<UtangWithUserInfo> getAllFamilyDebts() {
        String sql = "SELECT u.utang_id, u.user_id, u.jumlah, u.bunga, u.tanggal_jatuh_tempo, u.status, u.creditor, " +
                    "COALESCE(u.sisa_utang, u.jumlah) as sisa_utang, p.nama as user_name " +
                    "FROM Utang u " +
                    "JOIN Pengguna p ON u.user_id = p.user_id " +
                    "WHERE p.role = 'Tanggungan' " +
                    "ORDER BY u.tanggal_jatuh_tempo ASC";
        
        List<UtangWithUserInfo> utangList = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UtangWithUserInfo utangInfo = new UtangWithUserInfo(
                    rs.getString("utang_id"),
                    rs.getString("user_id"),
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("creditor"),
                    rs.getDouble("sisa_utang"),
                    rs.getString("user_name")
                );
                utangList.add(utangInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all family debts: " + e.getMessage());
            e.printStackTrace();
        }
        return utangList;
    }

    /**
     * Update pembayaran utang (hanya bisa dilakukan oleh Penanggung)
     */
    public boolean updatePayment(String utangId, double paymentAmount, String notes) {
        String sql = "UPDATE Utang SET sisa_utang = sisa_utang - ?, status = CASE WHEN (sisa_utang - ?) <= 0 THEN 'Lunas' WHEN (sisa_utang - ?) < jumlah THEN 'Sebagian Lunas' ELSE status END WHERE utang_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, paymentAmount);
            pstmt.setDouble(2, paymentAmount);
            pstmt.setDouble(3, paymentAmount);
            pstmt.setString(4, utangId);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Insert ke tabel history pembayaran jika diperlukan
                insertPaymentHistory(utangId, paymentAmount, notes);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void insertPaymentHistory(String utangId, double amount, String notes) {
        String sql = "INSERT INTO payment_history(utang_id, payment_amount, payment_date, notes) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utangId);
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            pstmt.setString(4, notes);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting payment history: " + e.getMessage());
        }
    }

    /**
     * Get debt by ID untuk keperluan pembayaran
     */
    public Utang getUtangById(String utangId) {
        String sql = "SELECT utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor, COALESCE(sisa_utang, jumlah) as sisa_utang FROM Utang WHERE utang_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utangId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Utang(
                    rs.getString("utang_id"),
                    rs.getString("user_id"),
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("creditor"),
                    rs.getDouble("sisa_utang")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting Utang by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inner class untuk menyimpan informasi utang beserta nama user
     */
    public static class UtangWithUserInfo extends Utang {
        private String userName;

        public UtangWithUserInfo(String utangId, String userId, double jumlah, double bunga, 
                                LocalDate tanggalJatuhTempo, String status, String creditor, 
                                double sisaUtang, String userName) {
            super(utangId, userId, jumlah, bunga, tanggalJatuhTempo, status, creditor, sisaUtang);
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}