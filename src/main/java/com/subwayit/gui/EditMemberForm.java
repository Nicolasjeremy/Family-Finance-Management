package com.subwayit.gui;

import com.subwayit.dao.PenanggungDAO;
import com.subwayit.dao.TanggunganDAO;
import com.subwayit.dao.UserDAO;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
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

public class EditMemberForm {

    private Stage dialogStage;
    private User userToEdit;

    // DAOs for saving data
    private UserDAO userDAO;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;

    // Form fields
    private TextField namaField;
    private TextField umurField;
    private TextField emailField;
    private PasswordField passwordField;
    private TextField posisiKeluargaField; // Only for Tanggungan
    private TextField pendidikanField;     // Only for Tanggungan
    private TextField pekerjaanField;      // For Penanggung or Tanggungan

    public EditMemberForm(User userToEdit, UserDAO userDAO, PenanggungDAO penanggungDAO, TanggunganDAO tanggunganDAO) {
        this.userToEdit = userToEdit;
        this.userDAO = userDAO;
        this.penanggungDAO = penanggungDAO;
        this.tanggunganDAO = tanggunganDAO;
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edit Profile: " + userToEdit.getNama());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        
        Label title = new Label("Update Profile");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane formGrid = createFormGrid();
        prefillForm();

        Button saveButton = new Button("💾 Save Changes");
        saveButton.setOnAction(e -> handleSubmit());
        saveButton.setStyle("-fx-background-color: #86DA71; -fx-text-fill: white; -fx-font-weight: bold;");

        layout.getChildren().addAll(title, formGrid, saveButton);
        
        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);

        // Initialize fields
        namaField = new TextField();
        umurField = new TextField();
        emailField = new TextField();
        passwordField = new PasswordField();
        posisiKeluargaField = new TextField();
        pendidikanField = new TextField();
        pekerjaanField = new TextField();

        // Layout fields
        int row = 0;
        grid.add(new Label("Nama:"), 0, row);
        grid.add(namaField, 1, row++);
        grid.add(new Label("Umur:"), 0, row);
        grid.add(umurField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("New Password:"), 0, row);
        grid.add(passwordField, 1, row++);
        passwordField.setPromptText("Leave blank to keep current password");

        // Add role-specific fields
        if (userToEdit instanceof Penanggung || userToEdit instanceof Tanggungan) {
            grid.add(new Label("Pekerjaan:"), 0, row);
            grid.add(pekerjaanField, 1, row++);
        }
        if (userToEdit instanceof Tanggungan) {
            grid.add(new Label("Posisi Keluarga:"), 0, row);
            grid.add(posisiKeluargaField, 1, row++);
            grid.add(new Label("Pendidikan:"), 0, row);
            grid.add(pendidikanField, 1, row++);
        }

        return grid;
    }

    private void prefillForm() {
        namaField.setText(userToEdit.getNama());
        umurField.setText(String.valueOf(userToEdit.getUmur()));
        emailField.setText(userToEdit.getEmail());

        if (userToEdit instanceof Penanggung) {
            pekerjaanField.setText(((Penanggung) userToEdit).getPekerjaan());
        } else if (userToEdit instanceof Tanggungan) {
            Tanggungan t = (Tanggungan) userToEdit;
            pekerjaanField.setText(t.getPekerjaan());
            posisiKeluargaField.setText(t.getPosisiKeluarga());
            pendidikanField.setText(t.getPendidikan());
        }
    }

    private void handleSubmit() {
        try {
            // Update general user info
            userToEdit.setNama(namaField.getText().trim());
            userToEdit.setUmur(Integer.parseInt(umurField.getText().trim()));
            userToEdit.setEmail(emailField.getText().trim());

            // Only update password if a new one is provided
            if (!passwordField.getText().isEmpty()) {
                userToEdit.setPassword(passwordField.getText());
            }

            // Update the base User table
            userDAO.updateUser(userToEdit);

            // Update role-specific tables
            if (userToEdit instanceof Penanggung) {
                Penanggung p = (Penanggung) userToEdit;
                p.setPekerjaan(pekerjaanField.getText().trim());
                penanggungDAO.updatePenanggung(p);
            } else if (userToEdit instanceof Tanggungan) {
                Tanggungan t = (Tanggungan) userToEdit;
                t.setPekerjaan(pekerjaanField.getText().trim());
                t.setPosisiKeluarga(posisiKeluargaField.getText().trim());
                t.setPendidikan(pendidikanField.getText().trim());
                tanggunganDAO.updateTanggungan(t);
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            dialogStage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Umur must be a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update profile: " + e.getMessage());
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