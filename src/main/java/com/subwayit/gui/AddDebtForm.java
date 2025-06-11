package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Utang;
import com.subwayit.model.Tanggungan; // Debt is managed by Tanggungan

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
import java.util.UUID; // To generate unique IDs

public class AddDebtForm {

    private Stage dialogStage;
    private Tanggungan currentTanggungan; // The Tanggungan user managing the debt
    private UtangDAO utangDAO;
    private Utang editingUtang; // For future edit functionality

    // Form fields
    private TextField creditorField;
    private TextField amountField;
    private TextField bungaField; // For interest rate (optional)
    private DatePicker dueDateField;
    private ComboBox<String> statusComboBox; // Changed to ComboBox for better UX
    private ComboBox<String> debtTypeComboBox; // New field for debt type

    // Theme colors (same as AddTransactionForm)
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    /** Constructor for adding new debt */
    public AddDebtForm(Tanggungan tanggungan) {
        this.currentTanggungan = tanggungan;
        this.utangDAO = new UtangDAO();
    }

    /** Constructor for editing debt (future functionality) */
    public AddDebtForm(Tanggungan tanggungan, Utang utang) {
        this(tanggungan);
        this.editingUtang = utang;
    }

    /** Display form (modal) */
    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(editingUtang == null ? "Add New Debt" : "Edit Debt");

