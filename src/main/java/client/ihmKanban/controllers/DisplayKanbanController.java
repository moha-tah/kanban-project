package client.ihmKanban.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import common.dataClasses.LightKanban;


import common.dataClasses.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import common.dataClasses.Kanban;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class DisplayKanbanController  {
    @FXML
    private Label kanbanTitle;  // Le label du titre du Kanban en haut  

    private Kanban kanban;

    /** Appelé par ton corps lorsqu'on charge le Kanban */
    public void setKanban(Kanban kanban) {
        this.kanban = kanban;
        kanbanTitle.setText(kanban.getTitle());
        //loadColumns();
    }

    // ----- POPUP 1 : MENU DE LA TÂCHE -----
    @FXML
    private VBox taskMenuPane;        // le petit menu gris (ADD A USER / SEE USERS / ...)

    // ----- POPUP 2 : CONTENEUR + VARIANTS -----
    @FXML
    private StackPane detailPopup;    // le grand overlay au centre

    @FXML
    private VBox seeUsersPane;        // variant "SEE USERS"
    @FXML
    private VBox addUsersPane;        // variant "ADD USER"
    @FXML
    private VBox editTaskPane;        // variant "EDIT TASK"

    // Champs d'édition de tâche
    @FXML
    private TextField editNameField;
    @FXML
    private TextArea editDescField;

    // Tâche actuellement sélectionnée (la carte VBox sur laquelle on a cliqué les 3 points)
    private VBox currentTaskCard;

    @FXML
    private void initialize() {
        // Au démarrage on cache tous les popups
        if (taskMenuPane != null) taskMenuPane.setVisible(false);
        if (detailPopup != null) detailPopup.setVisible(false);
        hideAllDetailPanes();
    }

    // ----------------------
    // 1) Clic sur "⋮" d'une tâche
    // ----------------------
    @FXML
    private void onTaskMenuClick(MouseEvent event) {
        // Retrouver la VBox qui représente la carte de la tâche
        Node n = (Node) event.getSource();
        while (n != null && !(n instanceof VBox)) {
            n = n.getParent();
        }
        if (n instanceof VBox) {
            currentTaskCard = (VBox) n;
        }

        // Afficher le petit menu de la tâche
        taskMenuPane.setVisible(true);

        // Fermer le popup de détail s'il était ouvert
        detailPopup.setVisible(false);
        hideAllDetailPanes();

        // (optionnel) Pré-remplir les champs d'édition avec le titre / desc de la tâche
        fillEditFieldsFromCurrentTask();
    }

    // ----------------------
    // 2) Boutons du menu
    // ----------------------

    @FXML
    private void onMenuAddUser() {
        showDetailPane(addUsersPane);
    }

    @FXML
    private void onMenuSeeUsers() {
        showDetailPane(seeUsersPane);
    }

    @FXML
    private void onMenuEditTask() {
        // s'assurer que les champs sont pré-remplis
        fillEditFieldsFromCurrentTask();
        showDetailPane(editTaskPane);
    }

    // Afficher un des panneaux de détail (SEE / ADD / EDIT)
    private void showDetailPane(VBox paneToShow) {
        detailPopup.setVisible(true);
        hideAllDetailPanes();
        paneToShow.setVisible(true);
    }

    // Cacher tous les panneaux de détail
    private void hideAllDetailPanes() {
        if (seeUsersPane != null) seeUsersPane.setVisible(false);
        if (addUsersPane != null) addUsersPane.setVisible(false);
        if (editTaskPane != null) editTaskPane.setVisible(false);
    }

    // ----------------------
    // 3) Fermeture des popups
    // ----------------------

    // Bouton "Close" du petit menu
    @FXML
    private void onCloseAllPopups() {
        taskMenuPane.setVisible(false);
        detailPopup.setVisible(false);
        hideAllDetailPanes();
        currentTaskCard = null;
    }

    // Bouton "Close" ou "Cancel" des popups de détail
    @FXML
    private void onCloseDetail() {
        detailPopup.setVisible(false);
        hideAllDetailPanes();
    }

    // ----------------------
    // 4) Sauvegarde d'une tâche modifiée
    // ----------------------
    @FXML
    private void onSaveTask() {
        if (currentTaskCard == null) {
            onCloseDetail();
            return;
        }

        String newName = editNameField.getText();
        String newDesc = editDescField.getText();

        // TODO : ici tu peux mettre à jour le modèle + appeler ton backend
        System.out.println("Sauver tâche : " + newName + " / " + newDesc);

        // (facultatif) mettre à jour l'affichage de la carte directement
        updateCurrentTaskLabels(newName, newDesc);

        onCloseDetail();
    }

    // ----------------------
    // 5) Helpers internes
    // ----------------------

    // Récupère le titre / description de la carte et les met dans les champs d'édition
    private void fillEditFieldsFromCurrentTask() {
        if (currentTaskCard == null) return;

        String title = null;
        String desc = null;

        // On cherche les deux premiers Label de la carte :
        // 1er = titre, 2ème = description
        int count = 0;
        for (Node child : currentTaskCard.getChildren()) {
            if (child instanceof Label) {
                count++;
                if (count == 1) {
                    title = ((Label) child).getText();
                } else if (count == 2) {
                    desc = ((Label) child).getText();
                    break;
                }
            }
        }

        if (editNameField != null && title != null) {
            editNameField.setText(title);
        }
        if (editDescField != null && desc != null) {
            editDescField.setText(desc);
        }
    }

    // Met à jour les labels de la carte avec les nouvelles valeurs
    private void updateCurrentTaskLabels(String newName, String newDesc) {
        if (currentTaskCard == null) return;

        int count = 0;
        for (Node child : currentTaskCard.getChildren()) {
            if (child instanceof Label) {
                count++;
                if (count == 1 && newName != null && !newName.isBlank()) {
                    ((Label) child).setText(newName);
                } else if (count == 2 && newDesc != null && !newDesc.isBlank()) {
                    ((Label) child).setText(newDesc);
                }
            }
        }
    }

    // ----------------------
    // 6) (Optionnel) Gestion des boutons ADD / View / Delete
    // ----------------------
    // Pour l'instant ils n'ont pas de handler spécifique dans le FXML.
    // Tu pourras ajouter :
    //
    // @FXML
    // private void onAddUserClicked(ActionEvent e) { ... }
    //
    // puis mettre onAction="#onAddUserClicked" sur les boutons ADD
    // et faire pareil pour View / Delete.
}
