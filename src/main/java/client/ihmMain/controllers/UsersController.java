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

    public void refreshUsers() {
        if (core == null || usersContainer == null) return;

        usersContainer.getChildren().clear();

        List<LightUser> users = core.getUsersSnapshot();
        for (LightUser user : users) {
            if (user != null && !user.equals(core.getMe())) { // Enlever l'utilisateur actuel 
                addUser(user);
            }
        }
    }

    private void addUser(LightUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
            Node userCard = loader.load();

            UserCardController controller = loader.getController();
            controller.setUserData(user.getUsername(), user.getAvatar());

            usersContainer.getChildren().add(userCard);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur user_card.fxml", e);
        }
    }
}