package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private ImageView profilePic;

    @FXML
    private void initialize() {
        MainCore core = MainApp.getCore();
        if (core == null) return;

        LightUser me = core.getMe();
        if (me == null) return;

        String avatarPath = me.getAvatar();   // tu viens d’ajouter ce champ dans LightUser

        if (avatarPath == null || avatarPath.isBlank()) {
            // on garde l’image par défaut définie dans menu.fxml
            return;
        }

        try {
            File file = new File(avatarPath);
            if (!file.exists()) {
                System.out.println("Fichier avatar introuvable: " + avatarPath);
                return;
            }

            String url = file.toURI().toString();  // file:/...
            profilePic.setImage(new Image(url, true));
        } catch (Exception e) {
            System.out.println("Impossible de charger l'avatar : " + avatarPath);
            profilePic.setImage(null); // ou garder l’image par défaut
        }
    }

    @FXML
    private void handleProfileClick() throws IOException {
        switchScene("/profile.fxml", "Mon Profil", profilePic);
    }

    @FXML
    private void handleHome() {}

    @FXML
    private void handleNotif() {
        HomeViewController.handleNotif();
    }

    @FXML
    private void handleLogout() {}

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}