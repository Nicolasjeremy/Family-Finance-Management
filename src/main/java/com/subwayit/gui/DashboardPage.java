package com.subwayit.gui;

import com.subwayit.model.User;
import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;
import com.subwayit.gui.DebtPage; // Add this import

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.time.LocalDate;
import java.util.List;

public class DashboardPage {

    private Stage primaryStage;
    private User loggedInUser;
    private TransaksiDAO transaksiDAO;
    private TableView<Transaksi> transactionTable;

    private Label totalTransactionValue = new Label();
    private Label thisMonthSpendingValue = new Label();
    private Label thisMonthEarningValue = new Label();
    private Label cashflowValue = new Label();

    // Green Theme Color Palette
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    public DashboardPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.transaksiDAO = new TransaksiDAO();
    }

    public Scene createScene() {
        HBox topNav = createTopNavigationBar();
        VBox mainContent = createMainContentArea();

        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(mainContent);
        
        // Clean white background
        root.setStyle("-fx-background-color: white;");

        return new Scene(root, 1200, 800);
    }

    private HBox createTopNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        
        // Green navigation bar
        navBar.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);");

        // Logo styling
        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);
        logo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 1, 0, 0, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Navigation buttons
        Button homeBtn = createNavLink("Home", "🏠");
        homeBtn.setOnAction(e -> primaryStage.setScene(createScene()));
        
        Button dashboardsBtn = createNavLink("Dashboard", "📊");
        dashboardsBtn.setOnAction(e -> primaryStage.setScene(createScene()));
        dashboardsBtn.setStyle(dashboardsBtn.getStyle() + 
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-color: rgba(255, 255, 255, 0.4); " +
            "-fx-border-width: 1px;");
        
        Button membersBtn = createNavLink("Members", "👥");
        membersBtn.setOnAction(e -> {
            MembersPage mp = new MembersPage(primaryStage, loggedInUser);
            primaryStage.setScene(mp.createScene());
        });
        Button debtBtn = createNavLink("Debt", "💰");
        debtBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;"); // Normal style
        // --- ACTION FOR DEBT BUTTON ---
        debtBtn.setOnAction(e -> {
            DebtPage debtPage = new DebtPage(primaryStage, loggedInUser);
            primaryStage.setScene(debtPage.createScene());
            primaryStage.centerOnScreen();
        });

        navBar.getChildren().addAll(logo, spacer, homeBtn, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
    }

    private Button createNavLink(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(10, 16, 10, 16));
        btn.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8; " +
                    "-fx-cursor: hand;");
        
        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + 
            "-fx-background-color: rgba(255,255,255,0.15);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
            .replace("-fx-background-color: rgba(255,255,255,0.15);", "")));
        
        return btn;
    }

    private VBox createMainContentArea() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));

        // Header section
        HBox header = createHeader();
        
        // Summary cards
        HBox summary = createSummaryCards();

        // Table section
        VBox tableSection = createTableSection();

        content.getChildren().addAll(header, summary, tableSection);
        return content;
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Welcome section
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

        // Action buttons
        Button viewReportsBtn = createActionButton("📈 View Reports", "#EEEEEE", "#DDDDDD");
        viewReportsBtn.setTextFill(Color.web(TEXT_DARK));
        
        Button addTransactionBtn = createActionButton("➕ Add Transaction", PRIMARY_GREEN, DARK_GREEN);
        addTransactionBtn.setTextFill(Color.WHITE);
        
        addTransactionBtn.setOnAction(e -> {
            AddTransactionForm form = new AddTransactionForm(loggedInUser);
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
        btn.setStyle("-fx-background-color: " + bgColor + "; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        // Hover effects
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
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20, 20, 20, 20));
        card.setPrefWidth(280);
        
        // Clean card styling
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 8; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                     "-fx-border-color: #E2E8F0; " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 8;");

        // Icon and title row
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(18));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        titleLabel.setTextFill(Color.web(TEXT_GRAY));
        
        titleRow.getChildren().addAll(iconLabel, titleLabel);
        
        // Value styling with high contrast
        value.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        value.setTextFill(Color.web(TEXT_DARK));
        
        // Add a subtle green underline
        Rectangle underline = new Rectangle(40, 3);
        underline.setFill(Color.web(PRIMARY_GREEN));
        underline.setArcWidth(3);
        underline.setArcHeight(3);
        
        card.getChildren().addAll(titleRow, value, underline);
        
        // Subtle hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-border-color: " + PRIMARY_GREEN + ";"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
            .replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;")));
        
        return card;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(20);
        
        // Section header
        HBox sectionHeader = new HBox(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        // Green accent bar
        Rectangle accentBar = new Rectangle(4, 30);
        accentBar.setFill(Color.web(PRIMARY_GREEN));
        accentBar.setArcWidth(4);
        accentBar.setArcHeight(4);
        
        Label tableTitle = new Label("Recent Transactions");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        tableTitle.setTextFill(Color.web(TEXT_DARK));
        
        VBox titleSection = new VBox(5);
        Label subtitle = new Label("✨ Manage your financial records beautifully");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        
        titleSection.getChildren().addAll(tableTitle, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // All transactions button
        Button allTransactionsBtn = new Button("🗂️ All transactions");
        allTransactionsBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        allTransactionsBtn.setPadding(new Insets(8, 16, 8, 16));
        allTransactionsBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; " +
                                  "-fx-text-fill: " + DARK_GREEN + "; " +
                                  "-fx-background-radius: 8; " +
                                  "-fx-cursor: hand;");
        
        sectionHeader.getChildren().addAll(accentBar, titleSection, spacer, allTransactionsBtn);
        
        // Clean table with good contrast
        transactionTable = createTransactionTable();
        refreshTransactionTable();
        updateFinancialSummary();
        
        tableSection.getChildren().addAll(sectionHeader, transactionTable);
        return tableSection;
    }

    private TableView<Transaksi> createTransactionTable() {
        TableView<Transaksi> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Clean table styling
        table.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 8; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2); " +
                      "-fx-border-color: #E2E8F0; " +
                      "-fx-border-width: 1; " +
                      "-fx-border-radius: 8;");

        // Columns with good contrast
        TableColumn<Transaksi, String> idDateCol = new TableColumn<>("ID / Date");
        idDateCol.setCellValueFactory(new PropertyValueFactory<>("transaksiIdAndDate"));
        idDateCol.setPrefWidth(140);
        styleTableColumn(idDateCol);

        TableColumn<Transaksi, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        descCol.setPrefWidth(220);
        styleTableColumn(descCol);

        TableColumn<Transaksi, String> payeeCol = new TableColumn<>("Payee / From");
        payeeCol.setCellValueFactory(new PropertyValueFactory<>("payeeFrom"));
        payeeCol.setPrefWidth(150);
        styleTableColumn(payeeCol);

        TableColumn<Transaksi, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        catCol.setPrefWidth(130);
        styleTableColumn(catCol);

        TableColumn<Transaksi, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        amtCol.setPrefWidth(120);
        styleTableColumn(amtCol);
        
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    setText("Rp " + formatRupiah(val));
                    
                    if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
                        setStyle("-fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions column
        TableColumn<Transaksi, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(180);
        styleTableColumn(actionCol);
        
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createTableButton("✏️ Edit", "#EEEEEE", "#DDDDDD", TEXT_DARK);
            private final Button delBtn = createTableButton("🗑️ Delete", "#FFEEEE", "#FFDDDD", RED);
            
            {
                editBtn.setOnAction(e -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    AddTransactionForm form = new AddTransactionForm(loggedInUser, t);
                    form.display();
                    refreshTransactionTable();
                    updateFinancialSummary();
                });
                
                delBtn.setOnAction(e -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Delete");
                    alert.setHeaderText("Delete Transaction");
                    alert.setContentText("Are you sure you want to delete this transaction?");
                    
                    if (alert.showAndWait().get() == ButtonType.OK) {
                        transaksiDAO.deleteTransaksi(t.getTransaksiId());
                        refreshTransactionTable();
                        updateFinancialSummary();
                    }
                });
            }
            
            private final HBox actionPane = new HBox(8, editBtn, delBtn);
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionPane);
            }
        });

        table.getColumns().addAll(idDateCol, descCol, payeeCol, catCol, amtCol, actionCol);
        return table;
    }

    private void styleTableColumn(TableColumn<?, ?> column) {
        column.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: medium;");
    }

    private Button createTableButton(String text, String bgColor, String hoverColor, String textColor) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        btn.setPadding(new Insets(6, 12, 6, 12));
        btn.setTextFill(Color.web(textColor));
        btn.setStyle("-fx-background-color: " + bgColor + "; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: #E2E8F0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 6;");
        
        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgColor, hoverColor)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverColor, bgColor)));
        
        return btn;
    }

    private void refreshTransactionTable() {
        if (loggedInUser != null) {
            List<Transaksi> lst = transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId());
            transactionTable.setItems(FXCollections.observableArrayList(lst));
        }
    }

    private void updateFinancialSummary() {
        List<Transaksi> lst = transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId());
        int total = lst.size();
        double spend = 0, earn = 0;
        LocalDate now = LocalDate.now();
        
        for (Transaksi t : lst) {
            if (t.getTanggalTransaksi().getMonth() == now.getMonth() && 
                t.getTanggalTransaksi().getYear() == now.getYear()) {
                if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
                    earn += t.getNominal();
                } else {
                    spend += t.getNominal();
                }
            }
        }
        
        totalTransactionValue.setText(String.valueOf(total));
        thisMonthSpendingValue.setText("Rp " + formatRupiah(spend));
        thisMonthEarningValue.setText("Rp " + formatRupiah(earn));
        
        double cashflow = earn - spend;
        cashflowValue.setText("Rp " + formatRupiah(cashflow));
        cashflowValue.setTextFill(cashflow >= 0 ? Color.web(DARK_GREEN) : Color.web(RED));
    }

    private String formatRupiah(double amt) {
        return String.format("%,.0f", amt).replace(',', '.');
    }
}