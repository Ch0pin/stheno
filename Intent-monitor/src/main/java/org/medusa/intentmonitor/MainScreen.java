package org.medusa.intentmonitor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.medusa.intentmonitor.controllers.MainScreenController;
import org.medusa.intentmonitor.socket.SocClient;

import java.io.IOException;
import java.util.Objects;

public class MainScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(MainScreen.class.getResource("main_screen_view.fxml")));
        Scene scene = new Scene(root, 1200, 600);
        stage.getIcons().add(new Image("stheno.png"));
        stage.setTitle("Σθενώ");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception{
        SocClient.getInstance().closeSocket();
    }

    public static void main(String[] args) {
        launch();
    }
}