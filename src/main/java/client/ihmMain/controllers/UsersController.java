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

/**
 * Contrôleur de la liste des utilisateurs connectés.
 * 
 * Ce contrôleur gère l'affichage de la liste des utilisateurs connectés
 * dans l'interface utilisateur. Il charge dynamiquement des cartes utilisateur
 * depuis le fichier FXML user_card.fxml pour chaque utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see UserCardController
 * @see MainCore
 */
public class UsersController {

    /**
     * Conteneur vertical pour afficher les cartes utilisateur.
     */
    @FXML
    private VBox usersContainer;

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(UsersController.class.getName());

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;

    /**
     * Définit le cœur de l'application principale.
     * 
     * @param core Le cœur de l'application (ne doit pas être null)
     */
    public void setCore(MainCore core) {
        this.core = core;
    }

    /**
     * Rafraîchit la liste des utilisateurs affichés.
     * 
     * Cette méthode récupère la liste des utilisateurs connectés depuis le core,
     * exclut l'utilisateur actuel, et crée une carte pour chaque utilisateur.
     * Si le core ou le conteneur ne sont pas initialisés, la méthode retourne sans action.
     */
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

    /**
     * Ajoute une carte utilisateur au conteneur.
     * 
     * Cette méthode charge le fichier FXML user_card.fxml, initialise son contrôleur
     * avec les données de l'utilisateur, et ajoute la carte au conteneur.
     * 
     * @param user L'utilisateur pour lequel créer la carte (ne doit pas être null)
     */
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