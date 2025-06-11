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
    
    // DAO attributes
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;
    private AdminDAO adminDAO;
    private UserDAO userDAO;

    private User currentUser; // The user who is currently logged in

    // Form fields
    private TextField userIdField, namaField, umurField, emailField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private TextField posisiKeluargaField, pendidikanField, pekerjaanField, penanggungIdField;
    private Label penanggungIdLabel;

    public AddMemberForm(User currentUser, UserDAO userDAO, PenanggungDAO penanggungDAO, TanggunganDAO tanggunganDAO, AdminDAO adminDAO) {
        this.currentUser = currentUser;
        this.userDAO = userDAO;
        this.penanggungDAO = penanggungDAO;
        this.tanggunganDAO = tanggunganDAO;
        this.adminDAO = adminDAO;
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Tambah Anggota Baru");

        GridPane formGrid = createFormGrid();
        
        updateRoleSpecificFieldsVisibility(roleComboBox.getValue());

        Button cancelButton = new Button("Batal");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button submitButton = new Button("Tambah Anggota");
        submitButton.setOnAction(e -> handleSubmit());
        
        HBox buttonLayout = new HBox(15, cancelButton, submitButton);
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);

        VBox dialogLayout = new VBox(20, formGrid, buttonLayout);
        dialogLayout.setPadding(new Insets(20));
        
        Scene scene = new Scene(dialogLayout);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private GridPane createFormGrid() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);

        // Initialize all fields
        userIdField = new TextField();
        userIdField.setEditable(false);
        namaField = new TextField();
        umurField = new TextField();
        emailField = new TextField();
        passwordField = new PasswordField();
        passwordField.setEditable(false);
        roleComboBox = new ComboBox<>();
        posisiKeluargaField = new TextField();
        pendidikanField = new TextField();
        pekerjaanField = new TextField();
        penanggungIdField = new TextField();
        penanggungIdLabel = new Label("ID Penanggung:");
        
        // Configure Role ComboBox
        roleComboBox.getItems().addAll("Penanggung", "Tanggungan", "Admin");
        if ("Penanggung".equals(currentUser.getRole())) {
            roleComboBox.setValue("Tanggungan");
            roleComboBox.setDisable(true);
        } else if ("Admin".equals(currentUser.getRole())) {
            roleComboBox.setValue("Penanggung");
        }
        
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateRoleSpecificFieldsVisibility(newVal));

        // Layouting
        int row = 0;
        formGrid.add(new Label("User ID:"), 0, row);
        formGrid.add(userIdField, 1, row++);
        formGrid.add(new Label("Password Awal:"), 0, row);
        formGrid.add(passwordField, 1, row++);
        formGrid.add(new Label("Nama Lengkap:"), 0, row);
        formGrid.add(namaField, 1, row++);
        formGrid.add(new Label("Umur:"), 0, row);
        formGrid.add(umurField, 1, row++);
        formGrid.add(new Label("Email:"), 0, row);
        formGrid.add(emailField, 1, row++);
        formGrid.add(new Label("Peran:"), 0, row);
        formGrid.add(roleComboBox, 1, row++);
        formGrid.add(new Label("Pekerjaan:"), 0, row);
        formGrid.add(pekerjaanField, 1, row++);
        formGrid.add(new Label("Posisi Keluarga:"), 0, row);
        formGrid.add(posisiKeluargaField, 1, row++);
        formGrid.add(new Label("Pendidikan:"), 0, row);
        formGrid.add(pendidikanField, 1, row++);
        formGrid.add(penanggungIdLabel, 0, row);
        formGrid.add(penanggungIdField, 1, row);
        
        return formGrid;
    }
    
    private void updateRoleSpecificFieldsVisibility(String selectedRole) {
        boolean isTanggungan = "Tanggungan".equals(selectedRole);
        boolean isPenanggung = "Penanggung".equals(selectedRole);
        
        userIdField.setPromptText("Akan digenerate otomatis");
        passwordField.setPromptText("Akan digenerate otomatis");
        
        setVisibility(posisiKeluargaField, isTanggungan);
        setVisibility(pendidikanField, isTanggungan);
        setVisibility(pekerjaanField, isPenanggung || isTanggungan);
        
        if ("Penanggung".equals(currentUser.getRole())) {
            penanggungIdField.setText(currentUser.getUserId());
            penanggungIdField.setEditable(false);
            setVisibility(penanggungIdLabel, true);
            setVisibility(penanggungIdField, true);
        } else if ("Admin".equals(currentUser.getRole())) {
            penanggungIdField.setEditable(true);
            penanggungIdField.clear();
            setVisibility(penanggungIdLabel, isTanggungan);
            setVisibility(penanggungIdField, isTanggungan);
        } else {
             setVisibility(penanggungIdLabel, false);
             setVisibility(penanggungIdField, false);
        }
    }
    
    private void setVisibility(javafx.scene.Node node, boolean isVisible) {
        node.setVisible(isVisible);
        node.setManaged(isVisible);
    }

    private void handleSubmit() {
        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 8);
        String password = "pass-" + UUID.randomUUID().toString().substring(0, 4);
        String nama = namaField.getText().trim();
        String umurStr = umurField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();
        
        if (nama.isEmpty() || umurStr.isEmpty() || email.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Lengkap", "Semua field wajib diisi.");
            return;
        }

        int umur;
        try {
            umur = Integer.parseInt(umurStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Salah", "Umur harus berupa angka.");
            return;
        }

        // Check for unique user ID before proceeding
        if (userDAO.getUserByUserId(userId) != null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "User ID yang digenerate sudah ada, silakan coba lagi.");
            return;
        }

        try {
            User newUser = new User(userId, nama, umur, email, password, role);
            userDAO.addUser(newUser);

            switch (role) {
                case "Penanggung":
                    Penanggung newPenanggung = new Penanggung(userId, nama, umur, email, password, pekerjaanField.getText().trim());
                    penanggungDAO.addPenanggung(newPenanggung);
                    break;

                case "Tanggungan":
                    String penanggungId = penanggungIdField.getText().trim();
                    // --- FIX: Fetch the Penanggung object before trying to use it ---
                    Penanggung penanggungToUpdate = penanggungDAO.getPenanggungById(penanggungId);

                    if (penanggungToUpdate == null) {
                        userDAO.deleteUser(userId); // Rollback user creation
                        showAlert(Alert.AlertType.ERROR, "Input Salah", "ID Penanggung untuk Tanggungan tidak valid atau kosong.");
                        return;
                    }

                    // Create and add the new Tanggungan
                    Tanggungan newTanggungan = new Tanggungan(userId, nama, umur, email, password,
                            posisiKeluargaField.getText().trim(),
                            pendidikanField.getText().trim(),
                            pekerjaanField.getText().trim(),
                            penanggungId);
                    tanggunganDAO.addTanggungan(newTanggungan, penanggungId);

                    // Now, update the fetched Penanggung object
                    penanggungToUpdate.addAnggotaTanggunganId(newTanggungan.getUserId());
                    penanggungDAO.updatePenanggung(penanggungToUpdate);
                    break;

                case "Admin":
                    Admin newAdmin = new Admin(userId, nama, umur, email, password, userId);
                    adminDAO.addAdmin(newAdmin);
                    break;
            }

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Anggota baru '" + nama + "' berhasil ditambahkan.");
            dialogStage.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal menambahkan anggota: " + e.getMessage());
            e.printStackTrace();
            // Consider rolling back user creation if subsequent steps fail
            userDAO.deleteUser(userId);
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