package com.subwayit.gui;

import com.subwayit.dao.*;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.User;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MembersPage {

    private Stage primaryStage;
    private User loggedInUser;

    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private TransaksiDAO transaksiDAO;
    private UtangDAO utangDAO;

    private ObservableList<User> familyMembers;
    private GridPane memberCardsGrid;

    // Theme colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";

    public MembersPage(Stage primaryStage, User user, UserDAO userDAO, PenanggungDAO penanggungDAO,
            TanggunganDAO tanggunganDAO, AdminDAO adminDAO, TransaksiDAO transaksiDAO, UtangDAO utangDAO) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.userDAO = userDAO;
        this.penanggungDAO = penanggungDAO;
        this.tanggunganDAO = tanggunganDAO;
        this.adminDAO = adminDAO;
        this.transaksiDAO = transaksiDAO;
        this.utangDAO = utangDAO; // Store the injected DAO
        this.familyMembers = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setTop(createModernNavigationBar());

        ScrollPane scrollPane = new ScrollPane(createModernContentArea());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");

        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, "
                + LIGHT_GREEN + " 100%);");

        return new Scene(root, 1200, 800);
    }

    private HBox createModernNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: " + PRIMARY_GREEN + ";");

        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashboardsBtn = createModernNavLink("Dashboard", "📊");
        dashboardsBtn.setOnAction(e -> {
            DashboardPage dp = new DashboardPage(primaryStage, loggedInUser, userDAO, penanggungDAO, tanggunganDAO,
                    adminDAO, transaksiDAO, utangDAO);
            primaryStage.setScene(dp.createScene());
        });

        Button membersBtn = createModernNavLink("Members", "👥");
        membersBtn.setStyle(membersBtn.getStyle() + "-fx-background-color: rgba(255,255,255,0.2);");

        Button debtBtn = createModernNavLink("Debt", "💰");
        debtBtn.setOnAction(e -> {
            DebtPage debtPage = new DebtPage(primaryStage, loggedInUser, userDAO, penanggungDAO, tanggunganDAO,
                    adminDAO, transaksiDAO, utangDAO);
            primaryStage.setScene(debtPage.createScene());
        });

        // Add profile button
        Button profileBtn = createProfileButton();

        // Add sign out button
        Button signOutBtn = createModernNavLink("Sign Out", "🚪");
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

        navBar.getChildren().addAll(logo, spacer, dashboardsBtn, membersBtn, debtBtn, profileBtn, signOutBtn);
        return navBar;
    }

    private Button createProfileButton() {
        String userInitial = loggedInUser.getNama().substring(0, 1).toUpperCase();
        Button profileBtn = new Button("👤 " + userInitial);
        profileBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        profileBtn.setPadding(new Insets(8, 12, 8, 12));
        profileBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");

        profileBtn.setOnMouseEntered(e -> profileBtn
                .setStyle(profileBtn.getStyle().replace("rgba(255,255,255,0.2)", "rgba(255,255,255,0.3)")));
        profileBtn.setOnMouseExited(e -> profileBtn
                .setStyle(profileBtn.getStyle().replace("rgba(255,255,255,0.3)", "rgba(255,255,255,0.2)")));

        profileBtn.setOnAction(e -> showProfileInfo());

        return profileBtn;
    }

    private void showProfileInfo() {
        Alert profileAlert = new Alert(Alert.AlertType.INFORMATION);
        profileAlert.setTitle("Profile Information");
        profileAlert.setHeaderText("Current User Details");

        StringBuilder profileInfo = new StringBuilder();
        profileInfo.append("👤 Name: ").append(loggedInUser.getNama()).append("\n");
        profileInfo.append("📧 Email: ").append(loggedInUser.getEmail()).append("\n");
        profileInfo.append("🎂 Age: ").append(loggedInUser.getUmur()).append(" years old\n");
        profileInfo.append("👔 Role: ").append(loggedInUser.getRole()).append("\n");
        profileInfo.append("🆔 User ID: ").append(loggedInUser.getUserId()).append("\n");

        if ("Penanggung".equals(loggedInUser.getRole())) {
            Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            if (penanggung != null) {
                profileInfo.append("💼 Pekerjaan: ")
                        .append(penanggung.getPekerjaan() != null ? penanggung.getPekerjaan() : "Not specified")
                        .append("\n");
                profileInfo.append("👨‍👩‍👧‍👦 Tanggungan: ").append(penanggung.getAnggotaTanggunganIds().size())
                        .append(" members\n");

                if (!penanggung.getAnggotaTanggunganIds().isEmpty()) {
                    profileInfo.append("\n📋 INFORMASI LOGIN TANGGUNGAN:\n");
                    profileInfo.append("═".repeat(40)).append("\n");
                    profileInfo.append(
                            "💡 TIP: Klik tombol 'Copy' di samping password untuk menyalin User ID & Password!\n\n");

                    // Create VBox to hold all dependents with their individual copy buttons
                    VBox dependentsContainer = new VBox(15);

                    int tanggunganCount = 1;
                    for (String tanggunganId : penanggung.getAnggotaTanggunganIds()) {
                        Tanggungan tanggungan = tanggunganDAO.getTanggunganById(tanggunganId);
                        if (tanggungan != null) {
                            // Create a container for each dependent
                            VBox tanggunganBox = new VBox(8);
                            tanggunganBox.setStyle(
                                    "-fx-background-color: #F7FAFC; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

                            // Header for this tanggungan
                            Label tanggunganHeader = new Label(
                                    "👤 Tanggungan " + tanggunganCount + ": " + tanggungan.getNama());
                            tanggunganHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                            tanggunganHeader.setTextFill(Color.web(TEXT_DARK));

                            // Create info grid
                            GridPane infoGrid = new GridPane();
                            infoGrid.setHgap(10);
                            infoGrid.setVgap(8);

                            // User ID row
                            Label userIdLabel = new Label("🆔 User ID:");
                            userIdLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
                            TextField userIdField = new TextField(tanggungan.getUserId());
                            userIdField.setEditable(false);
                            userIdField.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0;");

                            // Password row with copy button
                            Label passwordLabel = new Label("🔒 Password:");
                            passwordLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));

                            HBox passwordRow = new HBox(10);
                            TextField passwordField = new TextField(tanggungan.getPassword());
                            passwordField.setEditable(false);
                            passwordField.setPrefWidth(150);
                            passwordField.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0;");

                            // Copy button next to password
                            Button copyBtn = new Button("📋 Copy");
                            copyBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 11));
                            copyBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; -fx-text-fill: white; " +
                                    "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10 5 10;");

                            copyBtn.setOnAction(e -> {
                                String credentialsToCopy = "User ID: " + tanggungan.getUserId() + "\nPassword: "
                                        + tanggungan.getPassword();
                                copyToClipboard(credentialsToCopy);
                                showModernAlert(Alert.AlertType.INFORMATION, "Copied!",
                                        "User ID dan Password " + tanggungan.getNama() + " telah disalin!");
                            });

                            passwordRow.getChildren().addAll(passwordField, copyBtn);
                            passwordRow.setAlignment(Pos.CENTER_LEFT);

                            // Other info
                            Label positionLabel = new Label("🏠 Position:");
                            positionLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
                            TextField positionField = new TextField(
                                    tanggungan.getPosisiKeluarga() != null ? tanggungan.getPosisiKeluarga()
                                            : "Not specified");
                            positionField.setEditable(false);
                            positionField.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0;");

                            Label emailLabel = new Label("📧 Email:");
                            emailLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
                            TextField emailField = new TextField(tanggungan.getEmail());
                            emailField.setEditable(false);
                            emailField.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0;");

                            // Add to grid
                            infoGrid.add(userIdLabel, 0, 0);
                            infoGrid.add(userIdField, 1, 0);
                            infoGrid.add(passwordLabel, 0, 1);
                            infoGrid.add(passwordRow, 1, 1);
                            infoGrid.add(positionLabel, 0, 2);
                            infoGrid.add(positionField, 1, 2);
                            infoGrid.add(emailLabel, 0, 3);
                            infoGrid.add(emailField, 1, 3);

                            tanggunganBox.getChildren().addAll(tanggunganHeader, infoGrid);
                            dependentsContainer.getChildren().add(tanggunganBox);

                            // Add to text info (simplified version)
                            profileInfo.append("👤 Tanggungan ").append(tanggunganCount).append(": ")
                                    .append(tanggungan.getNama()).append("\n");
                            profileInfo.append("   • User ID: ").append(tanggungan.getUserId()).append("\n");
                            profileInfo.append("   • Password: ").append(tanggungan.getPassword())
                                    .append(" [Copy button available]\n");
                            if (tanggunganCount < penanggung.getAnggotaTanggunganIds().size()) {
                                profileInfo.append("   ─".repeat(20)).append("\n");
                            }
                            tanggunganCount++;
                        }
                    }

                    // Add scroll pane for the dependents container
                    ScrollPane scrollPane = new ScrollPane(dependentsContainer);
                    scrollPane.setFitToWidth(true);
                    scrollPane.setPrefHeight(300);
                    scrollPane.setStyle("-fx-background-color: transparent;");

                    profileAlert.getDialogPane().setExpandableContent(scrollPane);
                } else {
                    profileInfo.append("\n📝 Belum ada tanggungan yang terdaftar.");
                }
            }
        } else if ("Tanggungan".equals(loggedInUser.getRole())) {
            Tanggungan tanggungan = tanggunganDAO.getTanggunganById(loggedInUser.getUserId());
            if (tanggungan != null) {
                profileInfo.append("💼 Pekerjaan: ")
                        .append(tanggungan.getPekerjaan() != null ? tanggungan.getPekerjaan() : "Not specified")
                        .append("\n");
                profileInfo.append("🏠 Posisi Keluarga: ").append(
                        tanggungan.getPosisiKeluarga() != null ? tanggungan.getPosisiKeluarga() : "Not specified")
                        .append("\n");
                profileInfo.append("🎓 Pendidikan: ")
                        .append(tanggungan.getPendidikan() != null ? tanggungan.getPendidikan() : "Not specified");

                if (tanggungan.getPenanggungId() != null && !tanggungan.getPenanggungId().isEmpty()) {
                    Penanggung penanggung = penanggungDAO.getPenanggungById(tanggungan.getPenanggungId());
                    if (penanggung != null) {
                        profileInfo.append("\n👨‍👩‍👧‍👦 Kepala Keluarga: ").append(penanggung.getNama());
                    }
                }
            }
        }

        profileAlert.setContentText(profileInfo.toString());
        profileAlert.getDialogPane().setPrefSize(600, 700); // Increased size for better layout
        profileAlert.getDialogPane().setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 12px;");
        profileAlert.setResizable(true);
        profileAlert.showAndWait();
    }

    private void copyToClipboard(String text) {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private void showModernAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
    }

    private Button createModernNavLink(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(10, 16, 10, 16));
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: rgba(255,255,255,0.15);"));
        btn.setOnMouseExited(
                e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.15);", "")));
        return btn;
    }

    private VBox createModernContentArea() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(createModernHeaderSection(), createModernMembersSection());
        return content;
    }

    private VBox createModernHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER_LEFT);

        HBox titleRow = new HBox(15);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Circle titleIcon = new Circle(25, Color.web(PRIMARY_GREEN, 0.2));
        titleIcon.setStroke(Color.web(PRIMARY_GREEN));
        titleIcon.setStrokeWidth(2);

        VBox titleContainer = new VBox(5);
        Label membersTitle = new Label("Family Financial Members 👨‍👩‍👧‍👦");
        membersTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));

        Label subtitle = new Label("Manage your family's financial overview and member details");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web(TEXT_GRAY));

        titleContainer.getChildren().addAll(membersTitle, subtitle);
        titleRow.getChildren().addAll(titleIcon, titleContainer);

        Rectangle decorativeLine = new Rectangle(80, 3, Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleRow, decorativeLine);
        return headerSection;
    }

    private VBox createModernMembersSection() {
        VBox membersSection = new VBox(20);
        HBox sectionHeader = new HBox(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Rectangle accentBar = new Rectangle(4, 30, Color.web(PRIMARY_GREEN));
        accentBar.setArcWidth(4);
        accentBar.setArcHeight(4);

        Label sectionTitle = new Label("Family Members");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        sectionHeader.getChildren().addAll(accentBar, sectionTitle, spacer);

        // --- FIX 1: Only Penanggung can see the "Add Member" button ---
        if ("Penanggung".equals(loggedInUser.getRole())) {
            Button addMemberBtn = new Button("➕ Add Member");
            addMemberBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
            addMemberBtn.setTextFill(Color.WHITE);
            addMemberBtn.setStyle(
                    "-fx-background-color: " + PRIMARY_GREEN + "; -fx-background-radius: 8; -fx-cursor: hand;");

            addMemberBtn.setOnAction(e -> {
                AddMemberForm addMemberForm = new AddMemberForm(loggedInUser, userDAO, penanggungDAO, tanggunganDAO,
                        adminDAO);
                addMemberForm.display();
                loadAndDisplayMembers();
            });
            sectionHeader.getChildren().add(addMemberBtn);
        }

        memberCardsGrid = new GridPane();
        memberCardsGrid.setHgap(25);
        memberCardsGrid.setVgap(25);
        memberCardsGrid.setAlignment(Pos.CENTER);

        loadAndDisplayMembers();

        membersSection.getChildren().addAll(sectionHeader, memberCardsGrid);
        return membersSection;
    }

    private void loadAndDisplayMembers() {
        memberCardsGrid.getChildren().clear();
        familyMembers.clear();

        if ("Penanggung".equals(loggedInUser.getRole())) {
            Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            if (penanggung != null) {
                familyMembers.add(penanggung);
                if (penanggung.getAnggotaTanggunganIds() != null) {
                    for (String tanggunganId : penanggung.getAnggotaTanggunganIds()) {
                        Tanggungan tanggungan = tanggunganDAO.getTanggunganById(tanggunganId);
                        if (tanggungan != null) {
                            familyMembers.add(tanggungan);
                        }
                    }
                }
            }
        } else if ("Tanggungan".equals(loggedInUser.getRole())) {
            Tanggungan tanggungan = tanggunganDAO.getTanggunganById(loggedInUser.getUserId());
            if (tanggungan != null && tanggungan.getPenanggungId() != null && !tanggungan.getPenanggungId().isEmpty()) {
                String penanggungId = tanggungan.getPenanggungId();
                Penanggung headOfFamily = penanggungDAO.getPenanggungById(penanggungId);
                if (headOfFamily != null) {
                    familyMembers.add(headOfFamily);
                    if (headOfFamily.getAnggotaTanggunganIds() != null) {
                        for (String memberId : headOfFamily.getAnggotaTanggunganIds()) {
                            Tanggungan member = tanggunganDAO.getTanggunganById(memberId);
                            if (member != null)
                                familyMembers.add(member);
                        }
                    }
                }
            } else if (tanggungan != null) {
                familyMembers.add(tanggungan);
            }
        }
        populateGrid();
    }

    private void populateGrid() {
        int col = 0;
        int row = 0;
        for (User member : familyMembers) {
            VBox memberCard = createModernMemberCard(member);
            memberCardsGrid.add(memberCard, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
        // --- FIX 1 (cont.): Only Penanggung can see the "Add Member" card ---
        if ("Penanggung".equals(loggedInUser.getRole())) {
            VBox addMemberCard = createModernAddMemberCard();
            memberCardsGrid.add(addMemberCard, col, row);
        }
    }

    private VBox createModernMemberCard(User member) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20, 15, 20, 15));
        card.setPrefSize(220, 320); // Increased height to fit the edit button
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 16;");

        // Profile Picture
        StackPane profileContainer = new StackPane();
        profileContainer.setPrefSize(80, 80);
        profileContainer
                .setStyle("-fx-background-color: " + PRIMARY_GREEN + "30; -fx-background-radius: 40; -fx-border-color: "
                        + PRIMARY_GREEN + "; -fx-border-width: 2; -fx-border-radius: 40;");
        Label profileIcon = new Label(member.getNama().substring(0, 1).toUpperCase());
        profileIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        profileIcon.setTextFill(Color.web(PRIMARY_GREEN));
        profileContainer.getChildren().add(profileIcon);

        // Member Info
        VBox infoContainer = new VBox(8);
        infoContainer.setAlignment(Pos.CENTER);
        Label nameLabel = new Label(member.getNama());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        Label ageLabel = new Label(member.getUmur() + " years old");
        ageLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        ageLabel.setTextFill(Color.web(TEXT_GRAY));
        HBox roleBadge = new HBox(
                new Label((member.getRole().equals("Penanggung") ? "👨‍💼 " : "👨‍🎓 ") + member.getRole()));
        roleBadge.setAlignment(Pos.CENTER);
        roleBadge.setPadding(new Insets(6, 12, 6, 12));
        roleBadge.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 12;");
        infoContainer.getChildren().addAll(nameLabel, ageLabel, roleBadge);

        // --- FIX 2 & 3: Add Edit Button with Role-Based Logic ---
        Button editBtn = new Button("✏️ Edit Profile");
        editBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        editBtn.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 8; -fx-cursor: hand;");

        boolean canEdit = false;
        if ("Penanggung".equals(loggedInUser.getRole())) {
            // Penanggung can edit anyone in the family
            canEdit = true;
        } else if ("Tanggungan".equals(loggedInUser.getRole())) {
            // Tanggungan can only edit themselves
            if (loggedInUser.getUserId().equals(member.getUserId())) {
                canEdit = true;
            }
        }

        editBtn.setDisable(!canEdit);

        editBtn.setOnAction(e -> {
            // Open a new form for editing
            EditMemberForm editForm = new EditMemberForm(member, userDAO, penanggungDAO, tanggunganDAO);
            editForm.display();
            // Refresh the view after the form is closed
            loadAndDisplayMembers();
        });

        VBox.setVgrow(infoContainer, Priority.ALWAYS); // Make info container grow
        card.getChildren().addAll(profileContainer, infoContainer, new Region(), editBtn); // Add button at the bottom

        card.setOnMouseEntered(e -> card.setStyle(
                card.getStyle() + "-fx-border-color: " + PRIMARY_GREEN + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(
                card.getStyle().replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;")
                        .replace("-fx-scale-x: 1.02; -fx-scale-y: 1.02;", "")));
        return card;
    }

    private VBox createModernAddMemberCard() {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 320); // Match height of member card
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: " + PRIMARY_GREEN
                + "60; -fx-border-width: 2; -fx-border-radius: 16; -fx-border-style: dashed; -fx-cursor: hand;");

        StackPane iconContainer = new StackPane(new Label("➕"));
        iconContainer.setPrefSize(80, 80);
        iconContainer.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-background-radius: 40;");

        Label addLabel = new Label("Add New Member");
        addLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        card.getChildren().addAll(iconContainer, addLabel);

        card.setOnMouseClicked(e -> {
            AddMemberForm addMemberForm = new AddMemberForm(loggedInUser, userDAO, penanggungDAO, tanggunganDAO,
                    adminDAO);
            addMemberForm.display();
            loadAndDisplayMembers();
        });

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: " + LIGHT_GREEN + ";"));
        card.setOnMouseExited(e -> card.setStyle(
                card.getStyle().replace("-fx-background-color: " + LIGHT_GREEN + ";", "-fx-background-color: white;")));
        return card;
    }
}