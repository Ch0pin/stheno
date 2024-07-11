package org.medusa.intentmonitor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SettingsDialogController {

    @FXML
    private TextField serverAddressField;

    @FXML
    private TextField serverPortField;

    private Stage dialogStage;
    private MainScreenController mainController;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainScreenController mainController) {
        this.mainController = mainController;
        // Initialize the fields with current settings
        serverAddressField.setText(mainController.getServerAddress());
        serverPortField.setText(String.valueOf(mainController.getServerPort()));
    }

    @FXML
    private void handleApply() {
        String serverAddress = serverAddressField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        mainController.setServerAddress(serverAddress);
        mainController.setServerPort(serverPort);

        dialogStage.close();
    }


    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

}
