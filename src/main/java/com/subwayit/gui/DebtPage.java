package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Utang;
import com.subwayit.model.User;
import com.subwayit.model.Tanggungan; // Required to check if user is Tanggungan for debt

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Needed for showAlert
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

public class DebtPage {

    private Stage primaryStage;
    private User loggedInUser;
    private UtangDAO utangDAO;
    private TableView<Utang> debtTable; // Reference to the table to refresh data

    public DebtPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.utangDAO = new UtangDAO();
    }

    public Scene createScene() {
        HBox topNav = createTopNavigationBar();
        VBox mainContent = createMainContentArea();

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F0F0F0;");

        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: #F0F0F0;");

        Scene scene = new Scene(root, 1000, 700);
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
            // Navigate back to Dashboard
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button dashboardsBtn = createNavLink("Dashboards");
        dashboardsBtn.setOnAction(e -> {
            // Navigate back to Dashboard
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button membersBtn = createNavLink("Members");
        membersBtn.setOnAction(e -> {
            // Navigate to Members Page
            MembersPage membersPage = new MembersPage(primaryStage, loggedInUser);
            primaryStage.setScene(membersPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button debtBtn = createNavLink("Debt");
        debtBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"); // Highlight current page

        navBar.getChildren().addAll(logo, spacer, homeBtn, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
    }

    private Button createNavLink(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        return btn;
    }

    private VBox createMainContentArea() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // --- Greeting and Action Buttons ---
        HBox headerSection = new HBox(10);
        headerSection.setAlignment(Pos.CENTER_LEFT);
        Label greetingLabel = new Label("Hi " + loggedInUser.getNama());
        greetingLabel.setFont(Font.font("Arial", 28));
        greetingLabel.setTextFill(Color.web("#333333"));

        Label debtSummaryLabel = new Label("This is the Debt from the past 30 days");
        debtSummaryLabel.setFont(Font.font("Arial", 14));
        debtSummaryLabel.setTextFill(Color.GRAY);

        VBox greetingBox = new VBox(5, greetingLabel, debtSummaryLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button viewReportsBtn = new Button("View reports");
        viewReportsBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        viewReportsBtn.setPadding(new Insets(10, 15, 10, 15));

        Button addDebtBtn = new Button("Add Debt");
        addDebtBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");
        addDebtBtn.setPadding(new Insets(10, 15, 10, 15));
        // Action for Add Debt Button
        addDebtBtn.setOnAction(e -> {
            // Debug: Print user information
            System.out.println("User: " + loggedInUser.getNama());
            System.out.println("User Role: " + loggedInUser.getRole());
            System.out.println("Is Tanggungan? " + (loggedInUser instanceof Tanggungan));
            
            // Allow users with role "Tanggungan" to add debt
            if (loggedInUser != null && "Tanggungan".equals(loggedInUser.getRole())) {
                // Untuk sementara, buat Tanggungan object dari User
                Tanggungan tempTanggungan = new Tanggungan(
                    loggedInUser.getUserId(), 
                    loggedInUser.getNama(), 
                    loggedInUser.getUmur(), 
                    loggedInUser.getEmail(), 
                    loggedInUser.getPassword(), 
                    "Anak", "SMA", "Pelajar"
                );
                AddDebtForm form = new AddDebtForm(tempTanggungan);
                form.display(); // Show the modal form
                refreshDebtTable(); // Refresh table data after form closes
            } else {
                showAlert(Alert.AlertType.WARNING, "Access Denied", "Only users with Tanggungan role can add new debt.");
            }
        });

        headerSection.getChildren().addAll(greetingBox, headerSpacer, viewReportsBtn, addDebtBtn);

        // --- Summary Cards ---
        HBox summaryCards = new HBox(20);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        // Placeholders for actual calculated sums
        summaryCards.getChildren().addAll(
            createSummaryCard("Total Debt Amount", "$XX,XXX.XX", ""),
            createSummaryCard("This month payment", "$X,XXX.XX", "")
        );

        // --- Debt List ---
        debtTable = createDebtTable();
        refreshDebtTable(); // Load initial data when the page is created

        content.getChildren().addAll(headerSection, summaryCards, new Label("Debt List"), debtTable);

        return content;
    }

    /**
     * Refreshes the data displayed in the debt table by querying the database.
     */
    private void refreshDebtTable() {
        if (loggedInUser instanceof Tanggungan) { // Only Tanggungan has associated debt
            ObservableList<Utang> debts = FXCollections.observableArrayList(
                utangDAO.getAllUtangForTanggungan(loggedInUser.getUserId())
            );
            debtTable.setItems(debts);
        } else {
            // If the logged-in user is not a Tanggungan, show empty list or a message
            debtTable.setItems(FXCollections.emptyObservableList());
            // Optionally, show a message in the table or disable add debt button for Tanggungan/Admin
        }
    }

    private VBox createSummaryCard(String title, String value, String chartPlaceholder) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(220);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", 24));
        valueLabel.setTextFill(Color.web("#333333"));

        card.getChildren().addAll(titleLabel, valueLabel);

        if (!chartPlaceholder.isEmpty()) {
            Label chart = new Label("Chart Placeholder");
            chart.setTextFill(Color.LIGHTGRAY);
            card.getChildren().add(chart);
        }
        return card;
    }

    private TableView<Utang> createDebtTable() {
        TableView<Utang> table = new TableView<>();
        table.setPrefHeight(300);

        // Debt ID Column
        TableColumn<Utang, String> idCol = new TableColumn<>("Debt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("utangId"));
        idCol.setPrefWidth(100);

        // Creditor Column (Debtor Name)
        TableColumn<Utang, String> creditorCol = new TableColumn<>("Creditor");
        creditorCol.setCellValueFactory(new PropertyValueFactory<>("creditor"));
        creditorCol.setPrefWidth(150);

        // Amount Column
        TableColumn<Utang, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("jumlah")); // maps to 'jumlah' in Utang model
        amountCol.setPrefWidth(120);
        amountCol.setStyle("-fx-alignment: CENTER_RIGHT;");

        // Due Date Column
        TableColumn<Utang, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDueDate")); // Uses helper method in Utang model
        dueDateCol.setPrefWidth(120);

        // Status Column
        TableColumn<Utang, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, creditorCol, amountCol, dueDateCol, statusCol);
        return table;
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
}