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
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.UUID;

public class AddTransactionForm {

    private Stage dialogStage;
    private User currentUser;
    private TransaksiDAO transaksiDAO;
    private Transaksi editingTransaksi;

    private DatePicker datePicker;
    private TextField amountField;
    private TextArea descriptionArea;
    private ToggleGroup categoryToggleGroup;
    private ToggleButton monthlyEarningBtn;
    private ToggleButton monthlySpendingBtn;
    private ToggleButton unexpectedEarningBtn;
    private ToggleButton unexpectedSpendingBtn;

    // Theme colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";
    private static final String RED = "#E53E3E";

    /** Constructor for adding new transaction */
    public AddTransactionForm(User user) {
        this.currentUser = user;
        this.transaksiDAO = new TransaksiDAO();
    }

    /** Constructor for editing: accepts Transaction object to be modified */
    public AddTransactionForm(User user, Transaksi transaksi) {
        this(user);
        this.editingTransaksi = transaksi;
    }

    /** Display form (modal) */
    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(editingTransaksi == null ? "Add New Transaction" : "Edit Transaction");

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

        return new Scene(root, 600, 700);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        // Title with icon
        String titleText = editingTransaksi == null ? "Add New Transaction ✨" : "Edit Transaction ✏️";
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web(TEXT_DARK));

        // Subtitle
        String subtitleText = editingTransaksi == null ? 
            "Create a new financial record" : 
            "Update your transaction details";
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

        // Date field
        VBox dateSection = createFieldSection("📅 Transaction Date", createModernDatePicker());
        
        // Amount field
        VBox amountSection = createFieldSection("💰 Amount", createModernAmountField());
        
        // Description field
        VBox descSection = createFieldSection("📝 Description", createModernDescriptionArea());
        
        // Category section
        VBox categorySection = createCategorySection();

        formContent.getChildren().addAll(dateSection, amountSection, descSection, categorySection);

        // Pre-fill form if editing
        if (editingTransaksi != null) {
            prefillForm(editingTransaksi);
        } else {
            // Default values for new transaction
            datePicker.setValue(LocalDate.now());
            monthlyEarningBtn.setSelected(true);
            updateToggleButtonStyle(monthlyEarningBtn, true);
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

    private DatePicker createModernDatePicker() {
        datePicker = new DatePicker();
        datePicker.setPromptText("Select transaction date");
        datePicker.setPrefWidth(420);
        datePicker.setPrefHeight(45);
        datePicker.setStyle("-fx-background-color: white; " +
                           "-fx-border-color: #E2E8F0; " +
                           "-fx-border-width: 2; " +
                           "-fx-border-radius: 8; " +
                           "-fx-background-radius: 8; " +
                           "-fx-font-family: 'Segoe UI'; " +
                           "-fx-font-size: 14px;");

        // Focus styling
        datePicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                datePicker.setStyle(datePicker.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                datePicker.setStyle(datePicker.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return datePicker;
    }

    private TextField createModernAmountField() {
        amountField = new TextField();
        amountField.setPromptText("Enter amount (e.g., 100000)");
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

    private TextArea createModernDescriptionArea() {
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter transaction description...");
        descriptionArea.setPrefWidth(420);
        descriptionArea.setPrefHeight(80);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        descriptionArea.setFont(Font.font("Segoe UI", 14));
        descriptionArea.setStyle("-fx-background-color: white; " +
                                "-fx-border-color: #E2E8F0; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-padding: 12;");

        // Focus styling
        descriptionArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                descriptionArea.setStyle(descriptionArea.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            } else {
                descriptionArea.setStyle(descriptionArea.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
            }
        });

        return descriptionArea;
    }

    private VBox createCategorySection() {
        VBox categorySection = new VBox(15);
        
        Label categoryLabel = new Label("🏷️ Transaction Category");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        categoryLabel.setTextFill(Color.web(TEXT_DARK));

        // Create toggle buttons
        categoryToggleGroup = new ToggleGroup();
        monthlyEarningBtn = createModernToggleButton("💰 Monthly Earning", "Regular income like salary");
        monthlySpendingBtn = createModernToggleButton("🏠 Monthly Spending", "Regular expenses like bills");
        unexpectedEarningBtn = createModernToggleButton("🎉 Unexpected Earning", "Bonus, gifts, or windfalls");
        unexpectedSpendingBtn = createModernToggleButton("⚠️ Unexpected Spending", "Emergency or unplanned expenses");

        monthlyEarningBtn.setToggleGroup(categoryToggleGroup);
        monthlySpendingBtn.setToggleGroup(categoryToggleGroup);
        unexpectedEarningBtn.setToggleGroup(categoryToggleGroup);
        unexpectedSpendingBtn.setToggleGroup(categoryToggleGroup);

        // Toggle selection listener
        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) updateToggleButtonStyle((ToggleButton) oldToggle, false);
            if (newToggle != null) updateToggleButtonStyle((ToggleButton) newToggle, true);
        });

        // Layout toggle buttons in grid
        HBox row1 = new HBox(15, monthlyEarningBtn, monthlySpendingBtn);
        row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(15, unexpectedEarningBtn, unexpectedSpendingBtn);
        row2.setAlignment(Pos.CENTER);
        
        VBox toggleContainer = new VBox(12, row1, row2);
        toggleContainer.setAlignment(Pos.CENTER);

        categorySection.getChildren().addAll(categoryLabel, toggleContainer);
        return categorySection;
    }

    private ToggleButton createModernToggleButton(String text, String tooltip) {
        ToggleButton button = new ToggleButton(text);
        button.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setWrapText(true);
        button.setTooltip(new Tooltip(tooltip));
        
        updateToggleButtonStyle(button, false);
        
        return button;
    }

    private void updateToggleButtonStyle(ToggleButton button, boolean selected) {
        if (selected) {
            button.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 8; " +
                           "-fx-border-radius: 8; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2); " +
                           "-fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: white; " +
                           "-fx-text-fill: " + TEXT_DARK + "; " +
                           "-fx-border-color: #E2E8F0; " +
                           "-fx-border-width: 2; " +
                           "-fx-background-radius: 8; " +
                           "-fx-border-radius: 8; " +
                           "-fx-cursor: hand;");
        }

        // Hover effects
        button.setOnMouseEntered(e -> {
            if (!button.isSelected()) {
                button.setStyle(button.getStyle() + "-fx-border-color: " + PRIMARY_GREEN + ";");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!button.isSelected()) {
                button.setStyle(button.getStyle().replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;"));
            }
        });
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
        String submitText = editingTransaksi == null ? "✨ Add Transaction" : "💾 Update Transaction";
        Button submitBtn = new Button(submitText);
        submitBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        submitBtn.setPrefWidth(180);
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

    /** Pre-fill all fields from the Transaction object being edited */
    private void prefillForm(Transaksi t) {
        datePicker.setValue(t.getTanggalTransaksi());
        amountField.setText(String.valueOf(t.getNominal()));
        descriptionArea.setText(t.getDeskripsi());

        // Select toggle based on type & routine
        boolean rutin = t.isRutin();
        if (t.getJenis().equalsIgnoreCase("Pemasukan") && rutin) {
            monthlyEarningBtn.setSelected(true);
            updateToggleButtonStyle(monthlyEarningBtn, true);
        }
        else if (t.getJenis().equalsIgnoreCase("Pengeluaran") && rutin) {
            monthlySpendingBtn.setSelected(true);
            updateToggleButtonStyle(monthlySpendingBtn, true);
        }
        else if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
            unexpectedEarningBtn.setSelected(true);
            updateToggleButtonStyle(unexpectedEarningBtn, true);
        }
        else {
            unexpectedSpendingBtn.setSelected(true);
            updateToggleButtonStyle(unexpectedSpendingBtn, true);
        }
    }

    /** Save new data or update if editing */
    private void handleSubmit() {
        LocalDate date = datePicker.getValue();
        String desc = descriptionArea.getText().trim();
        String amtText = amountField.getText().trim();
        ToggleButton sel = (ToggleButton) categoryToggleGroup.getSelectedToggle();

        if (date == null || desc.isEmpty() || amtText.isEmpty() || sel == null) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Please complete all fields and select a category.");
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(amtText);
            if (nominal <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showModernAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a positive number.");
            return;
        }

        // Determine type, category, and isRutin
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
            // ADD mode
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
            showModernAlert(Alert.AlertType.INFORMATION, "Success", "🎉 Transaction added successfully!");
        } else {
            // EDIT mode: modify fields and save
            editingTransaksi.setTanggalTransaksi(date);
            editingTransaksi.setDeskripsi(desc);
            editingTransaksi.setNominal(nominal);
            editingTransaksi.setJenis(jenis);
            editingTransaksi.setRutin(isRutin);
            editingTransaksi.setKategori(kategori);
            transaksiDAO.updateTransaksi(editingTransaksi);
            showModernAlert(Alert.AlertType.INFORMATION, "Success", "💾 Transaction updated successfully!");
        }

        dialogStage.close();
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