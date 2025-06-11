package com.subwayit.gui;

import com.subwayit.dao.*;
import com.subwayit.dao.UtangDAO.UtangWithUserInfo;
import com.subwayit.model.Penanggung;
import com.subwayit.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DebtPage {

    private Stage primaryStage;
    private User loggedInUser;

    // --- All DAOs are now injected via the constructor ---
    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private TransaksiDAO transaksiDAO;
    private UtangDAO utangDAO;

    private TableView<UtangWithUserInfo> debtTable;
    private ObservableList<UtangWithUserInfo> debtList;

    // Summary Labels
    private Label totalDebtValue = new Label("Rp 0");
    private Label remainingDebtValue = new Label("Rp 0");
    private Label overdueDebtValue = new Label("0");

    // Theme Colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String TEXT_DARK = "#2D3748";
    private static final String RED = "#E53E3E";

    // --- Constructor now accepts all dependencies ---
    public DebtPage(Stage primaryStage, User user, UserDAO userDAO, PenanggungDAO penanggungDAO, TanggunganDAO tanggunganDAO, AdminDAO adminDAO, TransaksiDAO transaksiDAO, UtangDAO utangDAO) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.userDAO = userDAO;
        this.penanggungDAO = penanggungDAO;
        this.tanggunganDAO = tanggunganDAO;
        this.adminDAO = adminDAO;
        this.transaksiDAO = transaksiDAO;
        this.utangDAO = utangDAO; // Use injected DAO
        this.debtList = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setTop(createTopNavigationBar());
        root.setCenter(new ScrollPane(createMainContentArea()));
        root.setStyle("-fx-background-color: #F7FAFC;");
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

        // --- Navigation now correctly passes all DAOs ---
        Button dashboardsBtn = createNavLink("Dashboard", "📊");
        dashboardsBtn.setOnAction(e -> {
            DashboardPage dp = new DashboardPage(primaryStage, loggedInUser, userDAO, penanggungDAO, tanggunganDAO, adminDAO, transaksiDAO, utangDAO);
            primaryStage.setScene(dp.createScene());
        });

        Button membersBtn = createNavLink("Members", "👥");
        membersBtn.setOnAction(e -> {
            MembersPage mp = new MembersPage(primaryStage, loggedInUser, userDAO, penanggungDAO, tanggunganDAO, adminDAO, transaksiDAO, utangDAO);
            primaryStage.setScene(mp.createScene());
        });

        Button debtBtn = createNavLink("Debt", "💰");
        debtBtn.setStyle(debtBtn.getStyle() + "-fx-background-color: rgba(255,255,255,0.2);");

        navBar.getChildren().addAll(logo, spacer, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
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
        
        VBox welcomeSection = new VBox(5);
        Label titleLabel = new Label("Manajemen Utang & Cicilan");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcomeSection.getChildren().add(titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- FIX: Check user role before casting and creating the form ---
        if ("Penanggung".equals(loggedInUser.getRole())) {
            Button addDebtBtn = new Button("➕ Tambah Utang");
            addDebtBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            
            addDebtBtn.setOnAction(e -> {
                // Because we've already checked the role, this cast is now safe.
                Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
                if (penanggung != null) {
                    AddDebtForm form = new AddDebtForm(penanggung, tanggunganDAO, utangDAO, null);
                    form.display();
                    refreshDebtTable();
                } else {
                    // Handle rare case where Penanggung data might be inconsistent
                    // showAlert(Alert.AlertType.ERROR, "Data Error", "Could not load Penanggung details.");
                }
            });
            header.getChildren().addAll(welcomeSection, spacer, addDebtBtn);
        } else {
             header.getChildren().addAll(welcomeSection, spacer);
        }
        return header;
    }

    private HBox createSummaryCards() {
        HBox summary = new HBox(20);
        summary.getChildren().addAll(
            createSummaryCard("Total Pokok Utang", totalDebtValue, TEXT_DARK),
            createSummaryCard("Sisa Tagihan Aktif", remainingDebtValue, RED),
            createSummaryCard("Utang Jatuh Tempo", overdueDebtValue, RED)
        );
        return summary;
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox(20);
        debtTable = createDebtTable();
        refreshDebtTable();
        tableSection.getChildren().add(debtTable);
        return tableSection;
    }

    private TableView<UtangWithUserInfo> createDebtTable() {
        TableView<UtangWithUserInfo> table = new TableView<>();
        table.setPlaceholder(new Label("Tidak ada data utang ditemukan."));

        TableColumn<UtangWithUserInfo, String> ownerCol = new TableColumn<>("Pemilik");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<UtangWithUserInfo, String> creditorCol = new TableColumn<>("Kreditor");
        creditorCol.setCellValueFactory(new PropertyValueFactory<>("creditor"));

        TableColumn<UtangWithUserInfo, Double> amountCol = new TableColumn<>("Jumlah Pokok");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        amountCol.setCellFactory(tc -> createRupiahCell(false));

        TableColumn<UtangWithUserInfo, Double> remainingCol = new TableColumn<>("Sisa Tagihan");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("sisaUtang"));
        remainingCol.setCellFactory(tc -> createRupiahCell(true));

        TableColumn<UtangWithUserInfo, LocalDate> dueDateCol = new TableColumn<>("Jatuh Tempo");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("tanggalJatuhTempo"));

        TableColumn<UtangWithUserInfo, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<UtangWithUserInfo, Void> actionCol = createActionColumn();

        table.getColumns().addAll(ownerCol, creditorCol, amountCol, remainingCol, dueDateCol, statusCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void refreshDebtTable() {
        List<String> userIdsToFetch = new ArrayList<>();
        if ("Penanggung".equals(loggedInUser.getRole())) {
            Penanggung p = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            userIdsToFetch.add(p.getUserId());
            if (p.getAnggotaTanggunganIds() != null) {
                userIdsToFetch.addAll(p.getAnggotaTanggunganIds());
            }
        } else {
            userIdsToFetch.add(loggedInUser.getUserId());
        }
        
        List<UtangWithUserInfo> debts = utangDAO.getDebtsWithUserInfo(userIdsToFetch);
        debtList.setAll(debts);
        debtTable.setItems(debtList);
        updateDebtSummary();
    }

    private void updateDebtSummary() {
        double totalAmount = debtList.stream().mapToDouble(UtangWithUserInfo::getJumlah).sum();
        double remainingAmount = debtList.stream().mapToDouble(UtangWithUserInfo::getSisaUtang).sum();
        long overdueCount = debtList.stream().filter(u -> u.getSisaUtang() > 0 && u.getTanggalJatuhTempo().isBefore(LocalDate.now())).count();

        totalDebtValue.setText(formatRupiah(totalAmount));
        remainingDebtValue.setText(formatRupiah(remainingAmount));
        overdueDebtValue.setText(String.valueOf(overdueCount));
    }

    private TableColumn<UtangWithUserInfo, Void> createActionColumn() {
        TableColumn<UtangWithUserInfo, Void> actionCol = new TableColumn<>("Tindakan");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button payBtn = new Button("Bayar");
            private final Button editBtn = new Button("Edit");
            private final HBox pane = new HBox(5, payBtn, editBtn);
            
        {
            pane.setAlignment(Pos.CENTER);
            payBtn.setStyle("-fx-background-color: " + DARK_GREEN + "; -fx-text-fill: white; -fx-font-size: 10px;");
            editBtn.setStyle("-fx-background-color: #A0AEC0; -fx-text-fill: white; -fx-font-size: 10px;");

            payBtn.setOnAction(event -> {
                UtangWithUserInfo utang = getTableView().getItems().get(getIndex());
                // FIX: First, get the full Penanggung object from the database
                Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
                if (penanggung != null) {
                    AddPayDebtForm form = new AddPayDebtForm(penanggung, utang, utangDAO);
                    form.display();
                    refreshDebtTable();
                } else {
                    // showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat memuat detail Penanggung.");
                }
            });

            editBtn.setOnAction(event -> {
                UtangWithUserInfo utang = getTableView().getItems().get(getIndex());
                 // FIX: First, get the full Penanggung object from the database
                Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
                 if (penanggung != null) {
                    // Pass the fetched Penanggung object to the form
                    AddDebtForm form = new AddDebtForm(penanggung, tanggunganDAO, utangDAO, utang);
                    form.display();
                    refreshDebtTable();
                } else {
                    // showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat memuat detail Penanggung.");
                }
            });
        }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !"Penanggung".equals(loggedInUser.getRole())) {
                    setGraphic(null);
                } else {
                    UtangWithUserInfo utang = getTableView().getItems().get(getIndex());
                    payBtn.setDisable(utang.getSisaUtang() <= 0);
                    setGraphic(pane);
                }
            }
        });
        return actionCol;
    }

    // Helper methods
    private Button createNavLink(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(10, 16, 10, 16));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: rgba(255,255,255,0.15);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.15);", "")));
        return btn;
    }
    
    private VBox createSummaryCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        Label titleLabel = new Label(title);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        valueLabel.setTextFill(Color.web(color));
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private String formatRupiah(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(amount);
    }
    
    private TableCell<UtangWithUserInfo, Double> createRupiahCell(boolean isRemaining) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatRupiah(item));
                    if (isRemaining) {
                        setTextFill(item > 0 ? Color.web(RED) : Color.web(DARK_GREEN));
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        };
    }
}