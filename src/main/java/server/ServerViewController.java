package server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ServerViewController {

    @FXML
    private Label portLabel;

    public void setPort(int port) {
        if (portLabel != null) {
            portLabel.setText("Port: " + port);
        }
    }

    @FXML
    private void handleStopServer() {
        // Appelle la méthode statique d'arrêt propre dans ServerApp
        ServerApp.requestStop();
    }
}