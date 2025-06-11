package com.subwayit.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Utang {
    private String utangId;
    private String userId; // Ubah dari tanggunganID ke userId untuk konsistensi dengan database
    private double jumlah;
    private double bunga;
    private LocalDate tanggalJatuhTempo;
    private String status;
    private String creditor;
    private double sisaUtang; // Sisa utang yang belum dibayar

    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
        this.sisaUtang = jumlah; // Initially, sisa utang sama dengan jumlah utang
    }

    // Constructor dengan sisaUtang (untuk data dari database)
    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor, double sisaUtang) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
        this.sisaUtang = sisaUtang;
    }

    // Getters and Setters
    public String getUtangId() { return utangId; }
    public void setUtangId(String utangId) { this.utangId = utangId; }

    public String getUserId() { return userId; } // Ubah nama method
    public void setUserId(String userId) { this.userId = userId; } // Ubah nama method

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public double getBunga() { return bunga; }
    public void setBunga(double bunga) { this.bunga = bunga; }

    public LocalDate getTanggalJatuhTempo() { return tanggalJatuhTempo; }
    public void setTanggalJatuhTempo(LocalDate tanggalJatuhTempo) { this.tanggalJatuhTempo = tanggalJatuhTempo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreditor() { return creditor; }
    public void setCreditor(String creditor) { this.creditor = creditor; }

    public double getSisaUtang() { return sisaUtang; }
    public void setSisaUtang(double sisaUtang) { 
        this.sisaUtang = sisaUtang;
        // Update status berdasarkan sisa utang
        if (sisaUtang <= 0) {
            this.status = "Lunas";
        } else if (sisaUtang < jumlah) {
            this.status = "Sebagian Lunas";
        }
    }

    public String getFormattedDueDate() {
        if (tanggalJatuhTempo != null) {
            return tanggalJatuhTempo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    /**
     * Menghitung estimasi pembayaran bulanan yang diperlukan untuk melunasi utang sampai deadline
     */
    public double getEstimasiBiayaBulanan() {
        if (sisaUtang <= 0 || tanggalJatuhTempo == null) {
            return 0.0;
        }

        LocalDate now = LocalDate.now();
        if (tanggalJatuhTempo.isBefore(now) || tanggalJatuhTempo.equals(now)) {
            // Jika sudah overdue atau hari ini deadline, bayar semua
            return sisaUtang;
        }

        long monthsRemaining = ChronoUnit.MONTHS.between(now, tanggalJatuhTempo);
        if (monthsRemaining <= 0) {
            monthsRemaining = 1; // Minimum 1 bulan
        }

        // Hitung dengan bunga compound jika ada
        if (bunga > 0) {
            double monthlyRate = bunga / 12; // Konversi annual rate ke monthly
            double totalWithInterest = sisaUtang * Math.pow(1 + monthlyRate, monthsRemaining);
            return totalWithInterest / monthsRemaining;
        } else {
            return sisaUtang / monthsRemaining;
        }
    }

    /**
     * Format estimasi biaya bulanan untuk display
     */
    public String getFormattedEstimasiBulanan() {
        double estimasi = getEstimasiBiayaBulanan();
        return "Rp " + String.format("%,.0f", estimasi).replace(',', '.');
    }
}