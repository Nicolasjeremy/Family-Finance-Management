package com.subwayit.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utang {
    private String utangId;
    private String userId; // Ubah dari tanggunganID ke userId untuk konsistensi dengan database
    private double jumlah;
    private double bunga;
    private LocalDate tanggalJatuhTempo;
    private String status;
    private String creditor;

    public Utang(String utangId, String userId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.userId = userId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
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

    public String getFormattedDueDate() {
        if (tanggalJatuhTempo != null) {
            return tanggalJatuhTempo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
}