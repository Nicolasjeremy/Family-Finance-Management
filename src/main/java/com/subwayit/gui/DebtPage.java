package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.dao.UtangDAO.UtangWithUserInfo;
import com.subwayit.model.Utang;
import com.subwayit.model.User;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.Penanggung;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DebtPage {

    private Stage primaryStage;
    private User loggedInUser;
    private UtangDAO utangDAO;
    private TableView<?> debtTable; // Use wildcard to support both table types

    // Green Theme Color Palette (same as DashboardPage)
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    // Summary value labels
    private Label totalDebtValue = new Label();
    private Label thisMonthPaymentValue = new Label();
    private Label overdueDebtValue = new Label();
    private Label activeDebtValue = new Label();

    public DebtPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.utangDAO = new UtangDAO();
    }

    public Scene createScene() {
        HBox topNav = createTopNavigationBar();
        VBox mainContent = createMainContentArea();

        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(mainContent);
        
        root.setStyle("-fx-background-color: white;");

        return new Scene(root, 1200, 800);
    }

    private HBox createTopNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        
        navBar.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);");

        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);
        logo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 1, 0, 0, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeBtn = createNavLink("Home", "🏠");
        homeBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });
        
        Button dashboardsBtn = createNavLink("Dashboard", "📊");
        dashboardsBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });
        
        Button membersBtn = createNavLink("Members", "👥");
        membersBtn.setOnAction(e -> {
            MembersPage membersPage = new MembersPage(primaryStage, loggedInUser);
            primaryStage.setScene(membersPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button debtBtn = createNavLink("Debt", "💰");
        debtBtn.setStyle(debtBtn.getStyle() + 
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-color: rgba(255, 255, 255, 0.4); " +
            "-fx-border-width: 1px;");

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
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + 
            "-fx-background-color: rgba(255,255,255,0.15);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
            .replace("-fx-background-color: rgba(255,255,255,0.15);", "")));
        
        return btn;
    }

    private VBox createMainContentArea() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));

        HBox header = createHeader();
        HBox summary = createSummaryCards();
        VBox tableSection = createTableSection();

        content.getChildren().addAll(header, summary, tableSection);
        return content;
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox welcomeSection = new VBox(8);
        
        String titleText = "Penanggung".equals(loggedInUser.getRole()) ? 
            "Family Debt Management 💰" : "Your Debt Management 💰";
        Label welcomeLabel = new Label("Hi, " + loggedInUser.getNama() + " - " + titleText);
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcomeLabel.setTextFill(Color.web(TEXT_DARK));
        
        String descText = "Penanggung".equals(loggedInUser.getRole()) ? 
            "📊 Manage and pay all family debt obligations" : 
            "📊 Manage your debt portfolio and payment schedule";
        Label descLabel = new Label(descText);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        descLabel.setTextFill(Color.web(TEXT_GRAY));
        
        welcomeSection.getChildren().addAll(welcomeLabel, descLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewReportsBtn = createActionButton("📈 View Reports", "#EEEEEE", "#DDDDDD");
        viewReportsBtn.setTextFill(Color.web(TEXT_DARK));
        
        Button addDebtBtn = createActionButton("➕ Add Debt", PRIMARY_GREEN, DARK_GREEN);
        addDebtBtn.setTextFill(Color.WHITE);
        
        // Action for Add Debt Button - only for Tanggungan
        addDebtBtn.setOnAction(e -> {
            if ("Tanggungan".equals(loggedInUser.getRole())) {
                Tanggungan tempTanggungan = new Tanggungan(
                    loggedInUser.getUserId(), 
                    loggedInUser.getNama(), 
                    loggedInUser.getUmur(), 
                    loggedInUser.getEmail(), 
                    loggedInUser.getPassword(), 
                    "Anak", "SMA", "Pelajar"
                );
                AddDebtForm form = new AddDebtForm(tempTanggungan);
                form.display();
                refreshDebtTable();
                updateDebtSummary();
            } else {
                showAlert(Alert.AlertType.WARNING, "Access Denied", "Only Tanggungan can add new debt.");
            }
        });

        HBox buttonGroup = new HBox(15);
        if ("Tanggungan".equals(loggedInUser.getRole())) {
            buttonGroup.getChildren().addAll(viewReportsBtn, addDebtBtn);
        } else {
            buttonGroup.getChildren().add(viewReportsBtn);
        }
        
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
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgColor, hoverColor)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverColor, bgColor)));
        
        return btn;
    }

    private HBox createSummaryCards() {
        HBox summary = new HBox(20);
        summary.setAlignment(Pos.CENTER);
        
        VBox totalCard = createSummaryCard("Total Debt Amount", totalDebtValue, "📊");
        VBox paymentCard = createSummaryCard("This Month Payment", thisMonthPaymentValue, "💸");
        VBox overdueCard = createSummaryCard("Overdue Debts", overdueDebtValue, "⚠️");
        VBox activeCard = createSummaryCard("Active Debts", activeDebtValue, "📈");
        
        summary.getChildren().addAll(totalCard, paymentCard, overdueCard, activeCard);
        return summary;
    }

    private VBox createSummaryCard(String title, Label value, String icon) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20, 20, 20, 20));
        card.setPrefWidth(280);
        
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 8; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                     "-fx-border-color: #E2E8F0; " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 8;");

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(18));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        titleLabel.setTextFill(Color.web(TEXT_GRAY));
        
        titleRow.getChildren().addAll(iconLabel, titleLabel);
        
        value.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        value.setTextFill(Color.web(TEXT_DARK));
        
        Rectangle underline = new Rectangle(40, 3);
        underline.setFill(Color.web(PRIMARY_GREEN));
        underline.setArcWidth(3);
        underline.setArcHeight(3);
        
        card.getChildren().addAll(titleRow, value, underline);
        
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-border-color: " + PRIMARY_GREEN + ";"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
            .replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;")));
        
        return card;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(20);
        
        HBox sectionHeader = new HBox(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle accentBar = new Rectangle(4, 30);
        accentBar.setFill(Color.web(PRIMARY_GREEN));
        accentBar.setArcWidth(4);
        accentBar.setArcHeight(4);
        
        String titleText = "Penanggung".equals(loggedInUser.getRole()) ? 
            "Family Debt Management" : "Debt Management";
        Label tableTitle = new Label(titleText);
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        tableTitle.setTextFill(Color.web(TEXT_DARK));
        
        VBox titleSection = new VBox(5);
        String subtitleText = "Penanggung".equals(loggedInUser.getRole()) ? 
            "💳 Track and pay all family debt obligations" : 
            "💳 Track and manage your debt obligations";
        Label subtitle = new Label(subtitleText);
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        
        titleSection.getChildren().addAll(tableTitle, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button allDebtsBtn = new Button("🗂️ All debts");
        allDebtsBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        allDebtsBtn.setPadding(new Insets(8, 16, 8, 16));
        allDebtsBtn.setStyle("-fx-background-color: " + LIGHT_GREEN + "; " +
                            "-fx-text-fill: " + DARK_GREEN + "; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand;");
        
        sectionHeader.getChildren().addAll(accentBar, titleSection, spacer, allDebtsBtn);
        
        // Create appropriate table based on user role
        if ("Penanggung".equals(loggedInUser.getRole())) {
            debtTable = createFamilyDebtTable();
        } else {
            debtTable = createPersonalDebtTable();
        }
        
        refreshDebtTable();
        updateDebtSummary();
        
        tableSection.getChildren().addAll(sectionHeader, debtTable);
        return tableSection;
    }

    // Table for Penanggung to see all family debts
    private TableView<UtangWithUserInfo> createFamilyDebtTable() {
        TableView<UtangWithUserInfo> table = new TableView<>();
        table.setPrefHeight(400);
        
        table.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 8; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2); " +
                      "-fx-border-color: #E2E8F0; " +
                      "-fx-border-width: 1; " +
                      "-fx-border-radius: 8;");

        // Debt Owner Column (new for family view)
        TableColumn<UtangWithUserInfo, String> ownerCol = new TableColumn<>("Debt Owner");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        ownerCol.setPrefWidth(120);
        styleTableColumn(ownerCol);

        TableColumn<UtangWithUserInfo, String> idCol = new TableColumn<>("Debt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("utangId"));
        idCol.setPrefWidth(80);
        styleTableColumn(idCol);

        TableColumn<UtangWithUserInfo, String> creditorCol = new TableColumn<>("Creditor");
        creditorCol.setCellValueFactory(new PropertyValueFactory<>("creditor"));
        creditorCol.setPrefWidth(120);
        styleTableColumn(creditorCol);

        TableColumn<UtangWithUserInfo, Double> amountCol = new TableColumn<>("Original");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        amountCol.setPrefWidth(100);
        styleTableColumn(amountCol);
        
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("Rp " + formatRupiah(val));
                    setStyle("-fx-text-fill: " + TEXT_GRAY + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<UtangWithUserInfo, Double> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("sisaUtang"));
        remainingCol.setPrefWidth(100);
        styleTableColumn(remainingCol);
        
        remainingCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("Rp " + formatRupiah(val));
                    setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<UtangWithUserInfo, String> estimatedCol = new TableColumn<>("Est. Monthly");
        estimatedCol.setCellValueFactory(new PropertyValueFactory<>("formattedEstimasiBulanan"));
        estimatedCol.setPrefWidth(100);
        styleTableColumn(estimatedCol);

        TableColumn<UtangWithUserInfo, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDueDate"));
        dueDateCol.setPrefWidth(90);
        styleTableColumn(dueDateCol);

        TableColumn<UtangWithUserInfo, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(90);
        styleTableColumn(statusCol);
        
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.toLowerCase().contains("lunas")) {
                        setStyle("-fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions column for Penanggung
        TableColumn<UtangWithUserInfo, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        styleTableColumn(actionCol);
        
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button payBtn = createTableButton("💳 Pay", LIGHT_GREEN, "#D0E8C5", DARK_GREEN);
            private final Button viewBtn = createTableButton("👁️ View", "#E3F2FD", "#BBDEFB", "#1976D2");
            
            {
                payBtn.setOnAction(e -> {
                    UtangWithUserInfo utang = getTableView().getItems().get(getIndex());
                    
                    if (utang.getSisaUtang() <= 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Debt Paid", "This debt has already been fully paid.");
                        return;
                    }
                    
                    Penanggung penanggung = new Penanggung(
                        loggedInUser.getUserId(),
                        loggedInUser.getNama(),
                        loggedInUser.getUmur(),
                        loggedInUser.getEmail(),
                        loggedInUser.getPassword()
                    );
                    
                    // Convert UtangWithUserInfo to Utang for payment form
                    Utang utangForPayment = new Utang(
                        utang.getUtangId(),
                        utang.getUserId(),
                        utang.getJumlah(),
                        utang.getBunga(),
                        utang.getTanggalJatuhTempo(),
                        utang.getStatus(),
                        utang.getCreditor(),
                        utang.getSisaUtang()
                    );
                    
                    AddPayDebtForm payForm = new AddPayDebtForm(penanggung, utangForPayment);
                    payForm.display();
                    refreshDebtTable();
                    updateDebtSummary();
                });

                viewBtn.setOnAction(e -> {
                    UtangWithUserInfo utang = getTableView().getItems().get(getIndex());
                    showFamilyDebtDetails(utang);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actionPane = new HBox(8, payBtn, viewBtn);
                    setGraphic(actionPane);
                }
            }
        });

        table.getColumns().addAll(ownerCol, idCol, creditorCol, amountCol, remainingCol, estimatedCol, dueDateCol, statusCol, actionCol);
        return table;
    }

    // Original table for Tanggungan personal debts
    private TableView<Utang> createPersonalDebtTable() {
        TableView<Utang> table = new TableView<>();
        table.setPrefHeight(400);
        
        table.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 8; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2); " +
                      "-fx-border-color: #E2E8F0; " +
                      "-fx-border-width: 1; " +
                      "-fx-border-radius: 8;");

        TableColumn<Utang, String> idCol = new TableColumn<>("Debt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("utangId"));
        idCol.setPrefWidth(100);
        styleTableColumn(idCol);

        TableColumn<Utang, String> creditorCol = new TableColumn<>("Creditor");
        creditorCol.setCellValueFactory(new PropertyValueFactory<>("creditor"));
        creditorCol.setPrefWidth(140);
        styleTableColumn(creditorCol);

        TableColumn<Utang, Double> amountCol = new TableColumn<>("Original");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        amountCol.setPrefWidth(120);
        styleTableColumn(amountCol);
        
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("Rp " + formatRupiah(val));
                    setStyle("-fx-text-fill: " + TEXT_GRAY + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Utang, Double> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("sisaUtang"));
        remainingCol.setPrefWidth(120);
        styleTableColumn(remainingCol);
        
        remainingCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("Rp " + formatRupiah(val));
                    setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Utang, String> estimatedCol = new TableColumn<>("Est. Monthly");
        estimatedCol.setCellValueFactory(new PropertyValueFactory<>("formattedEstimasiBulanan"));
        estimatedCol.setPrefWidth(120);
        styleTableColumn(estimatedCol);

        TableColumn<Utang, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDueDate"));
        dueDateCol.setPrefWidth(100);
        styleTableColumn(dueDateCol);

        TableColumn<Utang, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        styleTableColumn(statusCol);
        
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.toLowerCase().contains("lunas")) {
                        setStyle("-fx-text-fill: " + DARK_GREEN + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + RED + "; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions column for Tanggungan
        TableColumn<Utang, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(180);
        styleTableColumn(actionCol);
        
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = createTableButton("✏️ Edit", "#EEEEEE", "#DDDDDD", TEXT_DARK);
            private final Button viewBtn = createTableButton("👁️ View", "#E3F2FD", "#BBDEFB", "#1976D2");
            
            {
                editBtn.setOnAction(e -> {
                    Utang utang = getTableView().getItems().get(getIndex());
                    showAlert(Alert.AlertType.INFORMATION, "Edit Debt", "Edit functionality will be implemented soon.");
                });

                viewBtn.setOnAction(e -> {
                    Utang utang = getTableView().getItems().get(getIndex());
                    showDebtDetails(utang);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actionPane = new HBox(8, editBtn, viewBtn);
                    setGraphic(actionPane);
                }
            }
        });

        table.getColumns().addAll(idCol, creditorCol, amountCol, remainingCol, estimatedCol, dueDateCol, statusCol, actionCol);
        return table;
    }

    private void showFamilyDebtDetails(UtangWithUserInfo utang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Family Debt Details");
        alert.setHeaderText("Debt Information for " + utang.getUserName());
        
        String details = String.format(
            "Debt Owner: %s\n" +
            "Debt ID: %s\n" +
            "Creditor: %s\n" +
            "Original Amount: Rp %s\n" +
            "Remaining Amount: Rp %s\n" +
            "Interest Rate: %.2f%%\n" +
            "Due Date: %s\n" +
            "Status: %s\n" +
            "Estimated Monthly Payment: %s",
            utang.getUserName(),
            utang.getUtangId(),
            utang.getCreditor(),
            formatRupiah(utang.getJumlah()),
            formatRupiah(utang.getSisaUtang()),
            utang.getBunga() * 100,
            utang.getFormattedDueDate(),
            utang.getStatus(),
            utang.getFormattedEstimasiBulanan()
        );
        
        alert.setContentText(details);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
    }

    private void showDebtDetails(Utang utang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Debt Details");
        alert.setHeaderText("Debt Information for " + utang.getCreditor());
        
        String details = String.format(
            "Debt ID: %s\n" +
            "Creditor: %s\n" +
            "Original Amount: Rp %s\n" +
            "Remaining Amount: Rp %s\n" +
            "Interest Rate: %.2f%%\n" +
            "Due Date: %s\n" +
            "Status: %s\n" +
            "Estimated Monthly Payment: %s",
            utang.getUtangId(),
            utang.getCreditor(),
            formatRupiah(utang.getJumlah()),
            formatRupiah(utang.getSisaUtang()),
            utang.getBunga() * 100,
            utang.getFormattedDueDate(),
            utang.getStatus(),
            utang.getFormattedEstimasiBulanan()
        );
        
        alert.setContentText(details);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
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
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgColor, hoverColor)));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(hoverColor, bgColor)));
        
        return btn;
    }

    @SuppressWarnings("unchecked")
    private void refreshDebtTable() {
        if (loggedInUser != null) {
            if ("Penanggung".equals(loggedInUser.getRole())) {
                List<UtangWithUserInfo> familyDebts = utangDAO.getAllFamilyDebts();
                ((TableView<UtangWithUserInfo>) debtTable).setItems(FXCollections.observableArrayList(familyDebts));
            } else {
                List<Utang> personalDebts = utangDAO.getAllUtangForTanggungan(loggedInUser.getUserId());
                ((TableView<Utang>) debtTable).setItems(FXCollections.observableArrayList(personalDebts));
            }
        } else {
            debtTable.setItems(FXCollections.emptyObservableList());
        }
    }

    private void updateDebtSummary() {
        double totalAmount = 0;
        double thisMonthPayment = 0;
        int overdueCount = 0;
        int activeCount = 0;
        
        LocalDate now = LocalDate.now();
        
        if ("Penanggung".equals(loggedInUser.getRole())) {
            // Calculate for all family debts
            List<UtangWithUserInfo> familyDebts = utangDAO.getAllFamilyDebts();
            for (UtangWithUserInfo debt : familyDebts) {
                totalAmount += debt.getJumlah();
                
                if (!debt.getStatus().toLowerCase().contains("lunas")) {
                    activeCount++;
                    
                    if (debt.getTanggalJatuhTempo().isBefore(now)) {
                        overdueCount++;
                    }
                    
                    if (debt.getTanggalJatuhTempo().getMonth() == now.getMonth() && 
                        debt.getTanggalJatuhTempo().getYear() == now.getYear()) {
                        thisMonthPayment += debt.getJumlah();
                    }
                }
            }
        } else {
            // Calculate for personal debts only
            List<Utang> personalDebts = utangDAO.getAllUtangForTanggungan(loggedInUser.getUserId());
            for (Utang debt : personalDebts) {
                totalAmount += debt.getJumlah();
                
                if (!debt.getStatus().toLowerCase().contains("lunas")) {
                    activeCount++;
                    
                    if (debt.getTanggalJatuhTempo().isBefore(now)) {
                        overdueCount++;
                    }
                    
                    if (debt.getTanggalJatuhTempo().getMonth() == now.getMonth() && 
                        debt.getTanggalJatuhTempo().getYear() == now.getYear()) {
                        thisMonthPayment += debt.getJumlah();
                    }
                }
            }
        }
        
        totalDebtValue.setText("Rp " + formatRupiah(totalAmount));
        thisMonthPaymentValue.setText("Rp " + formatRupiah(thisMonthPayment));
        overdueDebtValue.setText(String.valueOf(overdueCount));
        activeDebtValue.setText(String.valueOf(activeCount));
        
        if (overdueCount > 0) {
            overdueDebtValue.setTextFill(Color.web(RED));
        } else {
            overdueDebtValue.setTextFill(Color.web(DARK_GREEN));
        }
    }

    private String formatRupiah(double amt) {
        return String.format("%,.0f", amt).replace(',', '.');
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}