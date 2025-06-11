package com.subwayit.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // For formatting date for table display

public class Utang {
    private String utangId;
    private String penanggungId; // Foreign key to Penanggung (the one who has the debt)
    private double jumlah;       // The total amount of the debt
    private double bunga;        // Interest rate (can be 0.0)
    private LocalDate tanggalJatuhTempo; // When the debt is due
    private String status;       // e.g., "Belum Lunas", "Lunas", "Telat" (Overdue)
    private String creditor;     // The name of the entity/person to whom the debt is owed

    public Utang(String utangId, String penanggungId, double jumlah, double bunga, LocalDate tanggalJatuhTempo, String status, String creditor) {
        this.utangId = utangId;
        this.penanggungId = penanggungId;
        this.jumlah = jumlah;
        this.bunga = bunga;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.status = status;
        this.creditor = creditor;
    }

    // --- Getters and Setters ---
    public String getUtangId() { return utangId; }
    public void setUtangId(String utangId) { this.utangId = utangId; }

    public String getPenanggungId() { return penanggungId; }
    public void setPenanggungId(String penanggungId) { this.penanggungId = penanggungId; }

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

    // --- Helper for TableView display ---
    public String getFormattedDueDate() {
        if (tanggalJatuhTempo != null) {
            return tanggalJatuhTempo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
}