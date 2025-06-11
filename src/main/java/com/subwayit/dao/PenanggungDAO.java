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

    public PenanggungDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Inserts a new Penanggung into the Pengguna and Penanggung tables.
     * This handles the inheritance relationship.
     * @param penanggung The Penanggung object to insert.
     */
    public void addPenanggung(Penanggung penanggung) {
        try {
            userDAO.addUser(penanggung); // Ini akan throw RuntimeException jika gagal

            // Tambahkan kolom anggota_tanggungan_ids ke SQL INSERT
            String sql = "INSERT INTO Penanggung(penanggung_id, jumlah_pemasukan, jumlah_pengeluaran, pekerjaan, anggota_tanggungan_ids) VALUES(?,?,?,?,?)";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, penanggung.getUserId());
                pstmt.setInt(2, penanggung.getJumlahPemasukan());
                pstmt.setInt(3, penanggung.getJumlahPengeluaran());
                pstmt.setString(4, penanggung.getPekerjaan());
                pstmt.setString(5, penanggung.getAnggotaTanggunganIdsAsString()); // Simpan sebagai string koma
                pstmt.executeUpdate();
                System.out.println("Penanggung berhasil ditambahkan ke tabel Penanggung: " + penanggung.getNama());

                // Hapus logika pembuatan Keluarga di sini
                // String keluargaId = "KEL-" + UUID.randomUUID().toString().substring(0, 8);
                // Keluarga newKeluarga = new Keluarga(keluargaId, penanggung.getNama(), penanggung.getUserId());
                // keluargaDAO.addKeluarga(newKeluarga); // Hapus baris ini

            }
        } catch (SQLException e) {
            System.err.println("Error menambahkan Penanggung ke tabel Penanggung: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal menambahkan Penanggung: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat menambahkan Penanggung (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a Penanggung from the database by their ID.
     * @param penanggungId The ID of the Penanggung to retrieve.
     * @return The Penanggung object if found, null otherwise.
     */
    public Penanggung getPenanggungById(String penanggungId) {
        User user = userDAO.getUserByUserId(penanggungId);
        if (user == null || !"Penanggung".equals(user.getRole())) {
            return null;
        }

        // Ambil juga anggota_tanggungan_ids dari DB
        String sql = "SELECT jumlah_pemasukan, jumlah_pengeluaran, pekerjaan, anggota_tanggungan_ids FROM Penanggung WHERE penanggung_id = ?";
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
                        user.getPassword(),
                        rs.getString("pekerjaan"),
                        rs.getString("anggota_tanggungan_ids") // Muat string ID anggota
                );
                penanggung.setJumlahPemasukan(rs.getInt("jumlah_pemasukan"));
                penanggung.setJumlahPengeluaran(rs.getInt("jumlah_pengeluaran"));
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil Penanggung berdasarkan ID: " + e.getMessage());
            e.printStackTrace();
        }
        return penanggung;
    }

    /**
     * Updates an existing Penanggung's information in both Pengguna and Penanggung tables.
     * @param penanggung The Penanggung object with updated information.
     */
    public void updatePenanggung(Penanggung penanggung) {
        try {
            userDAO.updateUser(penanggung);

            // Update juga kolom anggota_tanggungan_ids
            String sql = "UPDATE Penanggung SET jumlah_pemasukan = ?, jumlah_pengeluaran = ?, pekerjaan = ?, anggota_tanggungan_ids = ? WHERE penanggung_id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, penanggung.getJumlahPemasukan());
                pstmt.setInt(2, penanggung.getJumlahPengeluaran());
                pstmt.setString(3, penanggung.getPekerjaan());
                pstmt.setString(4, penanggung.getAnggotaTanggunganIdsAsString()); // Simpan sebagai string koma
                pstmt.setString(5, penanggung.getUserId());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Penanggung berhasil diupdate di tabel Penanggung: " + penanggung.getNama());
                } else {
                    System.out.println("Penanggung tidak ditemukan untuk update di tabel Penanggung: " + penanggung.getNama());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengupdate Penanggung di tabel Penanggung: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal mengupdate Penanggung: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat mengupdate Penanggung (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a Penanggung from both Penanggung and Pengguna tables.
     * @param penanggungId The ID of the Penanggung to delete.
     */
    public void deletePenanggung(String penanggungId) {
        try {
            // Opsional: Perbarui Tanggungan yang sebelumnya terkait agar penanggung_id mereka NULL
            // Ini akan membuat Tanggungan tersebut "yatim" atau Anda bisa hapus mereka juga
            String updateTanggunganSql = "UPDATE Tanggungan SET penanggung_id = NULL WHERE penanggung_id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(updateTanggunganSql)) {
                pstmt.setString(1, penanggungId);
                pstmt.executeUpdate();
                System.out.println("Tanggungan terkait diatur ke NULL penanggung_id.");
            } catch (SQLException e) {
                System.err.println("Error mengupdate Tanggungan terkait saat menghapus Penanggung: " + e.getMessage());
            }

            // Hapus dari tabel Penanggung
            String sql = "DELETE FROM Penanggung WHERE penanggung_id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, penanggungId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Penanggung berhasil dihapus dari tabel Penanggung: " + penanggungId);
                    userDAO.deleteUser(penanggungId);
                } else {
                    System.out.println("Penanggung tidak ditemukan untuk penghapusan di tabel Penanggung: " + penanggungId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menghapus Penanggung: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal menghapus Penanggung: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            System.err.println("Error saat menghapus Penanggung (masalah User dasar): " + e.getMessage());
            throw e;
        }
    }
}