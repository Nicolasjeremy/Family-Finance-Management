package com.subwayit.model;

/**
 * Represents a dependent family member in the SubwayIT system.
 * Extends the base User class and has limited access to the system. [cite: 25]
 */
public class Tanggungan extends User {
    // Attributes specific to Tanggungan, based on Section 2.3.3 of the document
    private String posisiKeluarga; // 'Posisi Keluarga' [cite: 153]
    private String pendidikan; // 'Pendidikan' [cite: 153]
    private String pekerjaan; // 'Pekerjaan' [cite: 153]
    // Note: 'Nama' and 'Umur' are listed in the document's Tanggungan attributes[cite: 153],
    // but are inherited from the User class. They are included here for completeness of doc mapping.

    // Constructor
    // Calls the superclass (User) constructor first
    public Tanggungan(String userId, String nama, int umur, String email, String password, String posisiKeluarga, String pendidikan, String pekerjaan) {
        // Tanggungan role is fixed
        super(userId, nama, umur, email, password, "Tanggungan");
        this.posisiKeluarga = posisiKeluarga;
        this.pendidikan = pendidikan;
        this.pekerjaan = pekerjaan;
    }

    // --- Operations (Methods) specific to Tanggungan, based on Section 2.3.3 of the document ---

    /**
     * Allows the user to fill in their confidential personal information. [cite: 153]
     * (This method might be inherited from User and overridden, or specific to how Tanggungan fills data).
     * Here, assuming it's part of Tanggungan's specific interaction with personal data.
     */
    @Override // Marking as override as fillPersonalData() is in User
    public void fillPersonalData() {
        System.out.println(this.getNama() + " (Tanggungan) is filling personal data.");
        // Specific logic for Tanggungan's personal data filling
    }

    /**
     * Adds routine personal expenses for the dependent. [cite: 153]
     */
    public void addRoutineExpense() {
        System.out.println(this.getNama() + " (Tanggungan) is adding a routine expense.");
    }

    /**
     * Adds irregular personal expenses for the dependent. [cite: 153]
     */
    public void addIrregularExpense() {
        System.out.println(this.getNama() + " (Tanggungan) is adding an irregular expense.");
    }

    // --- Getters and Setters for Tanggungan-specific Attributes ---

    public String getPosisiKeluarga() {
        return posisiKeluarga;
    }

    public void setPosisiKeluarga(String posisiKeluarga) {
        this.posisiKeluarga = posisiKeluarga;
    }

    public String getPendidikan() {
        return pendidikan;
    }

    public void setPendidikan(String pendidikan) {
        this.pendidikan = pendidikan;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }
}