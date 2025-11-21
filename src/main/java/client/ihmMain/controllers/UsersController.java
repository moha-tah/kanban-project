package client.ihmMain.controllers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UsersController {

    @FXML
    private VBox usersContainer;

    private static final Logger LOGGER = Logger.getLogger(UsersController.class.getName());

    private MainCore core;

    // Setter pour injecter MainCore
    public void setCore(MainCore core) {
        this.core = core;
    }

    public void loadDummyUsers() {
        LOGGER.info("👥 Loading dummy users...");

        if (core == null) {
            LOGGER.severe("❌ MainCore n'est pas initialisé !");
            return;
        }

        List<LightUser> users = core.getUsersSnapshot();
        LOGGER.info("Users: " + users);

        //pas d'avatar pour l'instant
        for (LightUser user : users) {
            addUser(user.getUsername(), "https://randomuser.me/api/portraits/women/1.jpg");
        }
    }

    private void addUser(String username, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
            Node userCard = loader.load();

            UserCardController controller = loader.getController();
            controller.setUserData(username, imagePath);

            usersContainer.getChildren().add(userCard);
            LOGGER.info("✅ Added user: " + username);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du chargement de user_card.fxml pour l'utilisateur : " + username, e);
        }
    }
}
