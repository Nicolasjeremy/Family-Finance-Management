package com.subwayit.gui;

import com.subwayit.dao.UtangDAO;
import com.subwayit.dao.UtangDAO.UtangWithUserInfo;
import com.subwayit.model.Penanggung;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddPayDebtForm {

    private Stage dialogStage;
    private Penanggung currentPenanggung;
    private UtangDAO utangDAO;
    private UtangWithUserInfo debtToPay;
    
    private TextField paymentAmountField;

    public AddPayDebtForm(Penanggung penanggung, UtangWithUserInfo debtToPay, UtangDAO utangDAO) {
        this.currentPenanggung = penanggung;
        this.debtToPay = debtToPay;
        this.utangDAO = utangDAO;
    }

    public void display() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Bayar Utang: " + debtToPay.getCreditor());
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        paymentAmountField = new TextField();
        
        layout.getChildren().addAll(
            new Label("Membayar untuk: " + debtToPay.getUserName()),
            new Label("Sisa Tagihan: " + "Rp " + String.format("%,.0f", debtToPay.getSisaUtang()).replace(',', '.')),
            new Label("Jumlah Pembayaran:"),
            paymentAmountField
        );
        
        Button payButton = new Button("Proses Pembayaran");
        payButton.setOnAction(e -> handlePayment());
        layout.getChildren().add(payButton);
        
        dialogStage.setScene(new Scene(layout));
        dialogStage.showAndWait();
    }
    
    private void handlePayment() {
        try {
            double amount = Double.parseDouble(paymentAmountField.getText());
            if (amount <= 0 || amount > debtToPay.getSisaUtang()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Jumlah pembayaran tidak valid.");
                return;
            }
            if (utangDAO.updatePayment(debtToPay.getUtangId(), amount)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Pembayaran berhasil diproses.");
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal memproses pembayaran.");
            }
        } catch(NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jumlah harus berupa angka.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String msg) { /* ... */ }
}
