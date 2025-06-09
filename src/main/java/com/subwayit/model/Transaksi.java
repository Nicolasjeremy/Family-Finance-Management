package com.subwayit.model;

import java.time.LocalDate; // For handling dates more robustly

/**
 * Represents a financial transaction (income or expense).
 * Maps to the 'Transaksi' table.
 */
public class Transaksi {
    private String transaksiId; // From 'transaksi_id'
    private String userId;      // From 'user_id'
    private String jenis;       // 'Pemasukan' or 'Pengeluaran'
    private String kategori;    // e.g., 'Gaji', 'Makanan'
    private double nominal;
    private LocalDate tanggalTransaksi; // Using LocalDate for dates
    private String buktiTransaksi; // Path to file or base64
    private boolean isRutin;
    private String deskripsi;
    // Additional properties for display in table (derived from other fields)
    private String transaksiIdAndDate;
    private String payeeFrom;

    public Transaksi(String transaksiId, String userId, String jenis, String kategori, double nominal, LocalDate tanggalTransaksi, String buktiTransaksi, boolean isRutin, String deskripsi) {
        this.transaksiId = transaksiId;
        this.userId = userId;
        this.jenis = jenis;
        this.kategori = kategori;
        this.nominal = nominal;
        this.tanggalTransaksi = tanggalTransaksi;
        this.buktiTransaksi = buktiTransaksi;
        this.isRutin = isRutin;
        this.deskripsi = deskripsi;

        // Derived properties for TableView display
        this.transaksiIdAndDate = transaksiId + "\n" + tanggalTransaksi.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.payeeFrom = (jenis.equals("Pemasukan") ? "From: " : "To: ") + (jenis.equals("Pemasukan") ? "Source" : "Vendor"); // Placeholder, refine later
    }

    // --- Getters and Setters ---
    public String getTransaksiId() { return transaksiId; }
    public void setTransaksiId(String transaksiId) { this.transaksiId = transaksiId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public double getNominal() { return nominal; }
    public void setNominal(double nominal) { this.nominal = nominal; }
    public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(LocalDate tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }
    public String getBuktiTransaksi() { return buktiTransaksi; }
    public void setBuktiTransaksi(String buktiTransaksi) { this.buktiTransaksi = buktiTransaksi; }
    public boolean isRutin() { return isRutin; }
    public void setRutin(boolean rutin) { isRutin = rutin; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    // Getters for TableView PropertyValueFactory
    public String getTransaksiIdAndDate() { return transaksiIdAndDate; }
    public String getPayeeFrom() { return payeeFrom; } // Needs logic to determine actual payee/from
}