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

    // DAO instance
    private UserDAO userDAO;

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
            // Initialize database and update schema
            DatabaseManager.initializeDatabase();
            DatabaseManager.updateDatabaseSchema();
            
            System.out.println("Database setup completed successfully.");
        } catch (Exception e) {
            System.err.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog to user
            showModernAlert(Alert.AlertType.ERROR, "Database Error", 
                           "Failed to initialize database. Please check if the application has write permissions.");
        }

        // Initialize DAOs
        this.userDAO = new UserDAO();

        // Show login scene
        Scene loginScene = createLoginScene();
        primaryStage.setScene(loginScene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private Scene createLoginScene() {
        // Main container with modern styling
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, "
                + LIGHT_GREEN + " 100%);");

        // Login card container
        VBox loginCard = new VBox(25);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40, 50, 40, 50));
        loginCard.setMaxWidth(450);
        loginCard.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16;");

        // App title with modern styling
        VBox titleSection = createTitleSection();

        // Login form
        VBox loginForm = createLoginForm();

        // Register section
        VBox registerSection = createRegisterSection();

        loginCard.getChildren().addAll(titleSection, loginForm, registerSection);
        root.getChildren().add(loginCard);

        return new Scene(root, 900, 700);
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox(15);
        titleSection.setAlignment(Pos.CENTER);

        // App title with green accent
        Label appTitle = new Label("SUBWAYIT");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        appTitle.setTextFill(Color.web(PRIMARY_GREEN));
        appTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Subtitle
        Label subtitle = new Label("Financial Management System");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 16));
        subtitle.setTextFill(Color.web(TEXT_GRAY));

        // Decorative line
        Rectangle decorativeLine = new Rectangle(80, 3);
        decorativeLine.setFill(Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        titleSection.getChildren().addAll(appTitle, subtitle, decorativeLine);
        return titleSection;
    }

    private VBox createLoginForm() {
        VBox loginForm = new VBox(20);
        loginForm.setAlignment(Pos.CENTER);

        // Welcome message
        Label welcomeLabel = new Label("Welcome Back! 👋");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web(TEXT_DARK));

        Label loginDesc = new Label("Please sign in to your account");
        loginDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        loginDesc.setTextFill(Color.web(TEXT_GRAY));

        VBox welcomeSection = new VBox(5, welcomeLabel, loginDesc);
        welcomeSection.setAlignment(Pos.CENTER);

        // Username field with modern styling
        VBox usernameSection = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        usernameLabel.setTextFill(Color.web(TEXT_DARK));

        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(320);
        usernameField.setPrefHeight(45);
        usernameField.setFont(Font.font("Segoe UI", 14));
        usernameField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12;");

        // Focus styling
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle(usernameField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                usernameField.setStyle(usernameField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        usernameSection.getChildren().addAll(usernameLabel, usernameField);

        // Password field with modern styling
        VBox passwordSection = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        passwordLabel.setTextFill(Color.web(TEXT_DARK));

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(320);
        passwordField.setPrefHeight(45);
        passwordField.setFont(Font.font("Segoe UI", 14));
        passwordField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12;");

        // Focus styling
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(passwordField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                passwordField.setStyle(passwordField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        passwordSection.getChildren().addAll(passwordLabel, passwordField);

        // Login button with modern styling
        loginButton = new Button("🚀 Sign In");
        loginButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        loginButton.setPrefWidth(320);
        loginButton.setPrefHeight(50);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        // Hover effects
        loginButton.setOnMouseEntered(
                e -> loginButton.setStyle(loginButton.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        loginButton
                .setOnMouseExited(e -> loginButton.setStyle(loginButton.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        loginButton.setOnAction(e -> handleLogin());

        loginForm.getChildren().addAll(welcomeSection, usernameSection, passwordSection, loginButton);
        return loginForm;
    }

    private VBox createRegisterSection() {
        VBox registerSection = new VBox(15);
        registerSection.setAlignment(Pos.CENTER);

        // Separator
        HBox separator = new HBox(10);
        separator.setAlignment(Pos.CENTER);

        Rectangle leftLine = new Rectangle(100, 1);
        leftLine.setFill(Color.web("#E2E8F0"));

        Label orLabel = new Label("OR");
        orLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        orLabel.setTextFill(Color.web(TEXT_GRAY));

        Rectangle rightLine = new Rectangle(100, 1);
        rightLine.setFill(Color.web("#E2E8F0"));

        separator.getChildren().addAll(leftLine, orLabel, rightLine);

        // Register button
        registerButton = new Button("✨ Create New Account");
        registerButton.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        registerButton.setPrefWidth(320);
        registerButton.setPrefHeight(45);
        registerButton.setTextFill(Color.web(TEXT_DARK));
        registerButton.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + PRIMARY_GREEN + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");

        // Hover effects
        registerButton.setOnMouseEntered(e -> registerButton.setStyle(registerButton.getStyle() +
                "-fx-background-color: " + LIGHT_GREEN + ";"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle(registerButton.getStyle()
                .replace("-fx-background-color: " + LIGHT_GREEN + ";", "")));
        registerButton.setOnAction(e -> handleRegister());

        registerSection.getChildren().addAll(separator, registerButton);
        return registerSection;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showModernAlert(Alert.AlertType.ERROR, "Login Failed", "Username and password cannot be empty.");
            return;
        }

        User user = userDAO.getUserByUserId(username);

        if (user != null && user.getPassword().equals(password)) {
            showModernAlert(Alert.AlertType.INFORMATION, "Login Successful",
                    "Welcome back, " + user.getNama() + "! 🎉");
            openDashboard(user);
        } else {
            showModernAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private void handleRegister() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Create New Account - SubwayIT");

        // Main container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, "
                + LIGHT_GREEN + " 100%);");

        // Registration card
        VBox registerCard = new VBox(20);
        registerCard.setAlignment(Pos.CENTER);
        registerCard.setPadding(new Insets(30, 40, 30, 40));
        registerCard.setMaxWidth(400);
        registerCard.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5); " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16;");

        // Title section
        VBox titleSection = new VBox(10);
        titleSection.setAlignment(Pos.CENTER);

        Label registerTitle = new Label("Create Account ✨");
        registerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        registerTitle.setTextFill(Color.web(TEXT_DARK));

        Label registerDesc = new Label("Join SubwayIT financial management");
        registerDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        registerDesc.setTextFill(Color.web(TEXT_GRAY));

        titleSection.getChildren().addAll(registerTitle, registerDesc);

        // Form fields
        TextField regUserIdField = createModernTextField("User ID", "Enter unique user ID");
        TextField regNamaField = createModernTextField("Full Name", "Enter your full name");
        TextField regUmurField = createModernTextField("Age", "Enter your age");
        TextField regEmailField = createModernTextField("Email", "Enter your email address");
        PasswordField regPasswordField = createModernPasswordField("Password", "Create a strong password");

        // Role selection
        VBox roleSection = new VBox(8);
        Label roleLabel = new Label("Role");
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        roleLabel.setTextFill(Color.web(TEXT_DARK));

        ComboBox<String> regRoleComboBox = new ComboBox<>();
        regRoleComboBox.getItems().addAll("Penanggung", "Tanggungan", "Admin");
        regRoleComboBox.setPromptText("Select your role");
        regRoleComboBox.setPrefWidth(320);
        regRoleComboBox.setPrefHeight(45);
        regRoleComboBox.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;");
        regRoleComboBox.getSelectionModel().selectFirst();

        roleSection.getChildren().addAll(roleLabel, regRoleComboBox);

        // Create account button
        Button createAccountButton = new Button("🎉 Create Account");
        createAccountButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        createAccountButton.setPrefWidth(320);
        createAccountButton.setPrefHeight(50);
        createAccountButton.setTextFill(Color.WHITE);
        createAccountButton.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        // Hover effects
        createAccountButton.setOnMouseEntered(
                e -> createAccountButton.setStyle(createAccountButton.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        createAccountButton.setOnMouseExited(
                e -> createAccountButton.setStyle(createAccountButton.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));

        createAccountButton.setOnAction(e -> {
            try {
                String userId = regUserIdField.getText();
                String nama = regNamaField.getText();
                int umur = Integer.parseInt(regUmurField.getText());
                String email = regEmailField.getText();
                String password = regPasswordField.getText();
                String role = regRoleComboBox.getValue();

                if (userId.isEmpty() || nama.isEmpty() || regUmurField.getText().isEmpty() ||
                        email.isEmpty() || password.isEmpty() || role == null) {
                    showModernAlert(Alert.AlertType.ERROR, "Registration Error",
                            "Please fill in all fields to continue.");
                    return;
                }

                if (userDAO.getUserByUserId(userId) != null) {
                    showModernAlert(Alert.AlertType.ERROR, "Registration Error",
                            "User ID already exists. Please choose another one.");
                    return;
                }

                User newUser = new User(userId, nama, umur, email, password, role);
                userDAO.addUser(newUser); // Menambahkan ke tabel Pengguna

                // Menambahkan ke tabel spesifik berdasarkan peran
                if ("Penanggung".equals(role)) {
                    PenanggungDAO penanggungDAO = new PenanggungDAO();
                    // Menggunakan constructor Penanggung yang tidak memerlukan 'pekerjaan' secara
                    // eksplisit,
                    // karena form registrasi saat ini tidak memintanya.
                    // Model Penanggung akan menginisialisasi 'pekerjaan' ke string kosong.
                    Penanggung newPenanggung = new Penanggung(userId, nama, umur, email, password);
                    penanggungDAO.addPenanggung(newPenanggung);
                } else if ("Tanggungan".equals(role)) {
                    TanggunganDAO tanggunganDAO = new TanggunganDAO();
                    // Form registrasi saat ini tidak meminta posisiKeluarga, pendidikan, pekerjaan
                    // untuk Tanggungan.
                    // Menggunakan string kosong sebagai default.
                    // TODO: Pertimbangkan untuk menambahkan field ini ke form registrasi jika
                    // diperlukan.
                    Tanggungan newTanggungan = new Tanggungan(userId, nama, umur, email, password, "", "", "");
                    tanggunganDAO.addTanggungan(newTanggungan);
                } else if ("Admin".equals(role)) {
                    AdminDAO adminDAO = new AdminDAO();
                    // Untuk Admin, admin_id biasanya sama dengan user_id.
                    Admin newAdmin = new Admin(userId, nama, umur, email, password, userId);
                    adminDAO.addAdmin(newAdmin);
                }

                showModernAlert(Alert.AlertType.INFORMATION, "Registration Success",
                        "🎉 Account created successfully for " + nama + " (" + role
                                + ")!\nYou can now log in with your credentials.");
                registerStage.close();

            } catch (NumberFormatException ex) {
                showModernAlert(Alert.AlertType.ERROR, "Input Error", "Age must be a valid number.");
            } catch (Exception ex) {
                showModernAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "An error occurred during registration: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        registerCard.getChildren().addAll(
                titleSection,
                createFieldSection("User ID", regUserIdField),
                createFieldSection("Full Name", regNamaField),
                createFieldSection("Age", regUmurField),
                createFieldSection("Email", regEmailField),
                createFieldSection("Password", regPasswordField),
                roleSection,
                createAccountButton);

        root.getChildren().add(registerCard);
        Scene regScene = new Scene(root, 500, 700);
        registerStage.setScene(regScene);
        registerStage.centerOnScreen();
        registerStage.show();
    }

    private VBox createFieldSection(String labelText, TextField field) {
        VBox section = new VBox(8);
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web(TEXT_DARK));
        section.getChildren().addAll(label, field);
        return section;
    }

    private TextField createModernTextField(String label, String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(320);
        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12;");

        // Focus styling
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(field.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                field.setStyle(field.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return field;
    }

    private PasswordField createModernPasswordField(String label, String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(320);
        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 12;");

        // Focus styling
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(field.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                field.setStyle(field.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return field;
    }

    private void openDashboard(User user) {
        DashboardPage dashboardPage = new DashboardPage(primaryStage, user);
        Scene dashboardScene = dashboardPage.createScene();
        primaryStage.setScene(dashboardScene);
        primaryStage.centerOnScreen();
    }

    private void showModernAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert dialog
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}