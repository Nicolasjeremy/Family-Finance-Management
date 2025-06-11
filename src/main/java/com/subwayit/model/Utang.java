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
    private double sisaUtang; // Sisa utang yang belum dibayar (dari total dengan bunga)

    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
        this.sisaUtang = getTotalWithInterest(); // Initially, sisa utang sama dengan total plus bunga
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
    public void setJumlah(double jumlah) { 
        this.jumlah = jumlah;
        // Recalculate sisa utang if needed
        if (this.sisaUtang == 0) {
            this.sisaUtang = getTotalWithInterest();
        }
    }

    public double getBunga() { return bunga; }
    public void setBunga(double bunga) { 
        this.bunga = bunga;
        // Recalculate sisa utang if needed
        if (this.sisaUtang == 0) {
            this.sisaUtang = getTotalWithInterest();
        }
    }

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
        double totalWithInterest = getTotalWithInterest();
        if (sisaUtang <= 0) {
            this.status = "Lunas";
        } else if (sisaUtang < totalWithInterest) {
            this.status = "Sebagian Lunas";
        }
    }

    /**
     * Menghitung total utang dengan bunga
     * @return total amount including interest
     */
    public double getTotalWithInterest() {
        if (bunga <= 0) {
            return jumlah; // No interest
        }
        
        // Calculate compound interest based on time period
        LocalDate now = LocalDate.now();
        LocalDate startDate = now; // Assume debt started now for simplicity
        
        if (tanggalJatuhTempo != null && tanggalJatuhTempo.isAfter(now)) {
            // Calculate months between now and due date
            long monthsToMaturity = ChronoUnit.MONTHS.between(now, tanggalJatuhTempo);
            if (monthsToMaturity <= 0) {
                monthsToMaturity = 1; // Minimum 1 month
            }
            
            // Calculate compound interest
            double monthlyRate = bunga / 12; // Convert annual rate to monthly
            return jumlah * Math.pow(1 + monthlyRate, monthsToMaturity);
        } else {
            // If overdue or no due date, calculate for 1 year
            return jumlah * (1 + bunga);
        }
    }

    /**
     * Get formatted total with interest for display
     */
    public String getFormattedTotalWithInterest() {
        return "Rp " + String.format("%,.0f", getTotalWithInterest()).replace(',', '.');
    }

    /**
     * Get total interest amount
     */
    public double getTotalInterest() {
        return getTotalWithInterest() - jumlah;
    }

    /**
     * Get formatted total interest for display
     */
    public String getFormattedTotalInterest() {
        return "Rp " + String.format("%,.0f", getTotalInterest()).replace(',', '.');
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

        // Simple division of remaining debt
        return sisaUtang / monthsRemaining;
    }

    /**
     * Format estimasi biaya bulanan untuk display
     */
    public String getFormattedEstimasiBulanan() {
        double estimasi = getEstimasiBiayaBulanan();
        return "Rp " + String.format("%,.0f", estimasi).replace(',', '.');
    }

    /**
     * Get formatted original amount for display
     */
    public String getFormattedJumlah() {
        return "Rp " + String.format("%,.0f", jumlah).replace(',', '.');
    }

    /**
     * Get formatted remaining amount for display
     */
    public String getFormattedSisaUtang() {
        return "Rp " + String.format("%,.0f", sisaUtang).replace(',', '.');
    }

    /**
     * Get interest rate as percentage for display
     */
    public String getFormattedBunga() {
        return String.format("%.2f%%", bunga * 100);
    }
}