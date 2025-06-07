package com.subwayit.model;

import java.util.ArrayList; // For the list of Tanggungan
import java.util.List;

/**
 * Represents the main user or head of household in the SubwayIT system.
 * Extends the base User class and has full access to the system. [cite: 25]
 */
public class Penanggung extends User {
    // Attributes specific to Penanggung, based on Section 2.3.2 of the document
    private int jumlahPemasukan; // 'Jumlah pemasukan' [cite: 151]
    private int jumlahPengeluaran; // 'Jumlah pengeluaran' [cite: 151]
    // 'Anggota tanggungan' is a list[cite: 151], best represented as a List of Tanggungan objects
    private List<Tanggungan> anggotaTanggungan;

    // Constructor
    // Calls the superclass (User) constructor first
    public Penanggung(String userId, String nama, int umur, String email, String password) {
        // Penanggung role is fixed
        super(userId, nama, umur, email, password, "Penanggung");
        this.jumlahPemasukan = 0; // Initialize to 0
        this.jumlahPengeluaran = 0; // Initialize to 0
        this.anggotaTanggungan = new ArrayList<>();
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

    /**
     * Adds a new dependent (Tanggungan) to the family. [cite: 148]
     * @param tanggungan The Tanggungan object to add.
     */
    public void addDependent(Tanggungan tanggungan) {
        this.anggotaTanggungan.add(tanggungan);
        System.out.println(this.getNama() + " added dependent: " + tanggungan.getNama());
        // Actual implementation might involve saving to database via a DAO
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

    public List<Tanggungan> getAnggotaTanggungan() {
        return anggotaTanggungan;
    }

    public void setAnggotaTanggungan(List<Tanggungan> anggotaTanggungan) {
        this.anggotaTanggungan = anggotaTanggungan;
    }
}