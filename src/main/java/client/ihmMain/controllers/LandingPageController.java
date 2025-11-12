package client.ihmMain.controllers;

import java.io.IOException;
import java.net.URL;

import client.ihmMain.MainCore;

import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LandingPageController {

    private MainCore core;

    @FXML
    private BorderPane root;

    @FXML
    private Button logoButton;

    @FXML
    private void initialize() {
        // Vérifie que root est injecté
        if (root == null) {
            System.out.println("root is null !");
            return;
        }
    }


    // @FXML
    // private void goHomeOrLanding(MouseEvent event) throws IOException {
    //     // Choix du fichier FXML selon si l'utilisateur est connecté
    //     String fxmlFile = (core.getMe() != null) ? "/home.fxml" : "/landing.fxml";

    //     // Chargement du FXML
    //     Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

    //     // Récupération de la scène et du stage actuel
    //     Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    //     // Création de la nouvelle scène avec le CSS
    //     Scene scene = new Scene(root);

    //     // Application de la scène au stage
    //     stage.setScene(scene);
    // }

    // @FXML
    // private void getStarted() throws IOException {
    //     LightUser currentUser = core.getMe();

    //     if (currentUser == null) {
    //         // Pas d'utilisateur connecté → ouvrir login
    //         openPage("/login.fxml");
    //     } else {
    //         // Utilisateur existant → ouvrir home
    //         openPage("/home.fxml");
    //     }
    // }

    private void openPage(String fxmlFile) {
        try {
            // getResource renvoie URL absolu, null si le fichier n'existe pas
            URL fxmlLocation = getClass().getResource(fxmlFile);
            if (fxmlLocation == null) {
                System.out.println("FXML introuvable : " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent newRoot = loader.load();

            Stage stage = (Stage) root.getScene().getWindow();
            Scene scene = new Scene(newRoot);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Impossible de charger " + fxmlFile);
        }
    }


    @FXML
    private void login() {
        openPage("/login.fxml");
    }

    @FXML
    private void signup() {
        openPage("/signup.fxml");
    }

    @FXML
    private void importProfile() { System.out.println("Import clicked"); }

    @FXML
    private void showFeatures() { System.out.println("Show features"); }

    @FXML
    private void showAbout() { System.out.println("Show about"); }

    @FXML
    private void showContact() { System.out.println("Show contact"); }
}