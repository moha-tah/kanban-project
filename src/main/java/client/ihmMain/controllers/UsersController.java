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

    public void setCore(MainCore core) {
        this.core = core;
    }

    /**
     * Recharge la liste des utilisateurs connectés depuis MainCore
     * et met à jour l'IHM.
     */
    public void refreshUsers() {
        LOGGER.info("👥 Refreshing users list...");

        if (core == null) {
            LOGGER.severe("❌ MainCore n'est pas initialisé dans UsersController !");
            return;
        }
        if (usersContainer == null) {
            LOGGER.severe("❌ usersContainer est null dans UsersController !");
            return;
        }

        usersContainer.getChildren().clear();

        List<LightUser> users = core.getUsersSnapshot();
        LOGGER.info("Users from MainCore: " + users.size());

        for (LightUser user : users) {
            if (user != null) {
                addUser(user);
            }
        }
    }

    /**
     * Ajoute un utilisateur à la vue, à partir d'un LightUser du modèle.
     */
    private void addUser(LightUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
            Node userCard = loader.load();

            UserCardController controller = loader.getController();
            controller.setUserData(user.getUsername(), "");

            usersContainer.getChildren().add(userCard);
            LOGGER.info("✅ Added user to UI: " + user.getUsername());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "❌ Erreur lors du chargement de user_card.fxml pour l'utilisateur : " + user.getUsername(), e);
        }
    }
}