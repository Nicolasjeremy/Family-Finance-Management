package com.subwayit.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Utang {
    private String utangId;
    private String userId;
    private double jumlah;
    private double bunga;
    private Date tanggalJatuhTempo;
    private String status;
    private String creditor;

    // Constructor
    public Utang(String utangId, String userId, double jumlah, double bunga, Date tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
    }

    /**
     * Menghitung biaya cicilan bulanan untuk melunasi utang sampai jatuh tempo.
     * @return Biaya cicilan per bulan.
     */
    public double getBiayaBulanan() {
        // Konversi java.util.Date ke java.time.LocalDate untuk perhitungan modern
        LocalDate tanggalMulai = LocalDate.now();
        LocalDate tanggalAkhir = this.tanggalJatuhTempo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Hitung jumlah bulan dari sekarang hingga jatuh tempo
        long jumlahBulan = ChronoUnit.MONTHS.between(tanggalMulai, tanggalAkhir);

        // Jika jatuh tempo sudah lewat atau kurang dari 1 bulan, kembalikan total utang
        if (jumlahBulan < 1) {
            return this.jumlah + this.bunga;
        }

        // Total utang adalah jumlah pokok ditambah bunga
        double totalUtang = this.jumlah + this.bunga;

        // Bagi total utang dengan jumlah bulan
        return totalUtang / jumlahBulan;
    }

    // --- Getters and Setters ---

    public String getUtangId() {
        return utangId;
    }

    public void setUtangId(String utangId) {
        this.utangId = utangId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public double getBunga() {
        return bunga;
    }

    public void setBunga(double bunga) {
        this.bunga = bunga;
    }

    public Date getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public void setTanggalJatuhTempo(Date tanggalJatuhTempo) {
        this.tanggalJatuhTempo = tanggalJatuhTempo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }
}
