package com.subwayit;

import com.subwayit.dao.UserDAO;
import com.subwayit.database.DatabaseManager;
import com.subwayit.model.User;
import com.subwayit.gui.DashboardPage;

// Import model dan DAO yang diperlukan
import com.subwayit.model.Penanggung;
import com.subwayit.dao.PenanggungDAO;
import com.subwayit.model.Tanggungan;
import com.subwayit.dao.TanggunganDAO;
import com.subwayit.model.Admin;
import com.subwayit.dao.AdminDAO;
import com.subwayit.dao.TransaksiDAO; // FIX 1: Add import for TransaksiDAO
import com.subwayit.dao.UtangDAO;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

public class App extends Application {

    // GUI Components for Login
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button registerButton;

    // --- DAO instances ---
    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private TransaksiDAO transaksiDAO; // FIX 2: Declare TransaksiDAO attribute
    private UtangDAO utangDAO;

    // Primary stage reference
    private Stage primaryStage;

    // Theme colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SUBWAYIT - Family Finance Management");

        try {
            DatabaseManager.initializeDatabase();
            DatabaseManager.updateDatabaseSchema();
            System.out.println("Database setup completed successfully.");
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
            showModernAlert(Alert.AlertType.ERROR, "Kesalahan Database",
                    "Gagal menginisialisasi database. Harap periksa apakah aplikasi memiliki izin tulis.");
            return;
        }

        // Initialize all DAOs once at the start of the application
        this.userDAO = new UserDAO();
        this.tanggunganDAO = new TanggunganDAO(userDAO);
        this.penanggungDAO = new PenanggungDAO(userDAO);
        this.adminDAO = new AdminDAO();
        this.transaksiDAO = new TransaksiDAO(); // FIX 3: Initialize TransaksiDAO
        this.utangDAO = new UtangDAO();

