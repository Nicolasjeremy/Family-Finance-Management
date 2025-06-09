package com.subwayit;

import com.subwayit.dao.UserDAO;
import com.subwayit.database.DatabaseManager;
import com.subwayit.model.User;
import com.subwayit.gui.DashboardPage; // Import the DashboardPage class

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
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.ComboBox;

public class App extends Application {

    // GUI Components for Login
    private TextField usernameField; // Maps to TxtUsername
    private PasswordField passwordField; // Maps to TxtPassword
    private Button loginButton; // Maps to BtnLogin
    private Button registerButton; // New: for registration

    // DAO instance
    private UserDAO userDAO;

    // A reference to the primary stage to switch scenes
    private Stage primaryStage; // Added to hold the primary stage reference

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Store the primary stage reference
        userDAO = new UserDAO();
        primaryStage.setTitle("SubwayIT - Login/Register");

        // Optional: Ensure tables are created when the app starts
        // In a real application, this might be part of an installer or initial setup script.
        DatabaseManager.createTables();

        // --- UI Setup ---

        // "SUBWAYIT" Label
        Label appTitle = new Label("SUBWAYIT");
        appTitle.setFont(Font.font("Arial", 48)); // Larger font
        appTitle.setTextFill(Color.web("#4CAF50")); // Green color
        appTitle.setPadding(new Insets(0, 0, 30, 0)); // Padding below title

        // Username Field
        usernameField = new TextField();
        usernameField.setPromptText("Username"); // Corresponds to TxtUsername
        usernameField.setMaxWidth(250); // Limit width for cleaner look

        // Password Field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password"); // Corresponds to TxtPassword
        passwordField.setMaxWidth(250); // Limit width

        // Login Button
        loginButton = new Button("Login"); // Corresponds to BtnLogin
        loginButton.setFont(Font.font("Arial", 16));
        loginButton.setTextFill(Color.WHITE);
        loginButton.setStyle("-fx-background-color: #8BC34A; -fx-background-radius: 5;"); // Green color
        loginButton.setPadding(new Insets(10, 30, 10, 30));
        loginButton.setOnAction(e -> handleLogin()); // Action for login

        // Register Button (New for functionality)
        registerButton = new Button("Register");
        registerButton.setFont(Font.font("Arial", 14));
        registerButton.setStyle("-fx-background-color: #607D8B; -fx-background-radius: 5;"); // A different color
        registerButton.setTextFill(Color.WHITE);
        registerButton.setPadding(new Insets(8, 20, 8, 20));
        registerButton.setOnAction(e -> handleRegister()); // Action for registration

        // VBox for form elements
        VBox formLayout = new VBox(15); // Spacing between elements
        formLayout.setAlignment(Pos.CENTER);
        formLayout.getChildren().addAll(
                appTitle,
                new Label("Username:"), usernameField, // Added explicit label for username
                new Label("Password:"), passwordField, // Added explicit label for password
                loginButton,
                new Label("- OR -"), // Separator
                registerButton
        );

        // Main layout (StackPane to center the form)
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(formLayout);
        mainLayout.setStyle("-fx-background-color: #E0E0E0;"); // Light grey background

        Scene scene = new Scene(mainLayout, 800, 600); // Standard window size
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the login button action.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username and password cannot be empty.");
            return;
        }

        // Basic login validation (would typically involve hashing passwords and more robust checks)
        User user = userDAO.getUserByUserId(username); // Using UserID as username for simplicity here

        if (user != null && user.getPassword().equals(password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getNama() + "!");
            openDashboard(user); // Navigate to Dashboard upon successful login
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    /**
     * Handles the register button action.
     * For simplicity, this will open a new stage with a simple registration form.
     */
    private void handleRegister() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register New User");

        TextField regUserIdField = new TextField();
        regUserIdField.setPromptText("User ID");

        TextField regNamaField = new TextField();
        regNamaField.setPromptText("Name");

        TextField regUmurField = new TextField();
        regUmurField.setPromptText("Age");

        TextField regEmailField = new TextField();
        regEmailField.setPromptText("Email");

        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Password");

        ComboBox<String> regRoleComboBox = new ComboBox<>();
        regRoleComboBox.getItems().addAll("Penanggung", "Tanggungan", "Admin");
        regRoleComboBox.setPromptText("Select Role");
        regRoleComboBox.getSelectionModel().selectFirst();

        Button createAccountButton = new Button("Create Account");
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
                    showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields.");
                    return;
                }

                // Check if user ID already exists
                if (userDAO.getUserByUserId(userId) != null) {
                    showAlert(Alert.AlertType.ERROR, "Registration Error", "User ID already exists. Please choose another.");
                    return;
                }

                User newUser = new User(userId, nama, umur, email, password, role);
                userDAO.addUser(newUser);
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Account created for " + nama + " (" + role + ")! You can now log in.");
                registerStage.close(); // Close registration window on success

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Age must be a valid number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "An error occurred during registration: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox regLayout = new VBox(10);
        regLayout.setPadding(new Insets(20));
        regLayout.getChildren().addAll(
                new Label("User ID:"), regUserIdField,
                new Label("Name:"), regNamaField,
                new Label("Age:"), regUmurField,
                new Label("Email:"), regEmailField,
                new Label("Password:"), regPasswordField,
                new Label("Role:"), regRoleComboBox,
                createAccountButton
        );

        Scene regScene = new Scene(regLayout, 350, 450);
        registerStage.setScene(regScene);
        registerStage.show();
    }

    /**
     * Opens the main dashboard page after successful login.
     * @param user The logged-in User object.
     */
    private void openDashboard(User user) {
        DashboardPage dashboardPage = new DashboardPage(primaryStage, user);
        Scene dashboardScene = dashboardPage.createScene();
        primaryStage.setScene(dashboardScene);
        primaryStage.centerOnScreen(); // Center the stage again
    }

    /**
     * Helper method to show JavaFX Alert dialogs.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}