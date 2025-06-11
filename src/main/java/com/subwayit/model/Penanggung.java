package com.subwayit.model;

import java.util.ArrayList;
import java.util.Arrays; // Untuk konversi string ke list
import java.util.List;
import java.util.stream.Collectors; // Untuk konversi list ke string

/**
 * Represents the main user or head of household in the SubwayIT system.
 * Extends the base User class and has full access to the system.
 */
public class Penanggung extends User {
    private int jumlahPemasukan;
    private int jumlahPengeluaran;
    private String pekerjaan; // Atribut pekerjaan sudah ada
    // Atribut baru: menyimpan ID Tanggungan yang dipisahkan koma
    private List<String> anggotaTanggunganIds; // Ubah dari List<Tanggungan> ke List<String>

    // Constructor baru dengan pekerjaan
    public Penanggung(String userId, String nama, int umur, String email, String password, String pekerjaan) {
        super(userId, nama, umur, email, password, "Penanggung");
        this.jumlahPemasukan = 0;
        this.jumlahPengeluaran = 0;
        this.pekerjaan = pekerjaan;
        this.anggotaTanggunganIds = new ArrayList<>(); // Inisialisasi kosong
    }

    // Constructor jika memuat dari DB, yang memiliki ID Tanggungan dalam bentuk string
    public Penanggung(String userId, String nama, int umur, String email, String password, String pekerjaan, String anggotaTanggunganIdsString) {
        super(userId, nama, umur, email, password, "Penanggung");
        this.jumlahPemasukan = 0;
        this.jumlahPengeluaran = 0;
        this.pekerjaan = pekerjaan;
        // Konversi string ID yang dipisahkan koma menjadi List<String>
        if (anggotaTanggunganIdsString != null && !anggotaTanggunganIdsString.isEmpty()) {
            this.anggotaTanggunganIds = new ArrayList<>(Arrays.asList(anggotaTanggunganIdsString.split(",")));
        } else {
            this.anggotaTanggunganIds = new ArrayList<>();
        }
    }

    // --- Add getter and setter for pekerjaan ---
    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    // --- Operations (Methods) specific to Penanggung, based on Section 2.3.2 of the document ---

    /**
     * Allows the Penanggung to record financial transactions (income and expenses). [cite: 148]
     * This operation saves the history of every financial transaction. [cite: 148]
     */
    public void catatPemasukanPengeluaran() {
        System.out.println(this.getNama() + " is recording income/expenses.");
        // Actual implementation will involve interacting with Transaksi/Finansial objects and DAOs
    }

    public void addAnggotaTanggunganId(String tanggunganId) {
        if (!this.anggotaTanggunganIds.contains(tanggunganId)) {
            this.anggotaTanggunganIds.add(tanggunganId);
        }
    }

    public void removeAnggotaTanggunganId(String tanggunganId) {
        this.anggotaTanggunganIds.remove(tanggunganId);
    }

    /**
     * Establishes a dependent relationship. [cite: 148]
     * (This might be called internally after addDependent or for existing dependents)
     * @param tanggungan The Tanggungan object to set/associate.
     */
    public void setDependent(Tanggungan tanggungan) {
        // This method's precise meaning (compared to addDependent) depends on context.
        // It might imply updating a dependent's status or linking.
        System.out.println(this.getNama() + " is setting/associating dependent: " + tanggungan.getNama());
    }

    /**
     * Adds income to the financial record. [cite: 148]
     */
    public void addIncome() {
        System.out.println(this.getNama() + " is adding income.");
    }

    /**
     * Adds routine personal expenses. [cite: 148]
     */
    public void addRoutineExpense() {
        System.out.println(this.getNama() + " is adding a routine expense.");
    }

    /**
     * Adds irregular personal expenses. [cite: 151]
     */
    public void addIrregularExpense() {
        System.out.println(this.getNama() + " is adding an irregular expense.");
    }

    /**
     * Adds installment (debt) data. [cite: 151]
     */
    public void addInstallment() {
        System.out.println(this.getNama() + " is adding an installment.");
    }

    /**
     * Sends installment reminders. [cite: 151]
     */
    public void sendInstallmentReminder() {
        System.out.println(this.getNama() + " is sending installment reminders.");
    }

    /**
     * Manages access for family members (other users). [cite: 151]
     */
    public void manageUserAccess() {
        System.out.println(this.getNama() + " is managing user access.");
    }

    // --- Getters and Setters for Penanggung-specific Attributes ---

    public int getJumlahPemasukan() {
        return jumlahPemasukan;
    }

    public void setJumlahPemasukan(int jumlahPemasukan) {
        this.jumlahPemasukan = jumlahPemasukan;
    }

    public int getJumlahPengeluaran() {
        return jumlahPengeluaran;
    }

    public void setJumlahPengeluaran(int jumlahPengeluaran) {
        this.jumlahPengeluaran = jumlahPengeluaran;
    }

    public List<String> getAnggotaTanggunganIds() {
        return anggotaTanggunganIds;
    }

    // Setter untuk daftar ID Tanggungan (saat memuat dari DB)
    public void setAnggotaTanggunganIds(List<String> anggotaTanggunganIds) {
        this.anggotaTanggunganIds = anggotaTanggunganIds;
    }

    // Helper method untuk mendapatkan ID Tanggungan dalam format string yang dipisahkan koma untuk disimpan ke DB
    public String getAnggotaTanggunganIdsAsString() {
        return String.join(",", anggotaTanggunganIds);
    }
}