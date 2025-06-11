package com.subwayit.gui;

import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;
import com.subwayit.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.UUID;

public class AddTransactionForm {

    private Stage dialogStage;
    private User currentUser;
    private TransaksiDAO transaksiDAO;
    private Transaksi editingTransaksi;          // ← untuk mode edit

    private DatePicker datePicker;
    private TextField amountField;
    private TextArea descriptionArea;
    private ToggleGroup categoryToggleGroup;
    private ToggleButton monthlyEarningBtn;
    private ToggleButton monthlySpendingBtn;
    private ToggleButton unexpectedEarningBtn;
    private ToggleButton unexpectedSpendingBtn;

    /** Konstruktor untuk menambah transaksi baru */
    public AddTransactionForm(User user) {
        this.currentUser = user;
        this.transaksiDAO = new TransaksiDAO();
    }

    /** Konstruktor untuk edit: terima objek Transaksi yang akan diubah */
    public AddTransactionForm(User user, Transaksi transaksi) {
        this(user);
        this.editingTransaksi = transaksi;
    }

    /** Tampilkan form (modal) */
    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(editingTransaksi == null ? "Add New Transaction" : "Edit Transaction");

        // --- Komponen Form ---

        datePicker = new DatePicker();
        datePicker.setPromptText("Date");

        amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        categoryToggleGroup = new ToggleGroup();
        monthlyEarningBtn    = new ToggleButton("Monthly Earning");
        monthlySpendingBtn   = new ToggleButton("Monthly Spending");
        unexpectedEarningBtn = new ToggleButton("Unexpected Earning");
        unexpectedSpendingBtn= new ToggleButton("Unexpected Spending");

        monthlyEarningBtn.setToggleGroup(categoryToggleGroup);
        monthlySpendingBtn.setToggleGroup(categoryToggleGroup);
        unexpectedEarningBtn.setToggleGroup(categoryToggleGroup);
        unexpectedSpendingBtn.setToggleGroup(categoryToggleGroup);

        String baseStyle     = "-fx-background-color: #F0F0F0; -fx-text-fill: #333; -fx-font-size:12; -fx-padding:8 15; -fx-background-radius:3;";
        String selectedStyle = "-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-font-weight: bold;";

        monthlyEarningBtn.setStyle(baseStyle);
        monthlySpendingBtn.setStyle(baseStyle);
        unexpectedEarningBtn.setStyle(baseStyle);
        unexpectedSpendingBtn.setStyle(baseStyle);

        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (oldT != null) ((ToggleButton)oldT).setStyle(baseStyle);
            if (newT != null) ((ToggleButton)newT).setStyle(selectedStyle);
        });

        HBox row1 = new HBox(10, monthlyEarningBtn, monthlySpendingBtn);
        HBox row2 = new HBox(10, unexpectedEarningBtn, unexpectedSpendingBtn);
        VBox categoryBox = new VBox(10, row1, row2);
        categoryBox.setPadding(new Insets(5,0,5,0));

        // Jika edit, isi ulang field dari objek editingTransaksi
        if (editingTransaksi != null) {
            prefillForm(editingTransaksi);
        } else {
            // default selection bila tambah
            monthlyEarningBtn.setSelected(true);
            monthlyEarningBtn.setStyle(selectedStyle);
            datePicker.setValue(LocalDate.now());
        }

        // Tombol submit & cancel
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color:#F44336; -fx-text-fill:white; -fx-background-radius:5;");
        cancelBtn.setOnAction(e -> dialogStage.close());

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color:#4CAF50; -fx-text-fill:white; -fx-background-radius:5;");
        submitBtn.setOnAction(e -> handleSubmit());

        HBox btnBox = new HBox(15, cancelBtn, submitBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        // Layout grid
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        grid.add(new Label("Date:"),         0, 0);
        grid.add(datePicker,                 1, 0);
        grid.add(new Label("Amount:"),       0, 1);
        grid.add(amountField,                1, 1);
        grid.add(new Label("Description:"),  0, 2);
        grid.add(descriptionArea,            1, 2);
        grid.add(new Label("Category:"),     0, 3);
        grid.add(categoryBox,                1, 3);

        VBox layout = new VBox(20, grid, btnBox);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #FFF;");

        dialogStage.setScene(new Scene(layout));
        dialogStage.showAndWait();
    }

    /** Pre‐fill semua field dari objek Transaksi yang diedit */
    private void prefillForm(Transaksi t) {
        datePicker.setValue(t.getTanggalTransaksi());
        amountField.setText(String.valueOf(t.getNominal()));
        descriptionArea.setText(t.getDeskripsi());

        // Pilih toggle sesuai jenis & rutin
        boolean rutin = t.isRutin();
        if (t.getJenis().equalsIgnoreCase("Pemasukan") && rutin) {
            monthlyEarningBtn.setSelected(true);
            monthlyEarningBtn.setStyle("-fx-background-color: #8BC34A; -fx-text-fill:white;");
        }
        else if (t.getJenis().equalsIgnoreCase("Pengeluaran") && rutin) {
            monthlySpendingBtn.setSelected(true);
            monthlySpendingBtn.setStyle("-fx-background-color: #8BC34A; -fx-text-fill:white;");
        }
        else if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
            unexpectedEarningBtn.setSelected(true);
            unexpectedEarningBtn.setStyle("-fx-background-color: #8BC34A; -fx-text-fill:white;");
        }
        else {
            unexpectedSpendingBtn.setSelected(true);
            unexpectedSpendingBtn.setStyle("-fx-background-color: #8BC34A; -fx-text-fill:white;");
        }
    }

    /** Simpan data baru atau update bila edit */
    private void handleSubmit() {
        LocalDate date = datePicker.getValue();
        String desc = descriptionArea.getText().trim();
        String amtText = amountField.getText().trim();
        ToggleButton sel = (ToggleButton) categoryToggleGroup.getSelectedToggle();

        if (date == null || desc.isEmpty() || amtText.isEmpty() || sel == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please complete all fields & select a category.");
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(amtText);
            if (nominal <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a positive number.");
            return;
        }

        // Tentukan jenis, kategori, dan isRutin
        String jenis;
        boolean isRutin;
        String kategori;
        if (sel == monthlyEarningBtn) {
            jenis = "Pemasukan";    isRutin = true;  kategori = "Gaji";
        }
        else if (sel == monthlySpendingBtn) {
            jenis = "Pengeluaran";  isRutin = true;  kategori = "Rumah Tangga";
        }
        else if (sel == unexpectedEarningBtn) {
            jenis = "Pemasukan";    isRutin = false; kategori = "Tak Terduga";
        }
        else {
            jenis = "Pengeluaran";  isRutin = false; kategori = "Tak Terduga";
        }

        if (editingTransaksi == null) {
            // Mode ADD
            Transaksi t = new Transaksi(
                UUID.randomUUID().toString(),
                currentUser.getUserId(),
                jenis, kategori,
                nominal,
                date,
                "",
                isRutin,
                desc
            );
            transaksiDAO.addTransaksi(t);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully!");
        } else {
            // Mode EDIT: ubah field dan simpan
            editingTransaksi.setTanggalTransaksi(date);
            editingTransaksi.setDeskripsi(desc);
            editingTransaksi.setNominal(nominal);
            editingTransaksi.setJenis(jenis);
            editingTransaksi.setRutin(isRutin);
            editingTransaksi.setKategori(kategori);
            transaksiDAO.updateTransaksi(editingTransaksi);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction updated successfully!");
        }

        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
