package server;

import javafx.fxml.FXML;

public class ServerViewController {

    @FXML
    private void handleStopServer() {
        // Appelle la méthode statique d'arrêt propre dans ServerApp
        ServerApp.requestStop();
    }
}