package client.ihmMain.controllers;

import java.io.File;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserCardController {

    private static final Logger LOGGER = Logger.getLogger(UserCardController.class.getName());

    @FXML
    private Label nameLabel;

    @FXML
    private ImageView avatarImageView;

    public void setUserData(String username, String avatarPath) {
        nameLabel.setText(username);

        if (avatarImageView == null) {
            LOGGER.warning("avatarImageView est null (vérifie fx:id dans user_card.fxml)");
            return;
        }

        // Pas d’avatar → on laisse vide
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarImageView.setImage(null);
            return;
        }

        try {
            File file = new File(avatarPath);
            if (!file.exists()) {
                LOGGER.warning("Fichier avatar introuvable: " + avatarPath);
                avatarImageView.setImage(null);
                return;
            }

            String url = file.toURI().toString();   // file:/Users/...
            Image img = new Image(url, true);
            avatarImageView.setImage(img);
        } catch (Exception e) {
            LOGGER.warning("Impossible de charger l'image avatar: " + avatarPath);
            avatarImageView.setImage(null);
        }
    }
}