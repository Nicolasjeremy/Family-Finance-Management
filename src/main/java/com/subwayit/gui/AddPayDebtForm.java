package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Utang;
import com.subwayit.model.Penanggung;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;

public class AddPayDebtForm {

    private Stage dialogStage;
    private Penanggung currentPenanggung;
    private UtangDAO utangDAO;
    private Utang debtToPay;

    // Form fields
    private Label debtInfoLabel;
    private Label remainingDebtLabel;
    private Label estimatedMonthlyLabel;
    private TextField paymentAmountField;
    private TextArea notesArea;
    private ComboBox<String> paymentTypeComboBox;

    // Theme colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    public AddPayDebtForm(Penanggung penanggung, Utang utang) {
        this.currentPenanggung = penanggung;
        this.debtToPay = utang;
        this.utangDAO = new UtangDAO();
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Pay Debt - " + debtToPay.getCreditor());

        Scene scene = createModernFormScene();
        dialogStage.setScene(scene);
        dialogStage.centerOnScreen();
        dialogStage.showAndWait();
    }

    private Scene createModernFormScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, " + LIGHT_GREEN + " 100%);");

        VBox formCard = new VBox(25);
        formCard.setAlignment(Pos.TOP_CENTER);
        formCard.setPadding(new Insets(30, 40, 30, 40));
        formCard.setMaxWidth(550);
        formCard.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 16; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); " +
                         "-fx-border-color: #E2E8F0; " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 16;");

        VBox headerSection = createHeaderSection();
        VBox debtInfoSection = createDebtInfoSection();
        VBox formContent = createFormContent();
        HBox buttonSection = createButtonSection();

        formCard.getChildren().addAll(headerSection, debtInfoSection, formContent, buttonSection);
        root.getChildren().add(formCard);

        return new Scene(root, 650, 800);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("💳 Pay Debt");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(TEXT_DARK));

        Label subtitleLabel = new Label("Make a payment towards your debt obligation");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(TEXT_GRAY));

        Rectangle decorativeLine = new Rectangle(60, 3);
        decorativeLine.setFill(Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel, decorativeLine);
        return headerSection;
    }

    private VBox createDebtInfoSection() {
        VBox infoSection = new VBox(15);
        infoSection.setStyle("-fx-background-color: " + LIGHT_GREEN + "; " +
                            "-fx-background-radius: 12; " +
                            "-fx-padding: 20;");

        Label infoTitle = new Label("💰 Debt Information");
        infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        infoTitle.setTextFill(Color.web(TEXT_DARK));

        debtInfoLabel = new Label("Creditor: " + debtToPay.getCreditor() + "\nOriginal Amount: Rp " + 
                                 formatRupiah(debtToPay.getJumlah()));
        debtInfoLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        debtInfoLabel.setTextFill(Color.web(TEXT_DARK));

        remainingDebtLabel = new Label("Remaining Debt: Rp " + formatRupiah(debtToPay.getSisaUtang()));
        remainingDebtLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        remainingDebtLabel.setTextFill(Color.web(RED));

        estimatedMonthlyLabel = new Label("Estimated Monthly Payment: " + debtToPay.getFormattedEstimasiBulanan());
        estimatedMonthlyLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        estimatedMonthlyLabel.setTextFill(Color.web(DARK_GREEN));

        Label dueDateLabel = new Label("Due Date: " + debtToPay.getFormattedDueDate());
        dueDateLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        dueDateLabel.setTextFill(Color.web(TEXT_GRAY));

        infoSection.getChildren().addAll(infoTitle, debtInfoLabel, remainingDebtLabel, estimatedMonthlyLabel, dueDateLabel);
        return infoSection;
    }

    private VBox createFormContent() {
        VBox formContent = new VBox(20);

        // Payment type
        VBox paymentTypeSection = createFieldSection("🏷️ Payment Type", createPaymentTypeComboBox());
        
        // Payment amount
        VBox amountSection = createFieldSection("💰 Payment Amount", createPaymentAmountField());
        
        // Notes
        VBox notesSection = createFieldSection("📝 Payment Notes", createNotesArea());

        formContent.getChildren().addAll(paymentTypeSection, amountSection, notesSection);

        // Set default values
        paymentTypeComboBox.setValue("Monthly Payment");
        paymentAmountField.setText(String.format("%.0f", debtToPay.getEstimasiBiayaBulanan()));

        return formContent;
    }

    private VBox createFieldSection(String labelText, Control field) {
        VBox section = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web(TEXT_DARK));
        
        section.getChildren().addAll(label, field);
        return section;
    }

    private ComboBox<String> createPaymentTypeComboBox() {
        paymentTypeComboBox = new ComboBox<>();
        paymentTypeComboBox.getItems().addAll(
            "Monthly Payment", "Partial Payment", "Full Payment", "Extra Payment"
        );
        paymentTypeComboBox.setPromptText("Select payment type");
        paymentTypeComboBox.setPrefWidth(470);
        paymentTypeComboBox.setPrefHeight(45);
        paymentTypeComboBox.setStyle("-fx-background-color: white; " +
                                     "-fx-border-color: #E2E8F0; " +
                                     "-fx-border-width: 2; " +
                                     "-fx-border-radius: 8; " +
                                     "-fx-background-radius: 8; " +
                                     "-fx-font-family: 'Segoe UI'; " +
                                     "-fx-font-size: 14px;");

        // Update amount when payment type changes
        paymentTypeComboBox.setOnAction(e -> {
            String selectedType = paymentTypeComboBox.getValue();
            if ("Monthly Payment".equals(selectedType)) {
                paymentAmountField.setText(String.format("%.0f", debtToPay.getEstimasiBiayaBulanan()));
            } else if ("Full Payment".equals(selectedType)) {
                paymentAmountField.setText(String.format("%.0f", debtToPay.getSisaUtang()));
            } else {
                paymentAmountField.clear();
            }
        });

        return paymentTypeComboBox;
    }

    private TextField createPaymentAmountField() {
        paymentAmountField = new TextField();
        paymentAmountField.setPromptText("Enter payment amount");
        paymentAmountField.setPrefWidth(470);
        paymentAmountField.setPrefHeight(45);
        paymentAmountField.setFont(Font.font("Segoe UI", 14));
        paymentAmountField.setStyle("-fx-background-color: white; " +
                                   "-fx-border-color: #E2E8F0; " +
                                   "-fx-border-width: 2; " +
                                   "-fx-border-radius: 8; " +
                                   "-fx-background-radius: 8; " +
                                   "-fx-padding: 12;");

        // Numeric validation
        paymentAmountField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

        // Focus styling
        paymentAmountField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                paymentAmountField.setStyle(paymentAmountField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                paymentAmountField.setStyle(paymentAmountField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return paymentAmountField;
    }

    private TextArea createNotesArea() {
        notesArea = new TextArea();
        notesArea.setPromptText("Add payment notes (optional)");
        notesArea.setPrefWidth(470);
        notesArea.setPrefHeight(80);
        notesArea.setFont(Font.font("Segoe UI", 14));
        notesArea.setStyle("-fx-background-color: white; " +
                          "-fx-border-color: #E2E8F0; " +
                          "-fx-border-width: 2; " +
                          "-fx-border-radius: 8; " +
                          "-fx-background-radius: 8; " +
                          "-fx-padding: 12;");

        // Focus styling
        notesArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                notesArea.setStyle(notesArea.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                notesArea.setStyle(notesArea.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return notesArea;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(15);
        buttonSection.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        cancelBtn.setPrefWidth(150);
        cancelBtn.setPrefHeight(45);
        cancelBtn.setTextFill(Color.web(TEXT_DARK));
        cancelBtn.setStyle("-fx-background-color: white; " +
                          "-fx-border-color: #E2E8F0; " +
                          "-fx-border-width: 2; " +
                          "-fx-background-radius: 8; " +
                          "-fx-border-radius: 8; " +
                          "-fx-cursor: hand;");

        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelBtn.getStyle() + 
            "-fx-border-color: " + RED + "; -fx-text-fill: " + RED + ";"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelBtn.getStyle()
            .replace("-fx-border-color: " + RED + ";", "-fx-border-color: #E2E8F0;")
            .replace("-fx-text-fill: " + RED + ";", "-fx-text-fill: " + TEXT_DARK + ";")));
        cancelBtn.setOnAction(e -> dialogStage.close());

        Button payBtn = new Button("💳 Process Payment");
        payBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        payBtn.setPrefWidth(180);
        payBtn.setPrefHeight(45);
        payBtn.setTextFill(Color.WHITE);
        payBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                       "-fx-background-radius: 8; " +
                       "-fx-cursor: hand; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        payBtn.setOnMouseEntered(e -> payBtn.setStyle(payBtn.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        payBtn.setOnMouseExited(e -> payBtn.setStyle(payBtn.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        payBtn.setOnAction(e -> handlePayment());

        buttonSection.getChildren().addAll(cancelBtn, payBtn);
        return buttonSection;
    }

    private void handlePayment() {
        String paymentType = paymentTypeComboBox.getValue();
        String amountText = paymentAmountField.getText().trim();
        String notes = notesArea.getText().trim();

        if (paymentType == null || amountText.isEmpty()) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in payment type and amount.");
            return;
        }

        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(amountText);
            if (paymentAmount <= 0) {
                showModernAlert(Alert.AlertType.ERROR, "Input Error", "Payment amount must be positive.");
                return;
            }
            if (paymentAmount > debtToPay.getSisaUtang()) {
                showModernAlert(Alert.AlertType.ERROR, "Input Error", 
                    "Payment amount cannot exceed remaining debt (Rp " + formatRupiah(debtToPay.getSisaUtang()) + ").");
                return;
            }
        } catch (NumberFormatException e) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Payment amount must be a valid number.");
            return;
        }

        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Payment");
        confirmAlert.setHeaderText("Payment Confirmation");
        confirmAlert.setContentText("Process payment of Rp " + formatRupiah(paymentAmount) + 
                                   " for debt to " + debtToPay.getCreditor() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                String finalNotes = paymentType + (notes.isEmpty() ? "" : " - " + notes);
                boolean success = utangDAO.updatePayment(debtToPay.getUtangId(), paymentAmount, finalNotes);
                
                if (success) {
                    showModernAlert(Alert.AlertType.INFORMATION, "Success", 
                        "🎉 Payment processed successfully!\nAmount: Rp " + formatRupiah(paymentAmount));
                    dialogStage.close();
                } else {
                    showModernAlert(Alert.AlertType.ERROR, "Payment Failed", "Failed to process payment. Please try again.");
                }
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Error processing payment: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String formatRupiah(double amt) {
        return String.format("%,.0f", amt).replace(',', '.');
    }

    private void showModernAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
    }
}