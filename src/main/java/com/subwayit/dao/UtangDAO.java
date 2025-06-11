package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Utang;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtangDAO {

    // Inner class to hold combined Utang and User info
    public static class UtangWithUserInfo extends Utang {
        private String userName;

        public UtangWithUserInfo(String utangId, String userId, double jumlah, double bunga,
                                 LocalDate tanggalJatuhTempo, String status, String creditor,
                                 double sisaUtang, String userName) {
            super(utangId, userId, jumlah, bunga, tanggalJatuhTempo, status, creditor, sisaUtang);
            this.userName = userName;
        }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

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
            pstmt.setDouble(8, utang.getTotalWithInterest()); // Initial remaining amount includes interest
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding debt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateUtang(Utang utang) {
        String sql = "UPDATE Utang SET jumlah = ?, bunga = ?, tanggal_jatuh_tempo = ?, status = ?, creditor = ?, sisa_utang = ? WHERE utang_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, utang.getJumlah());
            pstmt.setDouble(2, utang.getBunga());
            pstmt.setDate(3, Date.valueOf(utang.getTanggalJatuhTempo()));
            pstmt.setString(4, utang.getStatus());
            pstmt.setString(5, utang.getCreditor());
            pstmt.setDouble(6, utang.getSisaUtang());
            pstmt.setString(7, utang.getUtangId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePayment(String utangId, double paymentAmount) {
        String sql = "UPDATE Utang SET sisa_utang = sisa_utang - ? WHERE utang_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, paymentAmount);
            pstmt.setString(2, utangId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                // After payment, check if the debt is now fully paid and update status
                String checkSql = "UPDATE Utang SET status = 'Lunas' WHERE utang_id = ? AND sisa_utang <= 0";
                try(PreparedStatement checkPstmt = conn.prepareStatement(checkSql)){
                    checkPstmt.setString(1, utangId);
                    checkPstmt.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteUtang(String utangId) {
        String sql = "DELETE FROM Utang WHERE utang_id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utangId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all debts for a list of user IDs, joining with the Pengguna table to get user names.
     * This is the primary method used by the DebtPage.
     */
    public List<UtangWithUserInfo> getDebtsWithUserInfo(List<String> userIds) {
        List<UtangWithUserInfo> debts = new ArrayList<>();
        if (userIds == null || userIds.isEmpty()) {
            return debts;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(userIds.size(), "?"));
        String sql = "SELECT u.*, p.nama as user_name FROM Utang u " +
                     "JOIN Pengguna p ON u.user_id = p.user_id " +
                     "WHERE u.user_id IN (" + placeholders + ") ORDER BY u.tanggal_jatuh_tempo ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < userIds.size(); i++) {
                pstmt.setString(i + 1, userIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                debts.add(new UtangWithUserInfo(
                    rs.getString("utang_id"),
                    rs.getString("user_id"),
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("creditor"),
                    rs.getDouble("sisa_utang"),
                    rs.getString("user_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return debts;
    }
}