package com.subwayit.gui;

import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;
import com.subwayit.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToggleButton;
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

    private DatePicker datePicker;
    private TextField amountField;
    private TextArea descriptionArea;
    private ToggleGroup categoryToggleGroup;
    private ToggleButton monthlyEarningBtn;
    private ToggleButton monthlySpendingBtn;
    private ToggleButton unexpectedEarningBtn;
    private ToggleButton unexpectedSpendingBtn;

    public AddTransactionForm(User user) {
        this.currentUser = user;
        this.transaksiDAO = new TransaksiDAO();
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Transaction");

        // --- Form Components ---

        // Date Input
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Date");

        // Amount Input
        amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        }));

        // Description Input
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        // Category Selection (Toggle Buttons based on image)
        categoryToggleGroup = new ToggleGroup();

        monthlyEarningBtn = new ToggleButton("Monthly Earning");
        monthlySpendingBtn = new ToggleButton("Monthly Spending");
        unexpectedEarningBtn = new ToggleButton("Unexpected Earning");
        unexpectedSpendingBtn = new ToggleButton("Unexpected Spending");

        // Assign toggle group
        monthlyEarningBtn.setToggleGroup(categoryToggleGroup);
        monthlySpendingBtn.setToggleGroup(categoryToggleGroup);
        unexpectedEarningBtn.setToggleGroup(categoryToggleGroup);
        unexpectedSpendingBtn.setToggleGroup(categoryToggleGroup);

        // Style for toggle buttons
        String toggleBtnStyle = "-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 12px; -fx-padding: 8 15; -fx-background-radius: 3;";
        String selectedToggleBtnStyle = "-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-font-weight: bold;";

        monthlyEarningBtn.setStyle(toggleBtnStyle);
        monthlySpendingBtn.setStyle(toggleBtnStyle);
        unexpectedEarningBtn.setStyle(toggleBtnStyle);
        unexpectedSpendingBtn.setStyle(toggleBtnStyle);

        // Listener to update styles when toggle selection changes
        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                ((ToggleButton) oldToggle).setStyle(toggleBtnStyle); // Cast to ToggleButton
            }
            if (newToggle != null) {
                ((ToggleButton) newToggle).setStyle(selectedToggleBtnStyle); // Cast to ToggleButton
            }
        });

        HBox categoryButtonsRow1 = new HBox(10, monthlyEarningBtn, monthlySpendingBtn);
        HBox categoryButtonsRow2 = new HBox(10, unexpectedEarningBtn, unexpectedSpendingBtn);
        VBox categorySelectionLayout = new VBox(10, categoryButtonsRow1, categoryButtonsRow2);

        // Default selection
        monthlyEarningBtn.setSelected(true);


        // Control Buttons
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
        submitButton.setOnAction(e -> handleSubmit());

        HBox buttonLayout = new HBox(15);
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);
        buttonLayout.getChildren().addAll(cancelButton, submitButton);

        // --- Form Layout ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        formGrid.add(new Label("Date:"), 0, 0);
        formGrid.add(datePicker, 1, 0);

        formGrid.add(new Label("Amount:"), 0, 1);
        formGrid.add(amountField, 1, 1);

        formGrid.add(new Label("Description:"), 0, 2);
        formGrid.add(descriptionArea, 1, 2);

        formGrid.add(new Label("Category:"), 0, 3);
        formGrid.add(categorySelectionLayout, 1, 3);

        // --- Main Dialog Layout ---
        VBox dialogLayout = new VBox(20);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setStyle("-fx-background-color: #FFFFFF;");
        dialogLayout.getChildren().addAll(formGrid, buttonLayout);

        Scene scene = new Scene(dialogLayout);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Handles the submission of the transaction form.
     */
    private void handleSubmit() {
        String userId = currentUser.getUserId();
        String transaksiId = UUID.randomUUID().toString();
        LocalDate tanggalTransaksi = datePicker.getValue();
        String description = descriptionArea.getText();
        String amountText = amountField.getText();

        if (tanggalTransaksi == null || amountText.isEmpty() || description.isEmpty() || categoryToggleGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields and select a category.");
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(amountText);
            if (nominal <= 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a valid number.");
            return;
        }

        String jenis = "";
        boolean isRutin = false;
        String kategori = "";
        ToggleButton selectedCategoryBtn = (ToggleButton) categoryToggleGroup.getSelectedToggle();

        if (selectedCategoryBtn == monthlyEarningBtn) {
            jenis = "Pemasukan";
            isRutin = true;
            kategori = "Gaji";
        } else if (selectedCategoryBtn == monthlySpendingBtn) {
            jenis = "Pengeluaran";
            isRutin = true;
            kategori = "Rumah Tangga";
        } else if (selectedCategoryBtn == unexpectedEarningBtn) {
            jenis = "Pemasukan";
            isRutin = false;
            kategori = "Tak Terduga";
        } else if (selectedCategoryBtn == unexpectedSpendingBtn) {
            jenis = "Pengeluaran";
            isRutin = false;
            kategori = "Tak Terduga";
        }

        Transaksi newTransaksi = new Transaksi(
            transaksiId,
            userId,
            jenis,
            kategori,
            nominal,
            tanggalTransaksi,
            "",
            isRutin,
            description
        );

        try {
            transaksiDAO.addTransaksi(newTransaksi);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully!");
            dialogStage.close();
            // TODO: Refresh dashboard table data if needed (already implemented in DashboardPage now)
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}