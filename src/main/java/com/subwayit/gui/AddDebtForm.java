package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Utang;
import com.subwayit.model.Penanggung; // Debt is managed by Penanggung

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.UUID; // To generate unique IDs

public class AddDebtForm {

    private Stage dialogStage;
    private Penanggung currentPenanggung; // The Penanggung user managing the debt
    private UtangDAO utangDAO;

    // Form fields
    private TextField creditorField;
    private TextField amountField;
    private TextField bungaField; // For interest rate (optional)
    private DatePicker dueDateField;
    private TextField statusField; // For initial status

    public AddDebtForm(Penanggung penanggung) {
        this.currentPenanggung = penanggung;
        this.utangDAO = new UtangDAO();
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Debt");

        // --- Form Components ---
        creditorField = new TextField();
        creditorField.setPromptText("Creditor Name (e.g., Bank ABC)");

        amountField = new TextField();
        amountField.setPromptText("Amount (e.g., 500000.00)");
        amountField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        }));

        bungaField = new TextField();
        bungaField.setPromptText("Interest Rate (e.g., 0.05 for 5%)");
        bungaField.setText("0.0"); // Default to no interest for simplicity
        bungaField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        }));

        dueDateField = new DatePicker(LocalDate.now().plusMonths(1)); // Default to one month from now
        dueDateField.setPromptText("Due Date");

        statusField = new TextField(); // For initial status like "Belum Lunas"
        statusField.setPromptText("Initial Status (e.g., Belum Lunas)");
        statusField.setText("Belum Lunas"); // Default status

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button submitButton = new Button("Add Debt");
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

        formGrid.add(new Label("Creditor Name:"), 0, 0);
        formGrid.add(creditorField, 1, 0);

        formGrid.add(new Label("Amount:"), 0, 1);
        formGrid.add(amountField, 1, 1);

        formGrid.add(new Label("Interest Rate:"), 0, 2);
        formGrid.add(bungaField, 1, 2);

        formGrid.add(new Label("Due Date:"), 0, 3);
        formGrid.add(dueDateField, 1, 3);

        formGrid.add(new Label("Initial Status:"), 0, 4);
        formGrid.add(statusField, 1, 4);

        VBox dialogLayout = new VBox(20);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setStyle("-fx-background-color: #FFFFFF;");
        dialogLayout.getChildren().addAll(formGrid, buttonLayout);

        Scene scene = new Scene(dialogLayout);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void handleSubmit() {
        String creditor = creditorField.getText();
        String amountText = amountField.getText();
        String bungaText = bungaField.getText();
        LocalDate dueDate = dueDateField.getValue();
        String status = statusField.getText();

        if (creditor.isEmpty() || amountText.isEmpty() || bungaText.isEmpty() || dueDate == null || status.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        double amount;
        double bunga;
        try {
            amount = Double.parseDouble(amountText);
            bunga = Double.parseDouble(bungaText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Amount and Interest Rate must be valid numbers.");
            return;
        }

        String utangId = UUID.randomUUID().toString();
        String penanggungId = currentPenanggung.getUserId(); // The logged-in Penanggung's ID

        Utang newUtang = new Utang(utangId, penanggungId, amount, bunga, dueDate, status, creditor);

        try {
            utangDAO.addUtang(newUtang);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Debt added successfully!");
            dialogStage.close();
            // TODO: Refresh debt table in DebtPage (this is handled by refreshDebtTable() call in DebtPage)
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add debt: " + e.getMessage());
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