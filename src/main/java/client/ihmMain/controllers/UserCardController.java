package client.ihmMain.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class UserCardController {
    @FXML private HBox userCardRoot;
    @FXML private ImageView profilePic;
    @FXML private Label usernameLabel;
    private String username;

    @FXML
    private void initialize() {
        makeProfilePictureRound();
        userCardRoot.setOnMouseClicked(event -> openProfile());
    }

    public String getUserName() {
        return username;
    }

    public void setUserData(String username, String imageUrl) {
        this.username = username;
        usernameLabel.setText(username);
        try {
            Image img = new Image(imageUrl, true);
            profilePic.setImage(img);
        } catch (Exception e) {
            System.err.println(" Erreur chargement image pour " + username);
        }
    }

    private void makeProfilePictureRound() {
        double radius = 19;
        Circle clip = new Circle(radius, radius, radius);
        profilePic.setClip(clip);
    }

    private void openProfile() {
        System.out.println("👤 Ouverture du profil de : " + username);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userCardRoot.getScene().getWindow();
            stage.setTitle("Profil de " + username);
            stage.setScene(new Scene(root, 1280, 720));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
