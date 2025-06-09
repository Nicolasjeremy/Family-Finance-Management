package com.subwayit.gui;

import com.subwayit.model.User;
import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class DashboardPage {

    private Stage primaryStage; // Reference to the main stage
    private User loggedInUser; // To hold the user who logged in
    private TransaksiDAO transaksiDAO; // DAO for fetching transactions
    private TableView<Transaksi> transactionTable; // Declare as a class member to refresh it

    // Constructor to receive the primary stage and logged-in user
    public DashboardPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.transaksiDAO = new TransaksiDAO(); // Initialize TransaksiDAO
    }

    public Scene createScene() {
        // --- Top Navigation Bar ---
        HBox topNav = createTopNavigationBar();

        // --- Main Content Area ---
        VBox mainContent = createMainContentArea();

        // --- Root Layout ---
        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(mainContent);
        root.setStyle("-fx-background-color: #F0F0F0;"); // Slightly lighter background

        Scene scene = new Scene(root, 1000, 700); // Adjust size as needed
        return scene;
    }


    private HBox createTopNavigationBar() {
        HBox navBar = new HBox(15);
        navBar.setPadding(new Insets(10, 20, 10, 20));
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setStyle("-fx-background-color: #333333;");

        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Arial", 24));
        logo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeBtn = createNavLink("Home");
        homeBtn.setOnAction(e -> {
            // Clicking Home on Dashboard should just refresh or stay
            // For now, let's keep it consistent, maybe return to initial dashboard state
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button dashboardsBtn = createNavLink("Dashboards");
        dashboardsBtn.setOnAction(e -> {
            // Same as Home for now
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button membersBtn = createNavLink("Members");
        membersBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;"); // Normal style
        // --- ACTION FOR MEMBERS BUTTON ---
        membersBtn.setOnAction(e -> {
            MembersPage membersPage = new MembersPage(primaryStage, loggedInUser);
            primaryStage.setScene(membersPage.createScene());
            primaryStage.centerOnScreen();
        });


        Button debtBtn = createNavLink("Debt");
        // Add action for Debt button later

        navBar.getChildren().addAll(logo, spacer, homeBtn, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
    }

    private Button createNavLink(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        // Add action for navigation later
        return btn;
    }

    private VBox createMainContentArea() {
        VBox content = new VBox(20); // Spacing between sections
        content.setPadding(new Insets(20));

        // --- Greeting and Action Buttons ---
        HBox headerSection = new HBox(10); // Spacing between greeting and buttons
        headerSection.setAlignment(Pos.CENTER_LEFT);
        Label greetingLabel = new Label("Hi " + loggedInUser.getNama());
        greetingLabel.setFont(Font.font("Arial", 28));
        greetingLabel.setTextFill(Color.web("#333333")); // Dark text

        Label transactionSummaryLabel = new Label("This is the transaction from the past 30 days");
        transactionSummaryLabel.setFont(Font.font("Arial", 14));
        transactionSummaryLabel.setTextFill(Color.GRAY);

        VBox greetingBox = new VBox(5, greetingLabel, transactionSummaryLabel);

        Region headerSpacer = new Region(); // Spacer to push buttons to right
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button viewReportsBtn = new Button("View reports");
        viewReportsBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        viewReportsBtn.setPadding(new Insets(10, 15, 10, 15));

        Button addTransactionBtn = new Button("Add transaction");
        addTransactionBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        addTransactionBtn.setPadding(new Insets(10, 15, 10, 15));
        // Action for Add Transaction Button:
        addTransactionBtn.setOnAction(e -> {
            AddTransactionForm form = new AddTransactionForm(loggedInUser);
            form.display(); // Show the modal form
            refreshTransactionTable(); // Refresh table data after form closes
        });

        headerSection.getChildren().addAll(greetingBox, headerSpacer, viewReportsBtn, addTransactionBtn);


        // --- Summary Cards ---
        HBox summaryCards = new HBox(20); // Spacing between cards
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        summaryCards.getChildren().addAll(
            createSummaryCard("Total transaction", "135", ""),
            createSummaryCard("This month spending", "$12,500.04", ""),
            createSummaryCard("This month earning", "$20,000.02", ""),
            createSummaryCard("Cashflow", "$7500.02", "chart_placeholder")
        );


        // --- Transaction List ---
        transactionTable = createTransactionTable(); // Assign to the class member variable
        refreshTransactionTable(); // Call to load initial data when the dashboard is created

        content.getChildren().addAll(headerSection, summaryCards, new Label("Transaction List"), transactionTable);

        

        return content;
    }

    /**
     * Refreshes the data displayed in the transaction table by querying the database.
     */
    private void refreshTransactionTable() {
        if (loggedInUser != null && loggedInUser.getUserId() != null) {
            ObservableList<Transaksi> transactions = FXCollections.observableArrayList(
                transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId())
            );
            transactionTable.setItems(transactions);
        }
    }

    private VBox createSummaryCard(String title, String value, String chartPlaceholder) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(220); // Fixed width for cards

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", 24));
        valueLabel.setTextFill(Color.web("#333333"));

        card.getChildren().addAll(titleLabel, valueLabel);

        if (!chartPlaceholder.isEmpty()) {
            // You'd add a small chart component here, e.g., an ImageView for a static chart image
            // For now, just a placeholder label
            Label chart = new Label("Chart Placeholder");
            chart.setTextFill(Color.LIGHTGRAY);
            card.getChildren().add(chart);
        }

        return card;
    }

    private TableView<Transaksi> createTransactionTable() {
        TableView<Transaksi> table = new TableView<>();
        table.setPrefHeight(300); // Set preferred height for the table

        // Transaction ID/Date column
        TableColumn<Transaksi, String> idDateCol = new TableColumn<>("Transaction ID/date");
        idDateCol.setCellValueFactory(new PropertyValueFactory<>("transaksiIdAndDate"));
        idDateCol.setPrefWidth(150);

        // Description column
        TableColumn<Transaksi, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        descriptionCol.setPrefWidth(200);

        // Payee/from column
        TableColumn<Transaksi, String> payeeCol = new TableColumn<>("Payee/from");
        payeeCol.setCellValueFactory(new PropertyValueFactory<>("payeeFrom"));
        payeeCol.setPrefWidth(150);

        // Category column
        TableColumn<Transaksi, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        categoryCol.setPrefWidth(150);

        // Amount column
        TableColumn<Transaksi, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        amountCol.setPrefWidth(100);
        amountCol.setStyle("-fx-alignment: CENTER_RIGHT;"); // Align numbers right

        table.getColumns().addAll(idDateCol, descriptionCol, payeeCol, categoryCol, amountCol);
        return table;
    }
}