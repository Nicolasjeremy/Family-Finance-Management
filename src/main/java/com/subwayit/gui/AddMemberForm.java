package com.subwayit.gui;

import com.subwayit.dao.AdminDAO;
import com.subwayit.dao.PenanggungDAO;
import com.subwayit.dao.TanggunganDAO;
import com.subwayit.model.Admin;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.User;
import com.subwayit.dao.UserDAO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.UUID;

public class AddMemberForm {

    private Stage dialogStage;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;

    // Form fields
    private TextField userIdField;
    private TextField namaField;
    private TextField umurField;
    private TextField emailField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private TextField posisiKeluargaField;
    private TextField pendidikanField;
    private TextField pekerjaanField;

    public AddMemberForm() {
        this.penanggungDAO = new PenanggungDAO();
        this.tanggunganDAO = new TanggunganDAO();
        this.adminDAO = new AdminDAO();
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Family Member");

        // --- Form Components ---
        userIdField = new TextField();
        userIdField.setPromptText("User ID (Unique)");
        namaField = new TextField();
        namaField.setPromptText("Name");
        umurField = new TextField();
        umurField.setPromptText("Age");
        emailField = new TextField();
        emailField.setPromptText("Email");
        passwordField = new PasswordField();
        passwordField.setPromptText("Initial Password");

        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Penanggung", "Tanggungan", "Admin");
        roleComboBox.setPromptText("Select Role");
        roleComboBox.getSelectionModel().select("Tanggungan"); // Default to Tanggungan

        // Fields specific to Tanggungan/Penanggung
        posisiKeluargaField = new TextField();
        posisiKeluargaField.setPromptText("Family Position (e.g., Anak, Suami, Istri)");
        pendidikanField = new TextField();
        pendidikanField.setPromptText("Education (e.g., SD, SMA, S1)");
        pekerjaanField = new TextField();
        pekerjaanField.setPromptText("Occupation (e.g., Student, Direktur)");

        // --- Form Layout (GridPane for labels and fields) ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));

        int rowIdx = 0; // Keep track of current row index

        formGrid.add(new Label("User ID:"), 0, rowIdx);
        formGrid.add(userIdField, 1, rowIdx++);

        formGrid.add(new Label("Name:"), 0, rowIdx);
        formGrid.add(namaField, 1, rowIdx++);

        formGrid.add(new Label("Age:"), 0, rowIdx);
        formGrid.add(umurField, 1, rowIdx++);

        formGrid.add(new Label("Email:"), 0, rowIdx);
        formGrid.add(emailField, 1, rowIdx++);

        formGrid.add(new Label("Password:"), 0, rowIdx);
        formGrid.add(passwordField, 1, rowIdx++);

        formGrid.add(new Label("Role:"), 0, rowIdx);
        formGrid.add(roleComboBox, 1, rowIdx++);

        // Add specific fields, their visibility will be managed dynamically
        formGrid.add(new Label("Family Position:"), 0, rowIdx);
        formGrid.add(posisiKeluargaField, 1, rowIdx++);
        formGrid.add(new Label("Education:"), 0, rowIdx);
        formGrid.add(pendidikanField, 1, rowIdx++);
        formGrid.add(new Label("Occupation:"), 0, rowIdx);
        formGrid.add(pekerjaanField, 1, rowIdx++);


        // Listener to adjust visibility based on role selection
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isTanggungan = "Tanggungan".equals(newVal);
            boolean isPenanggung = "Penanggung".equals(newVal);

            posisiKeluargaField.setVisible(isTanggungan);
            posisiKeluargaField.setManaged(isTanggungan); // Important for layout

            pendidikanField.setVisible(isTanggungan);
            pendidikanField.setManaged(isTanggungan);

            pekerjaanField.setVisible(isTanggungan || isPenanggung);
            pekerjaanField.setManaged(isTanggungan || isPenanggung);
        });

        // Manually trigger the visibility update for the initially selected role
        // This ensures fields are correctly managed from the start.
        updateRoleSpecificFieldsVisibility(roleComboBox.getValue());


        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button submitButton = new Button("Add Member");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
        submitButton.setOnAction(e -> handleSubmit());

        HBox buttonLayout = new HBox(15);
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);
        buttonLayout.getChildren().addAll(cancelButton, submitButton);


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
     * Helper method to manually update visibility of role-specific fields.
     */
    private void updateRoleSpecificFieldsVisibility(String selectedRole) {
        boolean isTanggungan = "Tanggungan".equals(selectedRole);
        boolean isPenanggung = "Penanggung".equals(selectedRole);

        posisiKeluargaField.setVisible(isTanggungan);
        posisiKeluargaField.setManaged(isTanggungan);

        pendidikanField.setVisible(isTanggungan);
        pendidikanField.setManaged(isTanggungan);

        pekerjaanField.setVisible(isTanggungan || isPenanggung);
        pekerjaanField.setManaged(isTanggungan || isPenanggung);
    }


    private void handleSubmit() {
        String userId = userIdField.getText();
        String nama = namaField.getText();
        String umurStr = umurField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (userId.isEmpty() || nama.isEmpty() || umurStr.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all general fields.");
            return;
        }

        int umur;
        try {
            umur = Integer.parseInt(umurStr);
            if (umur <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Age must be a positive number.");
            return;
        }

        if (new UserDAO().getUserByUserId(userId) != null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "User ID '" + userId + "' already exists. Please choose another.");
            return;
        }

        try {
            switch (role) {
                case "Penanggung":
                    String penanggungPekerjaan = pekerjaanField.getText();
                    if (penanggungPekerjaan.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Input Error", "Occupation is required for Penanggung.");
                        return;
                    }
                    Penanggung newPenanggung = new Penanggung(userId, nama, umur, email, password, penanggungPekerjaan); // Pass pekerjaan to constructor
                    penanggungDAO.addPenanggung(newPenanggung);
                    break;
                case "Tanggungan":
                    String posisiKeluarga = posisiKeluargaField.getText();
                    String pendidikan = pendidikanField.getText();
                    String pekerjaan = pekerjaanField.getText();
                    if (posisiKeluarga.isEmpty() || pendidikan.isEmpty() || pekerjaan.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Input Error", "Family Position, Education, and Occupation are required for Tanggungan.");
                        return;
                    }
                    Tanggungan newTanggungan = new Tanggungan(userId, nama, umur, email, password, posisiKeluarga, pendidikan, pekerjaan);
                    tanggunganDAO.addTanggungan(newTanggungan);
                    break;
                case "Admin":
                    Admin newAdmin = new Admin(userId, nama, umur, email, password, userId);
                    adminDAO.addAdmin(newAdmin);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Role Error", "Invalid role selected.");
                    return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member '" + nama + "' (" + role + ") added successfully!");
            dialogStage.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add member: " + e.getMessage());
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