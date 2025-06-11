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

    public TanggunganDAO(UserDAO userDAO) { // Ubah parameter konstruktor
        this.userDAO = userDAO;
    }

    /**
     * Inserts a new Tanggungan into the Pengguna and Tanggungan tables.
     * @param tanggungan The Tanggungan object to insert.
     */
    public void addTanggungan(Tanggungan tanggungan, String penanggungId) { // Ubah parameter keluargaId ke penanggungId
        try {
            userDAO.addUser(tanggungan);

            // Tambahkan kolom penanggung_id ke SQL INSERT
            String sql = "INSERT INTO Tanggungan(tanggungan_id, posisi, nama, umur, pendidikan, pekerjaan, penanggung_id) VALUES(?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tanggungan.getUserId());
                pstmt.setString(2, tanggungan.getPosisiKeluarga());
                pstmt.setString(3, tanggungan.getNama());
                pstmt.setInt(4, tanggungan.getUmur());
                pstmt.setString(5, tanggungan.getPendidikan());
                pstmt.setString(6, tanggungan.getPekerjaan());
                pstmt.setString(7, penanggungId); // Set penanggungId
                pstmt.executeUpdate();
                System.out.println("Tanggungan berhasil ditambahkan ke tabel Tanggungan: " + tanggungan.getNama());

                // Tidak ada lagi logika addAnggotaToFamily dari KeluargaDAO di sini
                // if (penanggungId != null && !penanggungId.trim().isEmpty()) { ... }
            }
        } catch (SQLException e) {
            System.err.println("Error menambahkan Tanggungan ke tabel Tanggungan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal menambahkan Tanggungan: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat menambahkan Tanggungan (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a Tanggungan from the database by their ID.
     * @param tanggunganId The ID of the Tanggungan to retrieve.
     * @return The Tanggungan object if found, null otherwise.
     */
    public Tanggungan getTanggunganById(String tanggunganId) {
        User user = userDAO.getUserByUserId(tanggunganId);
        if (user == null || !"Tanggungan".equals(user.getRole())) {
            return null;
        }

        // Ambil juga penanggung_id dari DB
        String sql = "SELECT posisi, nama, umur, pendidikan, pekerjaan, penanggung_id FROM Tanggungan WHERE tanggungan_id = ?";
        Tanggungan tanggungan = null;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tanggunganId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tanggungan = new Tanggungan(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("posisi"),
                        rs.getString("pendidikan"),
                        rs.getString("pekerjaan"),
                        rs.getString("penanggung_id") // Muat penanggungId
                );
                tanggungan.setNama(rs.getString("nama"));
                tanggungan.setUmur(rs.getInt("umur"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil Tanggungan berdasarkan ID: " + e.getMessage());
            e.printStackTrace();
        }
        return tanggungan;
    }

    /**
     * Updates an existing Tanggungan's information in both Pengguna and Tanggungan tables.
     * @param tanggungan The Tanggungan object with updated information.
     */
    public void updateTanggungan(Tanggungan tanggungan) {
        try {
            userDAO.updateUser(tanggungan);

            // Update juga kolom penanggung_id
            String sql = "UPDATE Tanggungan SET posisi = ?, nama = ?, umur = ?, pendidikan = ?, pekerjaan = ?, penanggung_id = ? WHERE tanggungan_id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tanggungan.getPosisiKeluarga());
                pstmt.setString(2, tanggungan.getNama());
                pstmt.setInt(3, tanggungan.getUmur());
                pstmt.setString(4, tanggungan.getPendidikan());
                pstmt.setString(5, tanggungan.getPekerjaan());
                pstmt.setString(6, tanggungan.getPenanggungId()); // Set penanggungId
                pstmt.setString(7, tanggungan.getUserId());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Tanggungan berhasil diupdate di tabel Tanggungan: " + tanggungan.getNama());
                } else {
                    System.out.println("Tanggungan tidak ditemukan untuk update di tabel Tanggungan: " + tanggungan.getNama());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengupdate Tanggungan di tabel Tanggungan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal mengupdate Tanggungan: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat mengupdate Tanggungan (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a Tanggungan from both Tanggungan and Pengguna tables.
     * @param tanggunganId The ID of the Tanggungan to delete.
     */
    public void deleteTanggungan(String tanggunganId) {
        try {
            // Logika hapus dari Anggota_Keluarga dihapus
            // if (keluargaDAO != null) { ... }

            String sql = "DELETE FROM Tanggungan WHERE tanggungan_id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tanggunganId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Tanggungan berhasil dihapus dari tabel Tanggungan: " + tanggunganId);
                    userDAO.deleteUser(tanggunganId);
                } else {
                    System.out.println("Tanggungan tidak ditemukan untuk penghapusan di tabel Tanggungan: " + tanggunganId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menghapus Tanggungan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal menghapus Tanggungan dan data terkait: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat menghapus Tanggungan (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }

    public List<Tanggungan> getAllTanggunan() {
        String sql = "SELECT tanggungan_id, posisi, nama, umur, pendidikan, pekerjaan, penanggung_id FROM Tanggungan";
        List<Tanggungan> tanggungans = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = userDAO.getUserByUserId(rs.getString("tanggungan_id"));
                if (user != null) {
                    Tanggungan tanggungan = new Tanggungan(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("posisi"),
                        rs.getString("pendidikan"),
                        rs.getString("pekerjaan"),
                        rs.getString("penanggung_id") // Muat penanggungId
                    );
                    tanggungan.setNama(rs.getString("nama"));
                    tanggungan.setUmur(rs.getInt("umur"));
                    tanggungans.add(tanggungan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all Tanggungan: " + e.getMessage());
            e.printStackTrace();
        }
        return tanggungans;
    }
    public List<Tanggungan> getTanggunanByPenanggungId(String penanggungId) {
        String sql = "SELECT tanggungan_id, posisi, nama, umur, pendidikan, pekerjaan, penanggung_id FROM Tanggungan WHERE penanggung_id = ?";
        List<Tanggungan> tanggungans = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, penanggungId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = userDAO.getUserByUserId(rs.getString("tanggungan_id"));
                if (user != null) {
                    Tanggungan tanggungan = new Tanggungan(
                        user.getUserId(),
                        user.getNama(),
                        user.getUmur(),
                        user.getEmail(),
                        user.getPassword(),
                        rs.getString("posisi"),
                        rs.getString("pendidikan"),
                        rs.getString("pekerjaan"),
                        rs.getString("penanggung_id")
                    );
                    tanggungans.add(tanggungan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting Tanggungan by Penanggung ID: " + e.getMessage());
            e.printStackTrace();
        }
        return tanggungans;
    }
}