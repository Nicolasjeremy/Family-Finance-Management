package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Utang;
import com.subwayit.model.Tanggungan;

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
import java.util.UUID;

public class AddDebtForm {

    private Stage dialogStage;
    private Tanggungan currentTanggungan;
    private UtangDAO utangDAO;
    private Utang editingUtang; // For edit functionality
    private boolean isEditMode = false;

    // Form fields
    private TextField creditorField;
    private TextField amountField;
    private TextField bungaField;
    private DatePicker dueDateField;
    private ComboBox<String> statusComboBox;
    private ComboBox<String> debtTypeComboBox;

    // Theme colors
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
        this.isEditMode = false;
    }

    /** Constructor for editing debt */
    public AddDebtForm(Tanggungan tanggungan, Utang utang) {
        this.currentTanggungan = tanggungan;
        this.utangDAO = new UtangDAO();
        this.editingUtang = utang;
        this.isEditMode = true;
    }

    /** Display form (modal) */
    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(isEditMode ? "Edit Debt - " + editingUtang.getCreditor() : "Add New Debt");

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
        formCard.setMaxWidth(500);
        formCard.setStyle("-fx-background-color: white; " +
                         "-fx-background-radius: 16; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); " +
                         "-fx-border-color: #E2E8F0; " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 16;");

        VBox headerSection = createHeaderSection();
        VBox formContent = createFormContent();
        HBox buttonSection = createButtonSection();

        formCard.getChildren().addAll(headerSection, formContent, buttonSection);
        root.getChildren().add(formCard);

        return new Scene(root, 600, 750);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        String titleText = isEditMode ? "Edit Debt ✏️" : "Add New Debt 💳";
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(TEXT_DARK));

        String subtitleText = isEditMode ? 
            "Update your debt details" : 
            "Record a new financial obligation";
        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(TEXT_GRAY));

        Rectangle decorativeLine = new Rectangle(60, 3);
        decorativeLine.setFill(Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel, decorativeLine);
        return headerSection;
    }

    private VBox createFormContent() {
        VBox formContent = new VBox(20);

        VBox creditorSection = createFieldSection("🏦 Creditor Name", createModernCreditorField());
        VBox debtTypeSection = createFieldSection("🏷️ Debt Type", createDebtTypeComboBox());
        VBox amountSection = createFieldSection("💰 Debt Amount", createModernAmountField());
        VBox interestSection = createFieldSection("📈 Interest Rate (%)", createModernInterestField());
        VBox dueDateSection = createFieldSection("📅 Due Date", createModernDatePicker());
        VBox statusSection = createFieldSection("📊 Status", createStatusComboBox());

        formContent.getChildren().addAll(creditorSection, debtTypeSection, amountSection, 
                                       interestSection, dueDateSection, statusSection);

        if (isEditMode && editingUtang != null) {
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

        amountField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

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

        bungaField.setTextFormatter(new TextFormatter<>(c ->
            c.getText().matches("[0-9]*\\.?[0-9]*") ? c : null
        ));

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
        statusComboBox.setPromptText("Select status");
        statusComboBox.setPrefWidth(420);
        statusComboBox.setPrefHeight(45);
        statusComboBox.setStyle("-fx-background-color: white; " +
                               "-fx-border-color: #E2E8F0; " +
                               "-fx-border-width: 2; " +
                               "-fx-border-radius: 8; " +
                               "-fx-background-radius: 8; " +
                               "-fx-font-family: 'Segoe UI'; " +
                               "-fx-font-size: 14px;");

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

        Button cancelBtn = new Button("❌ Cancel");
        cancelBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        cancelBtn.setPrefWidth(120);
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

        String submitText = isEditMode ? "💾 Update Debt" : "💳 Add Debt";
        Button submitBtn = new Button(submitText);
        submitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        submitBtn.setPrefWidth(150);
        submitBtn.setPrefHeight(45);
        submitBtn.setTextFill(Color.WHITE);
        submitBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(submitBtn.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle(submitBtn.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        submitBtn.setOnAction(e -> handleSubmit());

        // Add delete button for edit mode
        if (isEditMode) {
            Button deleteBtn = new Button("🗑️ Delete");
            deleteBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
            deleteBtn.setPrefWidth(120);
            deleteBtn.setPrefHeight(45);
            deleteBtn.setTextFill(Color.WHITE);
            deleteBtn.setStyle("-fx-background-color: " + RED + "; " +
                              "-fx-background-radius: 8; " +
                              "-fx-cursor: hand; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);");

            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace(RED, "#C53030")));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(deleteBtn.getStyle().replace("#C53030", RED)));
            deleteBtn.setOnAction(e -> handleDelete());

            buttonSection.getChildren().addAll(deleteBtn, cancelBtn, submitBtn);
        } else {
            buttonSection.getChildren().addAll(cancelBtn, submitBtn);
        }

        return buttonSection;
    }

    private void prefillForm(Utang utang) {
        creditorField.setText(utang.getCreditor());
        amountField.setText(String.valueOf(utang.getJumlah()));
        bungaField.setText(String.valueOf(utang.getBunga() * 100)); // Convert to percentage
        dueDateField.setValue(utang.getTanggalJatuhTempo());
        statusComboBox.setValue(utang.getStatus());
        debtTypeComboBox.setValue("Personal Loan"); // Default, as we don't store debt type
    }

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

        double bungaDecimal = bunga / 100.0; // Convert percentage to decimal

        if (isEditMode) {
            // UPDATE mode
            editingUtang.setCreditor(creditor);
            editingUtang.setJumlah(amount);
            editingUtang.setBunga(bungaDecimal);
            editingUtang.setTanggalJatuhTempo(dueDate);
            editingUtang.setStatus(status);
            
            // Update sisa_utang proportionally if original amount changed
            double originalAmount = editingUtang.getJumlah();
            if (originalAmount != amount && editingUtang.getSisaUtang() > 0) {
                double ratio = editingUtang.getSisaUtang() / originalAmount;
                editingUtang.setSisaUtang(amount * ratio);
            }
            
            try {
                boolean success = utangDAO.updateUtang(editingUtang);
                if (success) {
                    showModernAlert(Alert.AlertType.INFORMATION, "Success", "💾 Debt updated successfully!");
                    dialogStage.close();
                } else {
                    showModernAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update debt. Please try again.");
                }
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update debt: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // ADD mode
            String utangId = UUID.randomUUID().toString();
            String userId = currentTanggungan.getUserId();

            Utang newUtang = new Utang(utangId, userId, amount, bungaDecimal, dueDate, status, creditor);

            try {
                utangDAO.addUtang(newUtang);
                showModernAlert(Alert.AlertType.INFORMATION, "Success", "🎉 Debt added successfully!");
                dialogStage.close();
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add debt: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleDelete() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Debt");
        confirmAlert.setContentText("Are you sure you want to delete this debt from " + 
                                   editingUtang.getCreditor() + "?\n\nThis action cannot be undone.");
        
        confirmAlert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = utangDAO.deleteUtang(editingUtang.getUtangId(), currentTanggungan.getUserId());
                if (success) {
                    showModernAlert(Alert.AlertType.INFORMATION, "Success", "🗑️ Debt deleted successfully!");
                    dialogStage.close();
                } else {
                    showModernAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete debt. Please try again.");
                }
            } catch (Exception e) {
                showModernAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete debt: " + e.getMessage());
                e.printStackTrace();
            }
        }
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