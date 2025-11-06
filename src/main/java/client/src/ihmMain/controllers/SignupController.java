package client.src.ihmMain.controllers;

import client.src.MainApp;
import javafx.fxml.FXML;

public class SignupController {

    @FXML
    private void onBackToLogin() {
        try {
            MainApp.loadScene("/login.fxml", "Login");
        } catch (Exception e) {
            System.err.println("Cannot open Login: " + e.getMessage());
        }
    }
}