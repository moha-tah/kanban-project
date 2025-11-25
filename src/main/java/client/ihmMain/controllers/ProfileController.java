package client.ihmMain.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import common.dataClasses.User;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;

public class ProfileController {

    @FXML
    private Label profileName;

    @FXML
    private Label profileUsername;

    @FXML
    private ImageView profileAvatar;

    @FXML
    private Label kanbansCreated;

    @FXML
    private Label collaborations;

    @FXML
    private Label memberSince;

    @FXML
    private GridPane kanbansGrid;

    private MainCore core;

    public void setCore(MainCore core) {
        this.core = core;
    }
    
    public void setUser(LightUser currentUser) {
        // Infos utilisateur
        profileName.setText(currentUser.getUsername());
        profileUsername.setText("@" + currentUser.getUsername());
        kanbansCreated.setText(String.valueOf(currentUser.getMyKanban().size()));
        collaborations.setText("0"); 
        memberSince.setText("📅 Membre depuis le " + currentUser.getBirthDate().toString());

        // Avatar
        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            profileAvatar.setImage(new Image(currentUser.getAvatar()));
        }

        // Afficher les Kanbans
        kanbansGrid.getChildren().clear();
        int row = 0, col = 0;
        for (Kanban k : currentUser.getMyKanban()) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: linear-gradient(#6ee7b7, #3b82f6); -fx-padding: 25; -fx-background-radius: 15;");

            Label title = new Label(k.getTitle());
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Label info = new Label("Creator: ");
            info.setStyle("-fx-font-size: 13px;");

            card.getChildren().addAll(title, info);

            kanbansGrid.add(card, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }
}
