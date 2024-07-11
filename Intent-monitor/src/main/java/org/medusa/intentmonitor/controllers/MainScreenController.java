package org.medusa.intentmonitor.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.medusa.intentmonitor.ErrorHandler;
import org.medusa.intentmonitor.helpers.IntentItem;
import org.medusa.intentmonitor.helpers.MainScreenUtils;
import org.medusa.intentmonitor.socket.SocClient;
import org.json.JSONObject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainScreenController implements Initializable {
    @FXML
    private TableView<IntentItem> intentTableView;
    @FXML
    private TableColumn<IntentItem, Integer> indexColumn;
    @FXML
    private TableColumn<IntentItem, String> descriptionColumn;
    @FXML
    private TableColumn<IntentItem, String> destinationPackageColumn;
    @FXML
    private TableColumn<IntentItem, String> destinationClassColumn;
    @FXML
    private TableColumn<IntentItem, Boolean> isExportedColumn;
    @FXML
    private MenuItem startMonitor;
    @FXML
    private MenuItem stopMonitor;
    @FXML
    private Label connectionStatusLabel;
    @FXML
    private HBox connectionStatusBox;
    @FXML
    private WebView webView;

    private String serverAddress = "localhost";
    private int serverPort = 1711;
    private ResourceBundle resourceBundle;
    private final ObservableList<IntentItem> intentsList = FXCollections.observableArrayList();
    private final List<String> intentListWithFullInfo = new ArrayList<>();
    private SocClient socketClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        indexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty().asObject());
        indexColumn.setStyle("-fx-alignment: CENTER;");
        indexColumn.setPrefWidth(40);

        descriptionColumn.setPrefWidth(600);
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        destinationPackageColumn.setPrefWidth(150);
        destinationPackageColumn.setStyle("-fx-alignment: CENTER");

        destinationClassColumn.setPrefWidth(110);
        destinationClassColumn.setStyle("-fx-alignment: CENTER");

        isExportedColumn.setPrefWidth(120);
        isExportedColumn.setStyle("-fx-alignment: CENTER;");

        intentTableView.setItems(intentsList);
        socketClient = new SocClient(this);
        startMonitor.disableProperty().bind(socketClient.connectedProperty());
        stopMonitor.disableProperty().bind(socketClient.connectedProperty().not());
        socketClient.connectedProperty().addListener((observable, oldValue, newValue) -> updateConnectionStatus(newValue));
        connectionStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
        connectionStatusBox.setStyle("-fx-background-color: red; -fx-padding: 2;");
        this.resourceBundle = ResourceBundle.getBundle("html_content", Locale.getDefault());

    }

    @FXML
    protected void handleMenuAction(ActionEvent event){
        MenuItem source = (MenuItem) event.getSource();
        String itemId = source.getId();
        switch(itemId) {
            case "startMonitor":
                handleStartMonitor();
                break;
            case "stopMonitor":
                handleStopMonitor();
                break;
            case "clearLogs":
                intentsList.clear();
                intentListWithFullInfo.clear();
                webView.getEngine().loadContent("");
                break;
            case "exit":
                handleExit();
                break;
            default:
                System.out.println("Unknown menu item clicked: " + itemId);
                break;
        }
    }

    @FXML
    private void handleSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/medusa/intentmonitor/settings_dialog.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(intentTableView.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SettingsDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTableViewClick() {
        try {
            IntentItem selectedItem = intentTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                JSONObject intent = new JSONObject(intentListWithFullInfo.get(selectedItem.getIndex()));
                String description = (String) intent.get("description");
                String componentName = "";
                String flags = String.format("0x%08X", (int) intent.get("flags"));
                Pattern pattern = Pattern.compile("cmp=([^\\s]+)");
                String bundleString = (String) intent.get("extras");
                //String extrasToJson = bundleString;//MainScreenUtils.beautifyBundleString(MainScreenUtils.removeExtraNoise(bundleString));

                Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    componentName = matcher.group(1);
                }
                String htmlTemplate = resourceBundle.getString("html_content");

                String htmlContent = String.format(htmlTemplate,
                        intent.get("description"), componentName, intent.get("targetIsExported"),
                        intent.get("action"), intent.get("data"), intent.get("type"), flags, bundleString);
                WebEngine webEngine = webView.getEngine();
                webEngine.loadContent(htmlContent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleExit(){
        if(socketClient != null)
            socketClient.closeSocket();
        Platform.exit();
    }

    private void handleStartMonitor(){
        try {
            socketClient.connect(serverAddress, serverPort);
        } catch (ConnectException e) {
            ErrorHandler.handleExceptionWithMsg(e, "Please check if the server is up and running, and verify the server address and port.");
        } catch (IOException e) {
            ErrorHandler.handleExceptionWithMsg(e,"An unexpected I/O error occurred");}
    }

    private void handleStopMonitor(){
        socketClient.closeSocket();
    }

    public void addItem(String newItem) {
        try {
            JSONObject intent = new JSONObject(newItem);
            String description = intent.getString("description");
            String destinationPackage = intent.getString("targetPackageName");
            String destinationClassName = intent.getString("targetClassName");
            Boolean isExported = intent.getBoolean("targetIsExported");
            intentListWithFullInfo.add(newItem);
            Platform.runLater(() -> intentsList.add(new IntentItem(intentListWithFullInfo.size() - 1, description, destinationPackage, destinationClassName, isExported)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateConnectionStatus(boolean isConnected) {
        Platform.runLater(() -> {
            if (isConnected) {
                connectionStatusLabel.setText("Connected to "+serverAddress+":"+serverPort);
                connectionStatusBox.setStyle("-fx-background-color: green; -fx-padding: 2;");
            } else {
                connectionStatusLabel.setText("Disconnected");
                connectionStatusBox.setStyle("-fx-background-color: red; -fx-padding: 2;");
            }
        });
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}

