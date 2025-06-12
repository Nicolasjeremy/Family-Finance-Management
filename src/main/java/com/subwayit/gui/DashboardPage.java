package com.subwayit.gui;

import com.subwayit.dao.*;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.Transaksi;
import com.subwayit.model.User;
// import com.subwayit.gui.DebtPage;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class DashboardPage {

    private Stage primaryStage;
    private User loggedInUser;

    // --- FIX 1: Declare all necessary DAO attributes ---
    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private TransaksiDAO transaksiDAO;
    private UtangDAO utangDAO;
    private TableView<Transaksi> transactionTable;
    private Label totalTransactionValue = new Label("0");
    private Label thisMonthSpendingValue = new Label("Rp 0");
    private Label thisMonthEarningValue = new Label("Rp 0");
    private Label cashflowValue = new Label("Rp 0");

    // Green Theme Color Palette
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    public DashboardPage(Stage primaryStage, User user, UserDAO userDAO, PenanggungDAO penanggungDAO,
            TanggunganDAO tanggunganDAO, AdminDAO adminDAO, TransaksiDAO transaksiDAO, UtangDAO utangDAO) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
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
        navBar.setStyle("-fx-background-color: " + PRIMARY_GREEN + ";");

        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button dashboardsBtn = createNavLink("Dashboard", "📊");
        dashboardsBtn.setStyle(dashboardsBtn.getStyle() + "-fx-background-color: rgba(255,255,255,0.2);");

        Button membersBtn = createNavLink("Members", "👥");
        membersBtn.setOnAction(e -> {
            MembersPage mp = new MembersPage(primaryStage, loggedInUser, userDAO, penanggungDAO, tanggunganDAO,
                    adminDAO, transaksiDAO, utangDAO);
            primaryStage.setScene(mp.createScene());
        });

        Button debtBtn = createNavLink("Debt", "💰");
        debtBtn.setOnAction(e -> {
            DebtPage debtPage = new DebtPage(
                    primaryStage,
                    loggedInUser,
                    userDAO,
                    penanggungDAO,
                    tanggunganDAO,
                    adminDAO,
                    transaksiDAO,
                    utangDAO);
            primaryStage.setScene(debtPage.createScene());
        });

        // Add profile button
        Button profileBtn = createProfileButton();

        // Add sign out button
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

        navBar.getChildren().addAll(logo, spacer, dashboardsBtn, membersBtn, debtBtn, profileBtn, signOutBtn);
        return navBar;
    }

    private Button createNavLink(String text, String icon) {
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

    private VBox createMainContentArea() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.getChildren().addAll(createHeader(), createSummaryCards(), createTableSection());
        return content;
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeSection = new VBox(8);
        Label welcomeLabel = new Label("Hi, " + loggedInUser.getNama() + " 👋");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.web(TEXT_DARK));

        Label descLabel = new Label("⭐ Here's your beautiful financial overview for the past 30 days");
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        descLabel.setTextFill(Color.web(TEXT_GRAY));
        welcomeSection.getChildren().addAll(welcomeLabel, descLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewReportsBtn = createActionButton("📈 View Reports", "#EEEEEE", "#DDDDDD");
        viewReportsBtn.setTextFill(Color.web(TEXT_DARK));

        Button addTransactionBtn = createActionButton("➕ Add Transaction", PRIMARY_GREEN, DARK_GREEN);
        addTransactionBtn.setTextFill(Color.WHITE);
        addTransactionBtn.setOnAction(e -> {
            // Pass the TransaksiDAO to the form
            AddTransactionForm form = new AddTransactionForm(loggedInUser, transaksiDAO, null); // Pass null for
                                                                                                // existing transaction
            form.display();
            refreshTransactionTable();
            updateFinancialSummary();
        });

        HBox buttonGroup = new HBox(15, viewReportsBtn, addTransactionBtn);
        header.getChildren().addAll(welcomeSection, spacer, buttonGroup);
        return header;
    }

    private Button createActionButton(String text, String bgColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(12, 20, 12, 20));
        btn.setStyle("-fx-background-color: " + bgColor
                + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgColor, hoverColor)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverColor, bgColor)));
        return btn;
    }

    private HBox createSummaryCards() {
        HBox summary = new HBox(20);
        summary.setAlignment(Pos.CENTER);
        VBox totalCard = createSummaryCard("Total Transactions", totalTransactionValue, "📊");
        VBox spendingCard = createSummaryCard("This Month Spending", thisMonthSpendingValue, "💸");
        VBox earningCard = createSummaryCard("This Month Earning", thisMonthEarningValue, "💰");
        VBox cashflowCard = createSummaryCard("Net Cashflow", cashflowValue, "📈");
        summary.getChildren().addAll(totalCard, spendingCard, earningCard, cashflowCard);
        return summary;
    }

    private VBox createSummaryCard(String title, Label value, String icon) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");

        HBox titleRow = new HBox(10, new Label(icon), new Label(title));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        value.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        value.setTextFill(Color.web(TEXT_DARK));

        Rectangle underline = new Rectangle(40, 3, Color.web(PRIMARY_GREEN));
        underline.setArcWidth(3);
        underline.setArcHeight(3);

        card.getChildren().addAll(titleRow, value, underline);
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-border-color: " + PRIMARY_GREEN + ";"));
        card.setOnMouseExited(e -> card.setStyle(
                card.getStyle().replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;")));
        return card;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(20);
        HBox sectionHeader = new HBox(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Rectangle accentBar = new Rectangle(4, 30, Color.web(PRIMARY_GREEN));
        accentBar.setArcWidth(4);
        accentBar.setArcHeight(4);

        Label tableTitle = new Label("Recent Transactions");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        Label subtitle = new Label("✨ Manage your financial records beautifully");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        VBox titleSection = new VBox(5, tableTitle, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button allTransactionsBtn = new Button("🗂️ All transactions");
        allTransactionsBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        allTransactionsBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; -fx-text-fill: " + DARK_GREEN
                + "; -fx-background-radius: 8; -fx-cursor: hand;");

        sectionHeader.getChildren().addAll(accentBar, titleSection, spacer, allTransactionsBtn);

        transactionTable = createTransactionTable();
        refreshTransactionTable();
        updateFinancialSummary();

        tableSection.getChildren().addAll(sectionHeader, transactionTable);
        return tableSection;
    }

    private TableView<Transaksi> createTransactionTable() {
        TableView<Transaksi> table = new TableView<>();
        table.setPrefHeight(400);
        table.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 8;");

        // Columns setup
        TableColumn<Transaksi, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        descCol.setPrefWidth(250);

        TableColumn<Transaksi, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        catCol.setPrefWidth(150);

        TableColumn<Transaksi, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("tanggalTransaksi"));
        dateCol.setPrefWidth(120);

        TableColumn<Transaksi, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        amtCol.setPrefWidth(150);
        amtCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(formatRupiah(item));
                    Transaksi t = getTableView().getItems().get(getIndex());
                    setTextFill(t.getJenis().equalsIgnoreCase("Pemasukan") ? Color.web(DARK_GREEN) : Color.web(RED));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Transaksi, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = createTableButton("✏️ Edit", "#EEEEEE", "#DDDDDD", TEXT_DARK);
            private final Button delBtn = createTableButton("🗑️ Delete", "#FFEEEE", "#FFDDDD", RED);
            private final HBox pane = new HBox(8, editBtn, delBtn);

            {
                delBtn.setOnAction(event -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this transaction?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            transaksiDAO.deleteTransaksi(t.getTransaksiId());
                            refreshTransactionTable();
                            updateFinancialSummary();
                        }
                    });
                });
                editBtn.setOnAction(event -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    AddTransactionForm form = new AddTransactionForm(loggedInUser, transaksiDAO, t);
                    form.display();
                    refreshTransactionTable();
                    updateFinancialSummary();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(descCol, catCol, dateCol, amtCol, actionCol);
        return table;
    }

    private Button createTableButton(String text, String bgColor, String hoverColor, String textColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        btn.setPadding(new Insets(6, 12, 6, 12));
        btn.setTextFill(Color.web(textColor));
        btn.setStyle("-fx-background-color: " + bgColor
                + "; -fx-background-radius: 6; -fx-cursor: hand; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 6;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgColor, hoverColor)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverColor, bgColor)));
        return btn;
    }

    private void refreshTransactionTable() {
        if (loggedInUser == null || transaksiDAO == null)
            return;

        List<Transaksi> transactionsToShow = getTransactionsBasedOnRole();
        transactionTable.setItems(FXCollections.observableArrayList(transactionsToShow));
    }

    private void updateFinancialSummary() {
        if (loggedInUser == null || transaksiDAO == null)
            return;

        List<Transaksi> relevantTransactions = getTransactionsBasedOnRole();

        int total = relevantTransactions.size();
        double spend = 0, earn = 0;
        LocalDate now = LocalDate.now();

        for (Transaksi t : relevantTransactions) {
            LocalDate trxDate = t.getTanggalTransaksi();
            if (trxDate.getMonth() == now.getMonth() && trxDate.getYear() == now.getYear()) {
                if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
                    earn += t.getNominal();
                } else {
                    spend += t.getNominal();
                }
            }
        }

        totalTransactionValue.setText(String.valueOf(total));
        thisMonthSpendingValue.setText(formatRupiah(spend));
        thisMonthEarningValue.setText(formatRupiah(earn));

        double cashflow = earn - spend;
        cashflowValue.setText(formatRupiah(cashflow));
        cashflowValue.setTextFill(cashflow >= 0 ? Color.web(DARK_GREEN) : Color.web(RED));
    }

    private List<Transaksi> getTransactionsBasedOnRole() {
        String role = loggedInUser.getRole();

        if ("Penanggung".equals(role)) {
            // Penanggung sees their own transactions + all their dependents' transactions.

            // 1. Get the full Penanggung object to access their list of dependents
            Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            if (penanggung == null) {
                return FXCollections.observableArrayList(); // Should not happen, but safe check
            }

            // 2. Create a list of all user IDs in the family
            List<String> familyUserIds = new java.util.ArrayList<>();
            familyUserIds.add(penanggung.getUserId()); // Add the Penanggung's own ID
            familyUserIds.addAll(penanggung.getAnggotaTanggunganIds()); // Add all dependent IDs

            // 3. Fetch transactions for all users in the family
            return transaksiDAO.getTransactionsForMultipleUsers(familyUserIds);

        } else {
            // Tanggungan and Admin (for now) only see their own transactions.
            return transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId());
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount).replace(",00", "");
    }

    private Button createProfileButton() {
        // Create profile button with user's initial
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

        // Add role-specific information
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
                        profileInfo.append("\n👨‍👩‍👧‍👦 Head of Family: ").append(penanggung.getNama());
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
}