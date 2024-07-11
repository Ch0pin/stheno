package org.medusa.intentmonitor;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorHandler {

    public static void handleException(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }

    public static void handleExceptionWithMsg(Exception e, String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
}
