package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class LandingPageController {

    private MainCore core;

    @FXML
    private BorderPane root;

    @FXML
    private Button logoButton;

    @FXML
    public void initialize() {
        // Récupère le MainCore initialisé dans MainApp.start()
        core = MainApp.getCore();

        if (core == null) {
            System.err.println("[LandingPageController] MainCore is null !");
        }

        if (root == null) {
            System.err.println("[LandingPageController] root is null !");
        }
    }

    @FXML
    private void login() {
        if (core != null) {
            core.showLoginView();
        } else {
            System.err.println("[LandingPageController] core is null in login()");
        }
    }

    @FXML
    private void signup() {
        if (core != null) {
            core.showSignupView();
        } else {
            System.err.println("[LandingPageController] core is null in signup()");
        }
    }

    @FXML
    private void importProfile() {
        System.out.println("Import clicked");
    }

    @FXML
    private void showFeatures() {
        System.out.println("Show features");
    }

    @FXML
    private void showAbout() {
        System.out.println("Show about");
    }

    @FXML
    private void showContact() {
        System.out.println("Show contact");
    }
}