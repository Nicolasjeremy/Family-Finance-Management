package com.subwayit;

import com.subwayit.dao.UserDAO;
import com.subwayit.dao.UtangDAO; // Impor DAO baru
import com.subwayit.database.DatabaseManager;
import com.subwayit.model.User;
import com.subwayit.model.Utang; // Impor model baru

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Main application class for SubwayIT.
 * Manages GUI scenes for different functionalities like adding users and debts.
 */
public class App extends Application {

    private UserDAO userDAO;
    private UtangDAO utangDAO;
    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        userDAO = new UserDAO();
        utangDAO = new UtangDAO();
        primaryStage.setTitle("SubwayIT - Menu Utama");

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        Label titleLabel = new Label("Selamat Datang di SubwayIT");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button goToAddUserPageButton = new Button("Halaman Tambah Pengguna");
        goToAddUserPageButton.setMaxWidth(Double.MAX_VALUE);
        Button goToUtangPageButton = new Button("Halaman Tambah Utang");
        goToUtangPageButton.setMaxWidth(Double.MAX_VALUE);
        Button goToViewDebtPageButton = new Button("Halaman Lihat Informasi Utang"); // Tombol Baru
        goToViewDebtPageButton.setMaxWidth(Double.MAX_VALUE);

        goToAddUserPageButton.setOnAction(e -> primaryStage.setScene(createAddUserScene()));
        goToUtangPageButton.setOnAction(e -> primaryStage.setScene(createUtangScene()));
        goToViewDebtPageButton.setOnAction(e -> primaryStage.setScene(createViewDebtScene())); // Aksi Baru

        mainLayout.getChildren().addAll(titleLabel, goToAddUserPageButton, goToUtangPageButton, goToViewDebtPageButton);
        mainScene = new Scene(mainLayout, 400, 300);

        primaryStage.setScene(mainScene);
        primaryStage.show();
        DatabaseManager.createTables();
    }
    
    /**
     * Creates the scene for the "View Debts" page.
     * @return A Scene object for viewing debts.
     */
    private Scene createViewDebtScene() {
        primaryStage.setTitle("SubwayIT - Lihat Informasi Utang");

        // --- GUI Components ---
        TextField userIdField = new TextField();
        userIdField.setPromptText("Masukkan User ID yang ingin dicek");
        Button searchButton = new Button("Cari Utang");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        
        Button backToMenuButton = new Button("Kembali ke Menu Utama");

        // --- Set Actions ---
        searchButton.setOnAction(e -> handleViewDebt(userIdField.getText(), resultArea));
        backToMenuButton.setOnAction(e -> {
            primaryStage.setTitle("SubwayIT - Menu Utama");
            primaryStage.setScene(mainScene);
        });

        // --- Layout ---
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
            new Label("Lihat Utang Pengguna:"),
            userIdField,
            searchButton,
            new Label("Hasil:"),
            resultArea,
            backToMenuButton
        );

        return new Scene(layout, 500, 600);
    }
    
    /**
     * Handles the logic to fetch and display debt information.
     */
    private void handleViewDebt(String userId, TextArea resultArea) {
        if (userId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Input", "User ID tidak boleh kosong.");
            return;
        }

        List<Utang> daftarUtang = utangDAO.getUtangByUserId(userId);
        
        if (daftarUtang.isEmpty()) {
            resultArea.setText("Tidak ditemukan data utang untuk User ID: " + userId);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        // Format Rupiah
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (Utang utang : daftarUtang) {
            sb.append("---------------------------------\n");
            sb.append("ID Utang: ").append(utang.getUtangId()).append("\n");
            sb.append("Pemberi Utang: ").append(utang.getCreditor()).append("\n");
            sb.append("Jumlah Pokok: ").append(formatter.format(utang.getJumlah())).append("\n");
            sb.append("Biaya/Bunga: ").append(formatter.format(utang.getBunga())).append("\n");
            sb.append("Status: ").append(utang.getStatus()).append("\n");
            sb.append("Jatuh Tempo: ").append(utang.getTanggalJatuhTempo()).append("\n\n");
            sb.append(">> PERKIRAAN CICILAN PER BULAN: ")
              .append(formatter.format(utang.getBiayaBulanan()))
              .append("\n");
            sb.append("---------------------------------\n\n");
        }
        
        resultArea.setText(sb.toString());
    }

    // Metode createAddUserScene(), createUtangScene(), handleAddUser(), handleSaveUtang(), showAlert(), dan main()
    // tetap sama seperti kode sebelumnya. Pastikan untuk menyalinnya juga ke dalam file ini jika Anda
    // memulai dari awal. Di bawah ini hanya placeholder singkat.
    private Scene createAddUserScene() { return new Scene(new VBox(new Label("Placeholder Halaman Tambah Pengguna")), 400, 500); }
    private Scene createUtangScene() { return new Scene(new VBox(new Label("Placeholder Halaman Tambah Utang")), 400, 550); }
    private void handleAddUser(String u, String n, String um, String e, String p, String r) {}
    private void handleSaveUtang(String ui, String uid, String j, String b, String c, LocalDate d) {}
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void main(String[] args) { launch(args); }
}