        // Create modern form scene
        Scene scene = createModernFormScene();
        dialogStage.setScene(scene);
        dialogStage.centerOnScreen();
        dialogStage.showAndWait();
    }

    private Scene createModernFormScene() {
        // Main container with modern styling
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, " + LIGHT_GREEN + " 100%);");

        // Form card container
        VBox formCard = new VBox(25);
        formCard.setAlignment(Pos.TOP_CENTER);
        formCard.setPadding(new Insets(30, 40, 30, 40));
        formCard.setMaxWidth(500);
        formCard.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 16; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); " +
                         "-fx-border-color: #E2E8F0; " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 16;");

        // Header section
        VBox headerSection = createHeaderSection();
        
        // Form content
        VBox formContent = createFormContent();
        
        // Button section
        HBox buttonSection = createButtonSection();

        formCard.getChildren().addAll(headerSection, formContent, buttonSection);
        root.getChildren().add(formCard);

        return new Scene(root, 600, 750);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        // Title with icon
        String titleText = editingUtang == null ? "Add New Debt 💳" : "Edit Debt ✏️";
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(TEXT_DARK));

        // Subtitle
        String subtitleText = editingUtang == null ? 
            "Record a new financial obligation" : 
            "Update your debt details";
        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(TEXT_GRAY));

        // Decorative line
        Rectangle decorativeLine = new Rectangle(60, 3);
        decorativeLine.setFill(Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel, decorativeLine);
        return headerSection;
    }

    private VBox createFormContent() {
        VBox formContent = new VBox(20);

        // Creditor field
        VBox creditorSection = createFieldSection("🏦 Creditor Name", createModernCreditorField());
        
        // Debt type field (new)
        VBox debtTypeSection = createFieldSection("🏷️ Debt Type", createDebtTypeComboBox());
        
        // Amount field
        VBox amountSection = createFieldSection("💰 Debt Amount", createModernAmountField());
        
        // Interest rate field
        VBox interestSection = createFieldSection("📈 Interest Rate (%)", createModernInterestField());
        
        // Due date field
        VBox dueDateSection = createFieldSection("📅 Due Date", createModernDatePicker());
        
        // Status field
        VBox statusSection = createFieldSection("📊 Initial Status", createStatusComboBox());

        formContent.getChildren().addAll(creditorSection, debtTypeSection, amountSection, 
                                       interestSection, dueDateSection, statusSection);

        // Pre-fill form if editing
        if (editingUtang != null) {
            prefillForm(editingUtang);
        } else {
            // Default values for new debt
            dueDateField.setValue(LocalDate.now().plusMonths(1));
            statusComboBox.setValue("Belum Lunas");
            debtTypeComboBox.setValue("Personal Loan");
            bungaField.setText("0.0");
        }

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

    private TextField createModernCreditorField() {
        creditorField = new TextField();
        creditorField.setPromptText("Enter creditor name (e.g., Bank ABC, Credit Card)");
        creditorField.setPrefWidth(420);
        creditorField.setPrefHeight(45);
        creditorField.setFont(Font.font("Segoe UI", 14));
        creditorField.setStyle("-fx-background-color: white; " +
                              "-fx-border-color: #E2E8F0; " +
                              "-fx-border-width: 2; " +
                              "-fx-border-radius: 8; " +
                              "-fx-background-radius: 8; " +
                              "-fx-padding: 12;");

        // Focus styling
        creditorField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                creditorField.setStyle(creditorField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                creditorField.setStyle(creditorField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return creditorField;
    }

    private ComboBox<String> createDebtTypeComboBox() {
        debtTypeComboBox = new ComboBox<>();
        debtTypeComboBox.getItems().addAll(
            "Personal Loan", "Credit Card", "Mortgage", "Car Loan", 
            "Student Loan", "Business Loan", "Medical Bill", "Other"
        );
        debtTypeComboBox.setPromptText("Select debt type");
        debtTypeComboBox.setPrefWidth(420);
        debtTypeComboBox.setPrefHeight(45);
        debtTypeComboBox.setStyle("-fx-background-color: white; " +
                                 "-fx-border-color: #E2E8F0; " +
                                 "-fx-border-width: 2; " +
                                 "-fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-font-family: 'Segoe UI'; " +
                                 "-fx-font-size: 14px;");

        // Focus styling
        debtTypeComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                debtTypeComboBox.setStyle(debtTypeComboBox.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                debtTypeComboBox.setStyle(debtTypeComboBox.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return debtTypeComboBox;
    }

    private TextField createModernAmountField() {
        amountField = new TextField();
        amountField.setPromptText("Enter debt amount (e.g., 5000000)");
        amountField.setPrefWidth(420);
        amountField.setPrefHeight(45);
        amountField.setFont(Font.font("Segoe UI", 14));
        amountField.setStyle("-fx-background-color: white; " +
                            "-fx-border-color: #E2E8F0; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-padding: 12;");

        // Numeric validation
        amountField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

        // Focus styling
        amountField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                amountField.setStyle(amountField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                amountField.setStyle(amountField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return amountField;
    }

    private TextField createModernInterestField() {
        bungaField = new TextField();
        bungaField.setPromptText("Enter interest rate (e.g., 12.5 for 12.5%)");
        bungaField.setPrefWidth(420);
        bungaField.setPrefHeight(45);
        bungaField.setFont(Font.font("Segoe UI", 14));
        bungaField.setStyle("-fx-background-color: white; " +
                           "-fx-border-color: #E2E8F0; " +
                           "-fx-border-width: 2; " +
                           "-fx-border-radius: 8; " +
                           "-fx-background-radius: 8; " +
                           "-fx-padding: 12;");

        // Numeric validation
        bungaField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

        // Focus styling
        bungaField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                bungaField.setStyle(bungaField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                bungaField.setStyle(bungaField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return bungaField;
    }

    private DatePicker createModernDatePicker() {
        dueDateField = new DatePicker();
        dueDateField.setPromptText("Select due date");
        dueDateField.setPrefWidth(420);
        dueDateField.setPrefHeight(45);
        dueDateField.setStyle("-fx-background-color: white; " +
                             "-fx-border-color: #E2E8F0; " +
                             "-fx-border-width: 2; " +
                             "-fx-border-radius: 8; " +
                             "-fx-background-radius: 8; " +
                             "-fx-font-family: 'Segoe UI'; " +
                             "-fx-font-size: 14px;");

        // Focus styling
        dueDateField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                dueDateField.setStyle(dueDateField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                dueDateField.setStyle(dueDateField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return dueDateField;
    }

    private ComboBox<String> createStatusComboBox() {
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Belum Lunas", "Sebagian Lunas", "Lunas", "Overdue");
        statusComboBox.setPromptText("Select initial status");
        statusComboBox.setPrefWidth(420);
        statusComboBox.setPrefHeight(45);
        statusComboBox.setStyle("-fx-background-color: white; " +
                               "-fx-border-color: #E2E8F0; " +
                               "-fx-border-width: 2; " +
                               "-fx-border-radius: 8; " +
                               "-fx-background-radius: 8; " +
                               "-fx-font-family: 'Segoe UI'; " +
                               "-fx-font-size: 14px;");

        // Focus styling
        statusComboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                statusComboBox.setStyle(statusComboBox.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                statusComboBox.setStyle(statusComboBox.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return statusComboBox;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(15);
        buttonSection.setAlignment(Pos.CENTER);

        // Cancel button
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

        // Cancel button hover
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelBtn.getStyle() + 
            "-fx-border-color: " + RED + "; -fx-text-fill: " + RED + ";"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelBtn.getStyle()
            .replace("-fx-border-color: " + RED + ";", "-fx-border-color: #E2E8F0;")
            .replace("-fx-text-fill: " + RED + ";", "-fx-text-fill: " + TEXT_DARK + ";")));
        cancelBtn.setOnAction(e -> dialogStage.close());

        // Submit button
        String submitText = editingUtang == null ? "💳 Add Debt" : "💾 Update Debt";
        Button submitBtn = new Button(submitText);
        submitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        submitBtn.setPrefWidth(150);
        submitBtn.setPrefHeight(45);
        submitBtn.setTextFill(Color.WHITE);
        submitBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        // Submit button hover
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(submitBtn.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle(submitBtn.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        submitBtn.setOnAction(e -> handleSubmit());

        buttonSection.getChildren().addAll(cancelBtn, submitBtn);
        return buttonSection;
    }

    /** Pre-fill all fields from the Utang object being edited */
    private void prefillForm(Utang utang) {
        creditorField.setText(utang.getCreditor());
        amountField.setText(String.valueOf(utang.getJumlah()));
        bungaField.setText(String.valueOf(utang.getBunga()));
        dueDateField.setValue(utang.getTanggalJatuhTempo());
        statusComboBox.setValue(utang.getStatus());
        // debtTypeComboBox would need to be derived from existing data or set to default
        debtTypeComboBox.setValue("Personal Loan");
    }

    /** Save new data or update if editing */
    private void handleSubmit() {
        String creditor = creditorField.getText().trim();
        String amountText = amountField.getText().trim();
        String bungaText = bungaField.getText().trim();
        LocalDate dueDate = dueDateField.getValue();
        String status = statusComboBox.getValue();
        String debtType = debtTypeComboBox.getValue();

        if (creditor.isEmpty() || amountText.isEmpty() || bungaText.isEmpty() || 
            dueDate == null || status == null || debtType == null) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Please complete all fields.");
            return;
        }

        double amount;
        double bunga;
        try {   
            amount = Double.parseDouble(amountText);
            bunga = Double.parseDouble(bungaText);
            if (amount <= 0) {
                showModernAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a positive number.");
                return;
            }
            if (bunga < 0) {
                showModernAlert(Alert.AlertType.ERROR, "Input Error", "Interest rate cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Amount and Interest Rate must be valid numbers.");
            return;
        }

        if (editingUtang == null) {
            // ADD mode
            String utangId = UUID.randomUUID().toString();
            String userId = currentTanggungan.getUserId();

            // Convert percentage to decimal for storage
            double bungaDecimal = bunga / 100.0;

            Utang newUtang = new Utang(utangId, userId, amount, bungaDecimal, dueDate, status, creditor);

            try {
                utangDAO.addUtang(newUtang);
                showModernAlert(Alert.AlertType.INFORMATION, "Success", "🎉 Debt added successfully!");
                dialogStage.close();
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add debt: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // EDIT mode: modify fields and save
            editingUtang.setCreditor(creditor);
            editingUtang.setJumlah(amount);
            editingUtang.setBunga(bunga / 100.0); // Convert to decimal
            editingUtang.setTanggalJatuhTempo(dueDate);
            editingUtang.setStatus(status);
            
            try {
                // Assuming you have updateUtang method in UtangDAO
                // utangDAO.updateUtang(editingUtang);
                showModernAlert(Alert.AlertType.INFORMATION, "Success", "💾 Debt updated successfully!");
                dialogStage.close();
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update debt: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showModernAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        
        alert.showAndWait();
    }
}