        // Show the login scene
        Scene loginScene = createLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private Scene createLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, "
                + LIGHT_GREEN + " 100%);");

        VBox loginCard = new VBox(25);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40, 50, 40, 50));
        loginCard.setMaxWidth(450);
        loginCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 16;");

        VBox titleSection = createTitleSection();
        VBox loginForm = createLoginForm();
        VBox registerSection = createRegisterSection();

        loginCard.getChildren().addAll(titleSection, loginForm, registerSection);
        root.getChildren().add(loginCard);

        return new Scene(root, 900, 700);
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox(15);
        titleSection.setAlignment(Pos.CENTER);

        Label appTitle = new Label("SUBWAYIT");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        appTitle.setTextFill(Color.web(PRIMARY_GREEN));

        Label subtitle = new Label("Sistem Manajemen Keuangan Keluarga");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        subtitle.setTextFill(Color.web(TEXT_GRAY));

        Rectangle decorativeLine = new Rectangle(80, 3, Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        titleSection.getChildren().addAll(appTitle, subtitle, decorativeLine);
        return titleSection;
    }

    private VBox createLoginForm() {
        VBox loginForm = new VBox(20);
        loginForm.setAlignment(Pos.CENTER);

        // Welcome text
        Label welcomeLabel = new Label("Selamat Datang Kembali! 👋");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        Label loginDesc = new Label("Silakan masuk ke akun Anda");
        loginDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        loginDesc.setTextFill(Color.web(TEXT_GRAY));
        VBox welcomeSection = new VBox(5, welcomeLabel, loginDesc);
        welcomeSection.setAlignment(Pos.CENTER);

        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Masukkan nama pengguna Anda");
        VBox usernameSection = createFieldSection("Nama Pengguna", usernameField);

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan kata sandi Anda");
        VBox passwordSection = createFieldSection("Kata Sandi", passwordField);

        // Login button
        loginButton = new Button("🚀 Masuk");
        loginButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        loginButton.setPrefWidth(320);
        loginButton.setPrefHeight(50);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setStyle("-fx-background-color: " + PRIMARY_GREEN
                + "; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");
        loginButton.setOnMouseEntered(
                e -> loginButton.setStyle(loginButton.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        loginButton
                .setOnMouseExited(e -> loginButton.setStyle(loginButton.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        loginButton.setOnAction(e -> handleLogin());

        loginForm.getChildren().addAll(welcomeSection, usernameSection, passwordSection, loginButton);
        return loginForm;
    }

    private VBox createFieldSection(String labelText, TextField field) {
        VBox section = new VBox(8);
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web(TEXT_DARK));

        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle(
                "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal)
                field.setStyle(field.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            else
                field.setStyle(field.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
        });

        section.getChildren().addAll(label, field);
        return section;
    }

    private VBox createRegisterSection() {
        VBox registerSection = new VBox(15);
        registerSection.setAlignment(Pos.CENTER);

        HBox separator = new HBox(10, new Rectangle(100, 1, Color.web("#E2E8F0")), new Label("ATAU"),
                new Rectangle(100, 1, Color.web("#E2E8F0")));
        separator.setAlignment(Pos.CENTER);

        registerButton = new Button("✨ Buat Akun Baru");
        registerButton.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        registerButton.setPrefWidth(320);
        registerButton.setPrefHeight(45);
        registerButton.setStyle("-fx-background-color: white; -fx-border-color: " + PRIMARY_GREEN
                + "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        registerButton.setOnMouseEntered(
                e -> registerButton.setStyle(registerButton.getStyle() + "-fx-background-color: " + LIGHT_GREEN + ";"));
        registerButton.setOnMouseExited(e -> registerButton
                .setStyle(registerButton.getStyle().replace("-fx-background-color: " + LIGHT_GREEN + ";", "")));
        registerButton.setOnAction(e -> handleRegister());

        registerSection.getChildren().addAll(separator, registerButton);
        return registerSection;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showModernAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama pengguna dan kata sandi tidak boleh kosong.");
            return;
        }

        User user = userDAO.getUserByUserId(username);

        if (user != null && user.getPassword().equals(password)) {
            showModernAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                    "Selamat datang kembali, " + user.getNama() + "!");
            openDashboard(user);
        } else {
            showModernAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama pengguna atau kata sandi salah.");
        }
    }

    private void handleRegister() {
        // Create a simple registration form for the first admin/penanggung
        Stage registerStage = new Stage();
        registerStage.setTitle("Registrasi Akun Baru");
        registerStage.initOwner(primaryStage);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Form fields
        TextField userID = new TextField();
        userID.setPromptText("user ID");

        TextField namaField = new TextField();
        namaField.setPromptText("Nama Lengkap");

        TextField umurField = new TextField();
        umurField.setPromptText("Umur");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordRegField = new PasswordField();
        passwordRegField.setPromptText("Password");

        TextField pekerjaanField = new TextField();
        pekerjaanField.setPromptText("Pekerjaan");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Penanggung", "Admin");
        roleComboBox.setValue("Penanggung");
        roleComboBox.setPromptText("Pilih Role");

        Button registerBtn = new Button("Daftar");
        Button cancelBtn = new Button("Batal");

        registerBtn.setOnAction(e -> {
            try {
                String userId = userID.getText().trim();
                String nama = namaField.getText().trim();
                String umurStr = umurField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordRegField.getText().trim();
                String pekerjaan = pekerjaanField.getText().trim();
                String role = roleComboBox.getValue();

                if (userId.isEmpty() || nama.isEmpty() || umurStr.isEmpty() || email.isEmpty() || password.isEmpty()
                        || role == null) {
                    showModernAlert(Alert.AlertType.ERROR, "Error", "Semua field harus diisi!");
                    return;
                }

                int umur = Integer.parseInt(umurStr);

                // Create user
                User newUser = new User(userId, nama, umur, email, password, role);
                userDAO.addUser(newUser);

                // If role is Penanggung, create Penanggung entry
                if ("Penanggung".equals(role)) {
                    Penanggung newPenanggung = new Penanggung(userId, nama, umur, email, password, pekerjaan);
                    penanggungDAO.addPenanggung(newPenanggung);
                } else if ("Admin".equals(role)) {
                    // Create Admin entry if needed
                    Admin newAdmin = new Admin(userId, nama, umur, email, password, "Admin");
                    adminDAO.addAdmin(newAdmin);
                }

                showModernAlert(Alert.AlertType.INFORMATION, "Berhasil", "Akun berhasil dibuat! Silakan login.");
                registerStage.close();

            } catch (NumberFormatException ex) {
                showModernAlert(Alert.AlertType.ERROR, "Error", "Umur harus berupa angka!");
            } catch (Exception ex) {
                showModernAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelBtn.setOnAction(e -> registerStage.close());

        HBox buttonBox = new HBox(10, cancelBtn, registerBtn);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
                new Label("Registrasi Akun Baru"),
                userID, namaField, umurField, emailField, passwordRegField, pekerjaanField, roleComboBox,
                buttonBox);

        Scene scene = new Scene(layout, 350, 400);
        registerStage.setScene(scene);
        registerStage.showAndWait();
    }

    private void openDashboard(User user) {
        DashboardPage dashboardPage = new DashboardPage(
                primaryStage,
                user,
                userDAO,
                penanggungDAO,
                tanggunganDAO,
                adminDAO,
                transaksiDAO,
                utangDAO // <- Add the utangDAO instance here
        );
        Scene dashboardScene = dashboardPage.createScene();
        primaryStage.setScene(dashboardScene);
        primaryStage.centerOnScreen();
    }

    private void showModernAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}