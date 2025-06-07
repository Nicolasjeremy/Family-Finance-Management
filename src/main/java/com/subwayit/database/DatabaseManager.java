package com.subwayit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Defines the JDBC URL for your SQLite database.
    // subwayit.db will be created in the root of your Maven project (subwayit-app folder)
    private static final String DB_URL = "jdbc:sqlite:subwayit.db";

    /**
     * Establishes and returns a connection to the SQLite database.
     * @return A Connection object if successful, otherwise null.
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            // Load the SQLite JDBC driver. For modern JDBC, this is often implicit, but good practice.
            Class.forName("org.sqlite.JDBC");
            // Establish the connection
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite database established successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for more detailed error info
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Make sure it's in your pom.xml and downloaded.");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Creates all necessary tables in the database based on the schema defined in the document.
     */
    public static void createTables() {
        // SQL statements for creating tables as per your document (Section 2.7)
        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS Pengguna (" +
                "user_id TEXT PRIMARY KEY," +
                "nama TEXT," +
                "umur INTEGER," +
                "e_mail TEXT," +
                "password TEXT," +
                "role TEXT" + // e.g., 'Penanggung', 'Tanggungan', 'Admin'
            ");",

            // Penanggung (Main User/Head of Household)
            // penanggung_id is also a user_id from Pengguna
            "CREATE TABLE IF NOT EXISTS Penanggung (" +
                "penanggung_id TEXT PRIMARY KEY," +
                "jumlah_pemasukan INTEGER," +
                "jumlah_pengeluaran INTEGER," +
                "FOREIGN KEY (penanggung_id) REFERENCES Pengguna(user_id)" +
            ");",

            // Tanggungan (Dependent)
            // tanggungan_id is also a user_id from Pengguna
            "CREATE TABLE IF NOT EXISTS Tanggungan (" +
                "tanggungan_id TEXT PRIMARY KEY," +
                "posisi TEXT," + // 'Posisi Keluarga' in class design, e.g., 'Anak', 'Istri'
                "nama TEXT," + // Redundant if linking to Pengguna, but kept as per doc's class attributes [cite: 153]
                "umur INTEGER," + // Redundant if linking to Pengguna, but kept as per doc's class attributes [cite: 153]
                "pendidikan TEXT," +
                "pekerjaan TEXT," +
                "FOREIGN KEY (tanggungan_id) REFERENCES Pengguna(user_id)" +
            ");",

            // Keluarga (Family)
            "CREATE TABLE IF NOT EXISTS Keluarga (" +
                "keluarga_id TEXT PRIMARY KEY," +
                "nama_kepala TEXT," + // Added based on Keluarga class attribute [cite: 160]
                "penanggung_id TEXT," + // Link to the Penanggung in charge of the family [cite: 160]
                "FOREIGN KEY (penanggung_id) REFERENCES Penanggung(penanggung_id)" +
            ");",

            // Anggota_Keluarga (Linking table for Keluarga and Tanggungan)
            // This implicitly handles the 'anggota' list in Keluarga class. [cite: 160]
            "CREATE TABLE IF NOT EXISTS Anggota_Keluarga (" +
                "keluarga_id TEXT," +
                "tanggungan_id TEXT," +
                "PRIMARY KEY (keluarga_id, tanggungan_id)," +
                "FOREIGN KEY (keluarga_id) REFERENCES Keluarga(keluarga_id)," +
                "FOREIGN KEY (tanggungan_id) REFERENCES Tanggungan(tanggungan_id)" +
            ");",

            // Transaksi (Transaction - will cover both Pemasukan and Pengeluaran)
            "CREATE TABLE IF NOT EXISTS Transaksi (" +
                "transaksi_id TEXT PRIMARY KEY," +
                "user_id TEXT," + // The user (Penanggung or Tanggungan) who made the transaction [cite: 231]
                "jenis TEXT," + // 'Pemasukan' or 'Pengeluaran' [cite: 231]
                "kategori TEXT," + // e.g., 'Gaji', 'Makanan' (from Finansial/Pengeluaran classes)
                "nominal REAL," + // float in Finansial class [cite: 173]
                "tanggal_transaksi DATE," + // Date in Finansial class [cite: 173]
                "bukti_transaksi TEXT," + // string in Finansial class [cite: 173]
                "is_rutin BOOLEAN," + // boolean in Finansial class [cite: 173]
                "deskripsi TEXT," + // From Pemasukan/Pengeluaran algorithms
                "FOREIGN KEY (user_id) REFERENCES Pengguna(user_id)" +
            ");",

            // Utang (Debt/Installment)
            // ...
            "CREATE TABLE IF NOT EXISTS Utang (" +
                "utang_id TEXT PRIMARY KEY," +
                "user_id TEXT," + // Diubah agar terhubung ke user manapun (Penanggung atau Tanggungan)
                "jumlah REAL," +
                "bunga REAL," +
                "tanggal_jatuh_tempo DATE," +
                "status TEXT," +
                "creditor TEXT," +
                "FOREIGN KEY (user_id) REFERENCES Pengguna(user_id)" + // Diubah ke user_id
            ");",

            // Laporan (Report)
            // Based on the Laporan class, it links to Keluarga, Tanggungan, and Transaksi
            "CREATE TABLE IF NOT EXISTS Laporan (" +
                "laporan_id TEXT PRIMARY KEY," + // Laporan_ID from Laporan class [cite: 168]
                "keluarga_id TEXT," + // FK to Keluarga [cite: 231]
                "tanggungan_id TEXT," + // FK to Tanggungan [cite: 231]
                "transaksi_id TEXT," + // FK to Transaksi [cite: 231]
                "periode TEXT," + // Added based on algorithm (e.g., '2025-01') [cite: 201]
                "total_pemasukan REAL," + // Based on algorithm [cite: 201]
                "total_pengeluaran REAL," + // Based on algorithm [cite: 201]
                "rasio_keuangan REAL," + // Based on algorithm [cite: 201]
                "tanggal_dibuat DATE," +
                "FOREIGN KEY (keluarga_id) REFERENCES Keluarga(keluarga_id)," +
                "FOREIGN KEY (tanggungan_id) REFERENCES Tanggungan(tanggungan_id)," +
                "FOREIGN KEY (transaksi_id) REFERENCES Transaksi(transaksi_id)" +
            ");"
        };

        try (Connection conn = connect(); // Get a connection
             Statement stmt = conn.createStatement()) { // Create a statement object
            for (String sql : sqlStatements) {
                stmt.execute(sql); // Execute each SQL statement
            }
            System.out.println("All database tables created successfully or already exist.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // A main method to easily run this class and create the database
    public static void main(String[] args) {
        createTables();
    }
}