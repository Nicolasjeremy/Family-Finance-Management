package com.subwayit.gui;

import com.subwayit.model.User;
import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;

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
import javafx.stage.Stage;

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
        root.setStyle("-fx-background-color: #F0F0F0;");

        return new Scene(root, 1000, 700);
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

        // Buttons...
        Button homeBtn = createNavLink("Home");
        homeBtn.setOnAction(e -> primaryStage.setScene(createScene()));
        Button dashboardsBtn = createNavLink("Dashboards");
        dashboardsBtn.setOnAction(e -> primaryStage.setScene(createScene()));
        Button membersBtn = createNavLink("Members");
        membersBtn.setOnAction(e -> {
            MembersPage mp = new MembersPage(primaryStage, loggedInUser);
            primaryStage.setScene(mp.createScene());
        });
        Button debtBtn = createNavLink("Debt");
        // TODO: handler Debt

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

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label hi = new Label("Hi " + loggedInUser.getNama());
        hi.setFont(Font.font(28));
        hi.setTextFill(Color.web("#333333"));
        Label desc = new Label("This is the transaction from the past 30 days");
        desc.setFont(Font.font(14));
        desc.setTextFill(Color.GRAY);
        VBox vb = new VBox(5, hi, desc);
        Region space = new Region(); HBox.setHgrow(space, Priority.ALWAYS);

        Button viewR = new Button("View reports");
        viewR.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white;"); viewR.setPadding(new Insets(10));
        Button addT = new Button("Add transaction");
        addT.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white;"); addT.setPadding(new Insets(10));
        addT.setOnAction(e -> {
            // **Edit** form must accept Transaksi for editing
            AddTransactionForm form = new AddTransactionForm(loggedInUser);
            form.display(); // on save, calls addTransaksi or updateTransaksi
            refreshTransactionTable();
            updateFinancialSummary();
        });

        header.getChildren().addAll(vb, space, viewR, addT);

        // Summary cards
        HBox summary = new HBox(20);
        summary.getChildren().addAll(
            createSummaryCard("Total transaction", totalTransactionValue),
            createSummaryCard("This month spending", thisMonthSpendingValue),
            createSummaryCard("This month earning", thisMonthEarningValue),
            createSummaryCard("Cashflow", cashflowValue)
        );

        // Table with Actions column
        transactionTable = createTransactionTable();
        refreshTransactionTable();
        updateFinancialSummary();

        content.getChildren().addAll(header, summary, new Label("Transaction List"), transactionTable);
        return content;
    }

    private VBox createSummaryCard(String title, Label value) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color:white; -fx-background-radius:5; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),10,0,0,0);");
        card.setPrefWidth(220);
        Label t = new Label(title); t.setFont(Font.font(12)); t.setTextFill(Color.GRAY);
        value.setFont(Font.font(24)); value.setTextFill(Color.web("#333333"));
        card.getChildren().addAll(t, value);
        return card;
    }

    private TableView<Transaksi> createTransactionTable() {
        TableView<Transaksi> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<Transaksi, String> idDateCol = new TableColumn<>("ID/Date");
        idDateCol.setCellValueFactory(new PropertyValueFactory<>("transaksiIdAndDate"));
        idDateCol.setPrefWidth(120);

        TableColumn<Transaksi, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        descCol.setPrefWidth(200);

        TableColumn<Transaksi, String> payeeCol = new TableColumn<>("Payee/from");
        payeeCol.setCellValueFactory(new PropertyValueFactory<>("payeeFrom"));
        payeeCol.setPrefWidth(130);

        TableColumn<Transaksi, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        catCol.setPrefWidth(120);

        TableColumn<Transaksi, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        amtCol.setPrefWidth(100);
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val==null) {
                    setText(null);
                } else {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    setText("Rp " + formatRupiah(val));
                    setTextFill(t.getJenis().equalsIgnoreCase("Pemasukan") ? Color.GREEN : Color.RED);
                }
            }
        });

        // Actions column
        TableColumn<Transaksi, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn  = new Button("Delete");
            {
                editBtn.setOnAction(e -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    // Open form in edit mode:
                    AddTransactionForm form = new AddTransactionForm(loggedInUser, t);
                    form.display();
                    refreshTransactionTable();
                    updateFinancialSummary();
                });
                delBtn.setOnAction(e -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    transaksiDAO.deleteTransaksi(t.getTransaksiId());
                    refreshTransactionTable();
                    updateFinancialSummary();
                });
                editBtn.setStyle("-fx-background-color:#2196F3;-fx-text-fill:white;");
                delBtn.setStyle("-fx-background-color:#F44336;-fx-text-fill:white;");
            }
            private final HBox pane = new HBox(5, editBtn, delBtn);
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idDateCol, descCol, payeeCol, catCol, amtCol, actionCol);
        return table;
    }

    private void refreshTransactionTable() {
        if (loggedInUser!=null) {
            List<Transaksi> lst = transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId());
            transactionTable.setItems(FXCollections.observableArrayList(lst));
        }
    }

    private void updateFinancialSummary() {
        List<Transaksi> lst = transaksiDAO.getAllTransactionsForUser(loggedInUser.getUserId());
        int total = lst.size();
        double spend=0, earn=0;
        LocalDate now = LocalDate.now();
        for (Transaksi t : lst) {
            if (t.getTanggalTransaksi().getMonth()==now.getMonth() && t.getTanggalTransaksi().getYear()==now.getYear()) {
                if (t.getJenis().equalsIgnoreCase("Pemasukan")) earn += t.getNominal();
                else spend += t.getNominal();
            }
        }
        totalTransactionValue.setText(String.valueOf(total));
        thisMonthSpendingValue.setText("Rp " + formatRupiah(spend));
        thisMonthEarningValue.setText("Rp " + formatRupiah(earn));
        double cf = earn - spend;
        cashflowValue.setText("Rp " + formatRupiah(cf));
        cashflowValue.setTextFill(cf>=0 ? Color.GREEN : Color.RED);
    }

    private String formatRupiah(double amt) {
        return String.format("%,.0f", amt).replace(',','.');
    }
}
