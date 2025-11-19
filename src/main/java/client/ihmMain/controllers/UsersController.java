package client.ihmMain.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UsersController {

    @FXML
    private VBox usersContainer;

    private static final Logger LOGGER = Logger.getLogger(UsersController.class.getName());

    public void loadDummyUsers() {
        LOGGER.info("👥 Loading dummy users...");
        Object[][] dummyUsers = {
            {"Jenny", "https://randomuser.me/api/portraits/women/1.jpg"},
            {"Mina", "https://randomuser.me/api/portraits/women/65.jpg"},
            {"Thomas", "https://randomuser.me/api/portraits/men/22.jpg"}
        };

        for (Object[] user : dummyUsers) {
            addUser((String) user[0], (String) user[1]);
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
