package com.subwayit.dao;

import com.subwayit.database.DatabaseManager;
import com.subwayit.model.Utang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UtangDAO {

    /**
     * Menyimpan data utang baru ke dalam database.
     * @param utang Objek Utang yang akan disimpan.
     */
    public void addUtang(Utang utang) {
        String sql = "INSERT INTO Utang(utang_id, user_id, jumlah, bunga, tanggal_jatuh_tempo, status, creditor) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, utang.getUtangId());
            pstmt.setString(2, utang.getUserId());
            pstmt.setDouble(3, utang.getJumlah());
            pstmt.setDouble(4, utang.getBunga());
            pstmt.setDate(5, new java.sql.Date(utang.getTanggalJatuhTempo().getTime())); 
            pstmt.setString(6, utang.getStatus());
            pstmt.setString(7, utang.getCreditor());
            
            pstmt.executeUpdate();
            System.out.println("Utang baru berhasil ditambahkan.");
            
        } catch (SQLException e) {
            System.err.println("Error menambahkan utang: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Mengambil semua utang dari seorang pengguna berdasarkan user ID.
     * @param userId ID dari pengguna yang utangnya ingin dicari.
     * @return Sebuah List dari objek Utang.
     */
    public List<Utang> getUtangByUserId(String userId) {
        List<Utang> daftarUtang = new ArrayList<>();
        String sql = "SELECT * FROM Utang WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Utang utang = new Utang(
                    rs.getString("utang_id"),
                    rs.getString("user_id"),
                    rs.getDouble("jumlah"),
                    rs.getDouble("bunga"),
                    rs.getDate("tanggal_jatuh_tempo"),
                    rs.getString("status"),
                    rs.getString("creditor")
                );
                daftarUtang.add(utang);
            }
            
        } catch (SQLException e) {
            System.err.println("Error mengambil data utang: " + e.getMessage());
            e.printStackTrace();
        }
        
        return daftarUtang;
    }
}
