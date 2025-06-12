package com.subwayit.gui;

import com.subwayit.dao.*;
import com.subwayit.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminDashboard {

    private Stage primaryStage;
    private User loggedInAdmin;

    // DAOs
    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private TransaksiDAO transaksiDAO;
    private UtangDAO utangDAO;

    // Theme colors
    private static final String ADMIN_BLUE = "#3B82F6";
    private static final String LIGHT_BLUE = "#EBF8FF";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";

    public AdminDashboard(Stage primaryStage, User admin, UserDAO userDAO,
            PenanggungDAO penanggungDAO, TanggunganDAO tanggunganDAO,
            AdminDAO adminDAO, TransaksiDAO transaksiDAO, UtangDAO utangDAO) {
        this.primaryStage = primaryStage;
        this.loggedInAdmin = admin;
        this.userDAO = userDAO;
        this.penanggungDAO = penanggungDAO;
        this.tanggunganDAO = tanggunganDAO;
        this.adminDAO = adminDAO;
        this.transaksiDAO = transaksiDAO;
        this.utangDAO = utangDAO;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setTop(createTopNavigationBar());
        root.setCenter(createMainContentArea());
        root.setStyle("-fx-background-color: white;");

        return new Scene(root, 1200, 800);
    }

    private HBox createTopNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: " + ADMIN_BLUE + ";");

        Label logo = new Label("SUBWAYIT ADMIN");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashboardBtn = createNavLink("Admin Dashboard", "🛠️");
        dashboardBtn.setStyle(dashboardBtn.getStyle() + "-fx-background-color: rgba(255,255,255,0.2);");

        Button profileBtn = createProfileButton();

        Button signOutBtn = createNavLink("Sign Out", "🚪");
        signOutBtn.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Sign Out");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to sign out?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    com.subwayit.App app = new com.subwayit.App();
                    try {
                        app.start(primaryStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        navBar.getChildren().addAll(logo, spacer, dashboardBtn, profileBtn, signOutBtn);
        return navBar;
    }

    private Button createNavLink(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(8, 16, 8, 16));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: rgba(255,255,255,0.1);"));
        btn.setOnMouseExited(
                e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.1);", "")));
        return btn;
    }

    private Button createProfileButton() {
        String userInitial = loggedInAdmin.getNama().substring(0, 1).toUpperCase();
        Button profileBtn = new Button("👤 " + userInitial);
        profileBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        profileBtn.setPadding(new Insets(8, 12, 8, 12));
        profileBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");

        profileBtn.setOnMouseEntered(e -> profileBtn
                .setStyle(profileBtn.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.3)")));
        profileBtn.setOnMouseExited(e -> profileBtn
                .setStyle(profileBtn.getStyle().replace("rgba(255,255,255,0.3)", "rgba(255,255,255,0.2)")));

        profileBtn.setOnAction(e -> showAdminProfileInfo());

        return profileBtn;
    }

    private VBox createMainContentArea() {
        VBox mainContent = new VBox(40);
        mainContent.setPadding(new Insets(40, 50, 40, 50));
        mainContent.setAlignment(Pos.CENTER);

        // Welcome Section
        VBox welcomeSection = createWelcomeSection();

        // Features Section
        VBox featuresSection = createFeaturesSection();

        // Quick Stats Section
        VBox statsSection = createStatsSection();

        mainContent.getChildren().addAll(welcomeSection, featuresSection, statsSection);
        return mainContent;
    }

    private VBox createWelcomeSection() {
        VBox welcomeSection = new VBox(20);
        welcomeSection.setAlignment(Pos.CENTER);

        Label welcomeTitle = new Label("🛠️ Welcome to Admin Dashboard");
        welcomeTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        welcomeTitle.setTextFill(Color.web(ADMIN_BLUE));

        Label welcomeSubtitle = new Label("Hello, " + loggedInAdmin.getNama() + "!");
        welcomeSubtitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 20));
        welcomeSubtitle.setTextFill(Color.web(TEXT_DARK));

        Label description = new Label("You have administrator access to the SUBWAYIT Family Finance Management System");
        description.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        description.setTextFill(Color.web(TEXT_GRAY));

        welcomeSection.getChildren().addAll(welcomeTitle, welcomeSubtitle, description);
        return welcomeSection;
    }

    private VBox createFeaturesSection() {
        VBox featuresSection = new VBox(25);
        featuresSection.setAlignment(Pos.CENTER);

        Label featuresTitle = new Label("🔧 Admin Features");
        featuresTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        featuresTitle.setTextFill(Color.web(TEXT_DARK));

        HBox featuresRow = new HBox(30);
        featuresRow.setAlignment(Pos.CENTER);

        VBox feature1 = createFeatureCard("👥", "User Management", "Manage all system users and their roles");
        VBox feature2 = createFeatureCard("👨‍👩‍👧‍👦", "Family Groups", "View and organize family groups");
        VBox feature3 = createFeatureCard("📊", "System Reports", "Generate comprehensive system reports");
        VBox feature4 = createFeatureCard("⚙️", "System Settings", "Configure system-wide settings");

        featuresRow.getChildren().addAll(feature1, feature2, feature3, feature4);

        featuresSection.getChildren().addAll(featuresTitle, featuresRow);
        return featuresSection;
    }

    private VBox createFeatureCard(String icon, String title, String description) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 20, 25, 20));
        card.setPrefWidth(200);
        card.setPrefHeight(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4); " +
                "-fx-border-color: " + LIGHT_BLUE + "; -fx-border-width: 2; -fx-border-radius: 12;");

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 28));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web(TEXT_DARK));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        descLabel.setTextFill(Color.web(TEXT_GRAY));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: " + LIGHT_BLUE + ";"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-background-color: " + LIGHT_BLUE + ";",
                "-fx-background-color: white;")));

        return card;
    }

    private VBox createStatsSection() {
        VBox statsSection = new VBox(20);
        statsSection.setAlignment(Pos.CENTER);

        Label statsTitle = new Label("📈 Quick System Stats");
        statsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        statsTitle.setTextFill(Color.web(TEXT_DARK));

        HBox statsRow = new HBox(40);
        statsRow.setAlignment(Pos.CENTER);

        // Get actual counts from database
        int totalPenanggung = 0;
        int totalTanggungan = 0;
        int totalAdmins = 0;

        try {
            // Get all Penanggung
            totalPenanggung = penanggungDAO.getAllPenanggung().size();

            // Get all Tanggungan
            totalTanggungan = tanggunganDAO.getAllTanggungan().size();

            // Get all Admins
            totalAdmins = adminDAO.getAllAdmins().size();
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }

        int totalUsers = totalPenanggung + totalTanggungan + totalAdmins;

        VBox totalUsersCard = createStatCard("Total Users", String.valueOf(totalUsers), "👥");
        VBox totalFamiliesCard = createStatCard("Total Families", String.valueOf(totalPenanggung), "👨‍👩‍👧‍👦");
        VBox totalAdminsCard = createStatCard("Total Admins", String.valueOf(totalAdmins), "🛠️");

        statsRow.getChildren().addAll(totalUsersCard, totalFamiliesCard, totalAdminsCard);

        statsSection.getChildren().addAll(statsTitle, statsRow);
        return statsSection;
    }

    private VBox createStatCard(String title, String value, String icon) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 30, 20, 30));
        card.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 10; " +
                "-fx-border-color: " + ADMIN_BLUE + "; -fx-border-width: 1; -fx-border-radius: 10;");

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 24));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        valueLabel.setTextFill(Color.web(ADMIN_BLUE));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        titleLabel.setTextFill(Color.web(TEXT_GRAY));

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private void showAdminProfileInfo() {
        Alert profileAlert = new Alert(Alert.AlertType.INFORMATION);
        profileAlert.setTitle("Admin Profile");
        profileAlert.setHeaderText("Administrator Details");

        StringBuilder profileInfo = new StringBuilder();
        profileInfo.append("🛠️ Administrator Profile\n");
        profileInfo.append("═".repeat(30)).append("\n\n");
        profileInfo.append("👤 Name: ").append(loggedInAdmin.getNama()).append("\n");
        profileInfo.append("📧 Email: ").append(loggedInAdmin.getEmail()).append("\n");
        profileInfo.append("🎂 Age: ").append(loggedInAdmin.getUmur()).append(" years old\n");
        profileInfo.append("👔 Role: ").append(loggedInAdmin.getRole()).append("\n");
        profileInfo.append("🆔 User ID: ").append(loggedInAdmin.getUserId()).append("\n");

        profileInfo.append("\n⚡ System Privileges:\n");
        profileInfo.append("• View all user information\n");
        profileInfo.append("• Manage family groups\n");
        profileInfo.append("• System administration access\n");
        profileInfo.append("• Full database access\n");

        profileAlert.setContentText(profileInfo.toString());
        profileAlert.getDialogPane().setPrefSize(400, 500);
        profileAlert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        profileAlert.showAndWait();
    }
}
