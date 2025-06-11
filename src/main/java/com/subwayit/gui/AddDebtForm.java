package com.subwayit.gui;

import com.subwayit.dao.TanggunganDAO;
import com.subwayit.dao.UtangDAO;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.Utang;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

public class AddDebtForm {

    private Stage dialogStage;
    private Penanggung currentPenanggung;
    private TanggunganDAO tanggunganDAO;
    private UtangDAO utangDAO;
    private Utang editingUtang;
    private boolean isEditMode;

    private ComboBox<Tanggungan> userComboBox;
    private TextField creditorField, amountField, bungaField;
    private DatePicker dueDateField;

    public AddDebtForm(Penanggung penanggung, TanggunganDAO tanggunganDAO, UtangDAO utangDAO, Utang utangToEdit) {
        this.currentPenanggung = penanggung;
        this.tanggunganDAO = tanggunganDAO;
        this.utangDAO = utangDAO;
        this.editingUtang = utangToEdit;
        this.isEditMode = (utangToEdit != null);
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(isEditMode ? "Edit Utang" : "Tambah Utang Baru");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        creditorField = new TextField();
        amountField = new TextField();
        bungaField = new TextField();
        dueDateField = new DatePicker(LocalDate.now().plusMonths(1));
        userComboBox = new ComboBox<>();
        
        // Populate ComboBox with family members
        List<Tanggungan> dependents = currentPenanggung.getAnggotaTanggunganIds().stream()
                .map(id -> tanggunganDAO.getTanggunganById(id))
                .collect(Collectors.toList());
        userComboBox.setItems(FXCollections.observableArrayList(dependents));
        userComboBox.setPromptText("Pilih Anggota Keluarga");

        layout.getChildren().addAll(
            new Label("Pemilik Utang:"), userComboBox,
            new Label("Kreditor:"), creditorField,
            new Label("Jumlah Pokok:"), amountField,
            new Label("Bunga (%):"), bungaField,
            new Label("Tanggal Jatuh Tempo:"), dueDateField
        );

        Button saveButton = new Button(isEditMode ? "Update Utang" : "Tambah Utang");
        saveButton.setOnAction(e -> handleSubmit());
        layout.getChildren().add(saveButton);
        
        if(isEditMode) prefillForm();

        dialogStage.setScene(new Scene(layout));
        dialogStage.showAndWait();
    }

    private void prefillForm() {
        creditorField.setText(editingUtang.getCreditor());
        amountField.setText(String.valueOf(editingUtang.getJumlah()));
        bungaField.setText(String.valueOf(editingUtang.getBunga() * 100));
        dueDateField.setValue(editingUtang.getTanggalJatuhTempo());
        // Find and select the user in the combo box
        tanggunganDAO.getTanggunganById(editingUtang.getUserId());
        userComboBox.getSelectionModel().select(tanggunganDAO.getTanggunganById(editingUtang.getUserId()));
        userComboBox.setDisable(true); // Cannot change owner when editing
    }

    private void handleSubmit() {
        try {
            Tanggungan selectedUser = userComboBox.getValue();
            if(selectedUser == null && !isEditMode) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Pilih pemilik utang.");
                return;
            }
            String userId = isEditMode ? editingUtang.getUserId() : selectedUser.getUserId();
            double jumlah = Double.parseDouble(amountField.getText());
            double bunga = Double.parseDouble(bungaField.getText()) / 100.0;
            
            if (isEditMode) {
                editingUtang.setJumlah(jumlah);
                editingUtang.setBunga(bunga);
                editingUtang.setCreditor(creditorField.getText());
                editingUtang.setTanggalJatuhTempo(dueDateField.getValue());
                utangDAO.updateUtang(editingUtang);
            } else {
                Utang newUtang = new Utang(UUID.randomUUID().toString(), userId, jumlah, bunga, dueDateField.getValue(), "Belum Lunas", creditorField.getText());
                utangDAO.addUtang(newUtang);
            }
            dialogStage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jumlah dan Bunga harus angka.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) { /* ... */ }
}