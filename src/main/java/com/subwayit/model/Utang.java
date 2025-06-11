package com.subwayit.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Utang {
    private String utangId;
    private String userId;
    private String penanggungId; // The ID of the Penanggung responsible for this family
    private double jumlah;
    private double bunga;
    private LocalDate tanggalJatuhTempo;
    private String status;
    private String creditor;
    private double sisaUtang;

    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
        this.sisaUtang = this.getTotalWithInterest(); // Initial remaining amount is the full amount with interest
    }

    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor, double sisaUtang) {
        this(utangId, userId, jumlah, bunga, tanggalJatuhTempo, status, creditor);
        this.sisaUtang = sisaUtang;
    }

    // Getters and Setters
    public String getUtangId() { return utangId; }
    public void setUtangId(String utangId) { this.utangId = utangId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
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
    public void setSisaUtang(double sisaUtang) { this.sisaUtang = sisaUtang; }
    public String getPenanggungId() { return penanggungId; }
    public void setPenanggungId(String penanggungId) { this.penanggungId = penanggungId; }

    // Helper methods for calculation and display
    public double getTotalWithInterest() {
        return jumlah * (1 + bunga);
    }

    public String getFormattedDueDate() {
        return tanggalJatuhTempo != null ? tanggalJatuhTempo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
    }

    public double getEstimasiBiayaBulanan() {
        if (sisaUtang <= 0 || tanggalJatuhTempo == null || tanggalJatuhTempo.isBefore(LocalDate.now())) {
            return sisaUtang > 0 ? sisaUtang : 0.0;
        }
        long monthsRemaining = ChronoUnit.MONTHS.between(LocalDate.now(), tanggalJatuhTempo);
        return sisaUtang / (monthsRemaining <= 0 ? 1 : monthsRemaining);
    }

    public String getFormattedEstimasiBulanan() {
        return "Rp " + String.format("%,.0f", getEstimasiBiayaBulanan()).replace(',', '.');
    }
}