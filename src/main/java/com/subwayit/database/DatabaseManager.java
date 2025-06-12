package com.subwayit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:subwayit.db";

    /**
     * Connect to the SQLite database
     * 
     * @return Connection object or null if connection failed
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Initialize database by creating all necessary tables
     */
    public static void initializeDatabase() {
        System.out.println("Initializing database...");

        // Create all tables
        createPenggunaTable();
        createPenanggungTable();
        createTanggunganTable();
        createAdminTable(); // Add this line
        createTransaksiTable();
        createUtangTable();
        createPaymentHistoryTable();

        System.out.println("Database initialization completed.");
    }

    /**
     * Update database schema for new features
     */
    public static void updateDatabaseSchema() {
        System.out.println("Updating database schema...");

        try (Connection conn = connect()) {
            if (conn != null) {
                // Add sisa_utang column to Utang table if it doesn't exist
                addSisaUtangColumn(conn);

                // Update existing records to set sisa_utang = jumlah where sisa_utang is null
                updateExistingUtangRecords(conn);

                System.out.println("Database schema update completed.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating database schema: " + e.getMessage());
        }
    }

    /**
     * Create Pengguna table
     */
    // Metode createPenggunaTable() tetap sama
    private static void createPenggunaTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Pengguna (" +
                "user_id TEXT PRIMARY KEY," +
                "nama TEXT NOT NULL," +
                "umur INTEGER NOT NULL," +
                "e_mail TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Pengguna table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Pengguna table: " + e.getMessage());
        }
    }

    /**
     * Create Transaksi table
     */
    private static void createTransaksiTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Transaksi (" +
                "transaksi_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "jenis TEXT NOT NULL," + // Add jenis
                "kategori TEXT NOT NULL," + // Add kategori
                "nominal REAL NOT NULL," +
                "tanggal_transaksi DATE NOT NULL," + // Renamed from 'tanggal'
                "bukti_transaksi TEXT," + // Add bukti_transaksi
                "is_rutin BOOLEAN NOT NULL," + // Add is_rutin
                "deskripsi TEXT," +
                "FOREIGN KEY (user_id) REFERENCES Pengguna(user_id)" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Transaksi table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Transaksi table: " + e.getMessage());
        }
    }

    /**
     * Create Utang table
     */
    private static void createUtangTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Utang (" +
                "utang_id TEXT PRIMARY KEY," +
                "user_id TEXT NOT NULL," +
                "jumlah REAL NOT NULL," +
                "bunga REAL NOT NULL," +
                "tanggal_jatuh_tempo DATE NOT NULL," +
                "status TEXT NOT NULL," +
                "creditor TEXT NOT NULL," +
                "sisa_utang REAL," +
                "FOREIGN KEY (user_id) REFERENCES Pengguna(user_id)" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Utang table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Utang table: " + e.getMessage());
        }
    }

    private static void createPenanggungTable() {
        // Kolom 'anggota_tanggungan_ids' akan menyimpan daftar ID Tanggungan yang
        // dipisahkan koma.
        // Jika Anda ingin mengelola anggota secara lebih terstruktur, mungkin perlu
        // tabel join Anggota_Keluarga
        // yang hanya menghubungkan Penanggung dengan Tanggungan, tetapi tanpa model
        // Keluarga.
        // Untuk saat ini, mari kita pakai string dipisahkan koma untuk kesederhanaan
        // ekstrem.
        String sql = "CREATE TABLE IF NOT EXISTS Penanggung (" +
                "penanggung_id TEXT PRIMARY KEY," +
                "jumlah_pemasukan INTEGER NOT NULL DEFAULT 0," +
                "jumlah_pengeluaran INTEGER NOT NULL DEFAULT 0," +
                "pekerjaan TEXT," + // Tambahkan kolom pekerjaan
                "anggota_tanggungan_ids TEXT," + // Menyimpan ID Tanggungan yang dipisahkan koma
                "FOREIGN KEY (penanggung_id) REFERENCES Pengguna(user_id)" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Penanggung table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Penanggung table: " + e.getMessage());
        }
    }

    private static void createTanggunganTable() {
        // Kolom 'penanggung_id' akan mengaitkan Tanggungan ke Penanggung-nya
        String sql = "CREATE TABLE IF NOT EXISTS Tanggungan (" +
                "tanggungan_id TEXT PRIMARY KEY," +
                "posisi TEXT," +
                "nama TEXT NOT NULL," + // Diulang dari Pengguna, tapi sesuai doc
                "umur INTEGER NOT NULL," + // Diulang dari Pengguna, tapi sesuai doc
                "pendidikan TEXT," +
                "pekerjaan TEXT," +
                "penanggung_id TEXT," + // Tambahkan kolom penanggung_id
                "FOREIGN KEY (tanggungan_id) REFERENCES Pengguna(user_id)," +
                "FOREIGN KEY (penanggung_id) REFERENCES Penanggung(penanggung_id)" + // FK ke Penanggung
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tanggungan table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Tanggungan table: " + e.getMessage());
        }
    }

    /**
     * Create payment_history table
     */
    private static void createPaymentHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS payment_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "utang_id TEXT NOT NULL," +
                "payment_amount REAL NOT NULL," +
                "payment_date DATE NOT NULL," +
                "notes TEXT," +
                "FOREIGN KEY (utang_id) REFERENCES Utang(utang_id)" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("payment_history table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating payment_history table: " + e.getMessage());
        }
    }

    /**
     * Add sisa_utang column to existing Utang table
     */
    private static void addSisaUtangColumn(Connection conn) {
        // Check if column already exists by trying to add it
        String sql = "ALTER TABLE Utang ADD COLUMN sisa_utang REAL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Added sisa_utang column to Utang table");
        } catch (SQLException e) {
            // Column might already exist, that's okay
            if (e.getMessage().contains("duplicate column name")) {
                System.out.println("sisa_utang column already exists");
            } else {
                System.err.println("Error adding sisa_utang column: " + e.getMessage());
            }
        }
    }

    /**
     * Update existing Utang records to set sisa_utang = jumlah where sisa_utang is
     * null
     */
    private static void updateExistingUtangRecords(Connection conn) {
        String sql = "UPDATE Utang SET sisa_utang = jumlah WHERE sisa_utang IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Updated " + updated + " existing Utang records with sisa_utang");
            }
        } catch (SQLException e) {
            System.err.println("Error updating existing Utang records: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection conn = connect()) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close database connection safely
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Create Admin table
     */
    private static void createAdminTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Admin (" +
                "admin_id TEXT PRIMARY KEY," +
                "FOREIGN KEY (admin_id) REFERENCES Pengguna(user_id)" +
                ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Admin table created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating Admin table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // A main method to easily run this class and create the database
    public static void main(String[] args) {
        initializeDatabase();
    }
}