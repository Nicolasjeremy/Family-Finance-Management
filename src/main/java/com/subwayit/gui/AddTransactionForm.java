package com.subwayit.gui;

import com.subwayit.dao.TransaksiDAO;
import com.subwayit.model.Transaksi;
import com.subwayit.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
    private TransaksiDAO transaksiDAO; // Will be injected
    private Transaksi editingTransaksi; // If not null, we are in "edit" mode

    // Form components
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

    // --- FIX 1: Unify constructors and inject the DAO dependency ---
    /**
     * Universal constructor for adding or editing a transaction.
     * @param currentUser The user performing the action.
     * @param transaksiDAO The shared DAO instance from the main application.
     * @param editingTransaksi The transaction to edit. Pass null to create a new one.
     */
    public AddTransactionForm(User currentUser, TransaksiDAO transaksiDAO, Transaksi editingTransaksi) {
        this.currentUser = currentUser;
        this.transaksiDAO = transaksiDAO; // Use the injected DAO
        this.editingTransaksi = editingTransaksi; // Null for "add" mode, non-null for "edit" mode
    }

    /** Displays the form as a modal dialog */
    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(editingTransaksi == null ? "Add New Transaction" : "Edit Transaction");

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
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 8); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 16;");

        formCard.getChildren().addAll(createHeaderSection(), createFormContent(), createButtonSection());
        root.getChildren().add(formCard);

        return new Scene(root, 600, 700);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER);

        String titleText = editingTransaksi == null ? "Add New Transaction ✨" : "Edit Transaction ✏️";
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        String subtitleText = editingTransaksi == null ? "Create a new financial record" : "Update your transaction details";
        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(TEXT_GRAY));

        Rectangle decorativeLine = new Rectangle(60, 3, Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel, decorativeLine);
        return headerSection;
    }

    private VBox createFormContent() {
        VBox formContent = new VBox(20);

        datePicker = createModernDatePicker();
        amountField = createModernAmountField();
        descriptionArea = createModernDescriptionArea();

        VBox dateSection = createFieldSection("📅 Transaction Date", datePicker);
        VBox amountSection = createFieldSection("💰 Amount", amountField);
        VBox descSection = createFieldSection("📝 Description", descriptionArea);
        VBox categorySection = createCategorySection();

        formContent.getChildren().addAll(dateSection, amountSection, descSection, categorySection);

        if (editingTransaksi != null) {
            prefillForm(editingTransaksi);
        } else {
            datePicker.setValue(LocalDate.now());
            monthlySpendingBtn.setSelected(true); // Default to spending
            updateToggleButtonStyle(monthlySpendingBtn, true);
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
    
    // createModernDatePicker(), createModernAmountField(), createModernDescriptionArea() methods are unchanged...
    // ... (Your implementation was good, no changes needed here)
    private DatePicker createModernDatePicker() {
        datePicker = new DatePicker();
        datePicker.setPromptText("Select transaction date");
        datePicker.setPrefWidth(420);
        datePicker.setPrefHeight(45);
        datePicker.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        datePicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) datePicker.setStyle(datePicker.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            else datePicker.setStyle(datePicker.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
        });
        return datePicker;
    }

    private TextField createModernAmountField() {
        amountField = new TextField();
        amountField.setPromptText("Enter amount (e.g., 100000)");
        amountField.setPrefWidth(420);
        amountField.setPrefHeight(45);
        amountField.setFont(Font.font("Segoe UI", 14));
        amountField.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        amountField.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
        amountField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) amountField.setStyle(amountField.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            else amountField.setStyle(amountField.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
        });
        return amountField;
    }

    private TextArea createModernDescriptionArea() {
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter transaction description...");
        descriptionArea.setPrefWidth(420);
        descriptionArea.setPrefHeight(80);
        descriptionArea.setWrapText(true);
        descriptionArea.setFont(Font.font("Segoe UI", 14));
        descriptionArea.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        descriptionArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) descriptionArea.setStyle(descriptionArea.getStyle().replace("#E2E8F0", PRIMARY_GREEN));
            else descriptionArea.setStyle(descriptionArea.getStyle().replace(PRIMARY_GREEN, "#E2E8F0"));
        });
        return descriptionArea;
    }


    private VBox createCategorySection() {
        VBox categorySection = new VBox(15);
        Label categoryLabel = new Label("🏷️ Transaction Category");
        categoryLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));

        categoryToggleGroup = new ToggleGroup();
        monthlyEarningBtn = createModernToggleButton("💰 Monthly Earning", "Regular income like salary", categoryToggleGroup);
        monthlySpendingBtn = createModernToggleButton("🏠 Monthly Spending", "Regular expenses like bills", categoryToggleGroup);
        unexpectedEarningBtn = createModernToggleButton("🎉 Unexpected Earning", "Bonus, gifts, or windfalls", categoryToggleGroup);
        unexpectedSpendingBtn = createModernToggleButton("⚠️ Unexpected Spending", "Emergency or unplanned expenses", categoryToggleGroup);

        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) updateToggleButtonStyle((ToggleButton) oldToggle, false);
            if (newToggle != null) updateToggleButtonStyle((ToggleButton) newToggle, true);
        });

        HBox row1 = new HBox(15, monthlyEarningBtn, monthlySpendingBtn);
        HBox row2 = new HBox(15, unexpectedEarningBtn, unexpectedSpendingBtn);
        VBox toggleContainer = new VBox(12, row1, row2);

        categorySection.getChildren().addAll(categoryLabel, toggleContainer);
        return categorySection;
    }

    private ToggleButton createModernToggleButton(String text, String tooltip, ToggleGroup group) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(group);
        button.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setTooltip(new Tooltip(tooltip));
        updateToggleButtonStyle(button, false);
        return button;
    }

    private void updateToggleButtonStyle(ToggleButton button, boolean selected) {
        String baseStyle = "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        if (selected) {
            button.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; -fx-text-fill: white; -fx-font-weight: bold; " + baseStyle);
        } else {
            button.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_DARK + "; -fx-border-color: #E2E8F0; -fx-border-width: 2; " + baseStyle);
        }
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(15);
        buttonSection.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("❌ Cancel");
        styleModernButton(cancelBtn, "white", "#E2E8F0", TEXT_DARK, RED);
        cancelBtn.setOnAction(e -> dialogStage.close());

        String submitText = editingTransaksi == null ? "✨ Add Transaction" : "💾 Update Transaction";
        Button submitBtn = new Button(submitText);
        styleModernButton(submitBtn, PRIMARY_GREEN, DARK_GREEN, "white", null);
        submitBtn.setOnAction(e -> handleSubmit());

        buttonSection.getChildren().addAll(cancelBtn, submitBtn);
        return buttonSection;
    }

    private void styleModernButton(Button btn, String bgColor, String hoverBgColor, String textColor, String hoverBorderColor) {
        final String originalStyle = String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;", bgColor, textColor);
        btn.setStyle(originalStyle);
        btn.setPrefHeight(45);
        btn.setPrefWidth(180);

        btn.setOnMouseEntered(e -> {
            String hoverStyle = String.format("-fx-background-color: %s; -fx-text-fill: %s;", hoverBgColor, textColor);
            if (hoverBorderColor != null) {
                hoverStyle += String.format("-fx-border-color: %s;", hoverBorderColor);
            }
            btn.setStyle(btn.getStyle() + hoverStyle);
        });
        btn.setOnMouseExited(e -> btn.setStyle(originalStyle));
    }
    
    private void prefillForm(Transaksi t) {
        datePicker.setValue(t.getTanggalTransaksi());
        amountField.setText(String.format("%.0f", t.getNominal()));
        descriptionArea.setText(t.getDeskripsi());

        boolean rutin = t.isRutin();
        if (t.getJenis().equalsIgnoreCase("Pemasukan")) {
            categoryToggleGroup.selectToggle(rutin ? monthlyEarningBtn : unexpectedEarningBtn);
        } else {
            categoryToggleGroup.selectToggle(rutin ? monthlySpendingBtn : unexpectedSpendingBtn);
        }
    }

    private void handleSubmit() {
        LocalDate date = datePicker.getValue();
        String desc = descriptionArea.getText().trim();
        String amtText = amountField.getText().trim();
        ToggleButton selectedToggle = (ToggleButton) categoryToggleGroup.getSelectedToggle();

        if (date == null || desc.isEmpty() || amtText.isEmpty() || selectedToggle == null) {
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

        String jenis;
        boolean isRutin;
        String kategori;

        if (selectedToggle == monthlyEarningBtn) {
            jenis = "Pemasukan"; isRutin = true; kategori = "Gaji";
        } else if (selectedToggle == monthlySpendingBtn) {
            jenis = "Pengeluaran"; isRutin = true; kategori = "Rumah Tangga";
        } else if (selectedToggle == unexpectedEarningBtn) {
            jenis = "Pemasukan"; isRutin = false; kategori = "Lain-lain";
        } else { // unexpectedSpendingBtn
            jenis = "Pengeluaran"; isRutin = false; kategori = "Lain-lain";
        }

        if (editingTransaksi == null) {
            // ADD mode
            Transaksi t = new Transaksi(
                "TRX-" + UUID.randomUUID().toString().substring(0, 8),
                currentUser.getUserId(),
                jenis, kategori, nominal, date,
                "SubwayIT App", // Payee/From placeholder
                isRutin, desc
            );
            transaksiDAO.addTransaksi(t);
            showModernAlert(Alert.AlertType.INFORMATION, "Success", "🎉 Transaction added successfully!");
        } else {
            // EDIT mode
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
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        alert.showAndWait();
    }
}