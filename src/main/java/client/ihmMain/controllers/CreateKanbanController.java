package client.ihmMain.controllers;

import client.data.KanbanCallsDataImplementation;
import client.ihmMain.MainCore;
import common.dataClasses.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.LinkedHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateKanbanController {

    private MainCore mainCore;

    @FXML private TextField projectNameField;
    @FXML private TextArea descriptionField;
    @FXML private ChoiceBox<String> visibilityChoice;
    @FXML private Button cancelButton;
    @FXML private Button createButton;
    @FXML private VBox columnsContainer;

    // ColorPickers définis dans le FXML
    @FXML private ColorPicker colorPicker1;
    @FXML private ColorPicker colorPicker2;
    @FXML private ColorPicker colorPicker3;
    @FXML private ColorPicker colorPicker4;

    private final List<ColumnInput> columnInputs = new ArrayList<>();

    public void setMainCore(MainCore mainCore) {
        this.mainCore = mainCore;
    }

    @FXML
    private void initialize() {
        visibilityChoice.getItems().addAll("Private", "Public");
        visibilityChoice.setValue("Private");

        // Initialisation des couleurs par défaut
        colorPicker1.setValue(Color.web("#4D96FF"));
        colorPicker2.setValue(Color.web("#FFA500"));
        colorPicker3.setValue(Color.web("#FF69B4"));
        colorPicker4.setValue(Color.web("#32CD32"));

        // Liaison des champs FXML existants à notre liste logique
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(0)).getChildren().get(0), colorPicker1));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(1)).getChildren().get(0), colorPicker2));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(2)).getChildren().get(0), colorPicker3));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(3)).getChildren().get(0), colorPicker4));
    }

    @FXML
    private void handleAddColumn() {
        HBox newColumn = new HBox(10);
        newColumn.setAlignment(Pos.CENTER_LEFT);

        TextField columnName = new TextField();
        columnName.setPromptText("Column name");
        columnName.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-padding: 6;");

        ColorPicker colorPicker = new ColorPicker(Color.web("#FF6B6B"));

        newColumn.getChildren().addAll(columnName, colorPicker);
        columnsContainer.getChildren().add(newColumn);

        columnInputs.add(new ColumnInput(columnName, colorPicker));
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCreate() {
        String title = projectNameField.getText().trim();
        String visibility = visibilityChoice.getValue();

        if (title.isEmpty() || visibility == null) {
            showAlert("Missing fields", "Please fill in all required fields.");
            return;
        }

        // Récupération de l'utilisateur
        User currentUser = null;
        if (mainCore != null) {
            var provider = mainCore.getDataClientProvider();
            if (provider != null) {
                var model = provider.getMyModel();
                currentUser = model.getLocalUser();
            }
        }

        if (currentUser == null) {
            showAlert("Error", "Cannot create kanban: user not logged in.");
            return;
        }

        // 1. Instanciation du Kanban
        Kanban newKanban = new Kanban(UUID.randomUUID(), title, visibility, currentUser);

        // 2. Configuration des colonnes AVEC ORDRE GARANTI (1 -> 2 -> 3 -> 4)
        // LinkedHashMap est OBLIGATOIRE pour conserver l'ordre d'insertion dans le JSON
        java.util.LinkedHashMap<Column, List<Task>> orderedColumns = new java.util.LinkedHashMap<>();

        // --- BOUCLE STANDARD (0 vers Fin) ---
        // Comme votre liste 'columnInputs' est déjà ordonnée visuellement dans le FXML
        // (To Do est en haut, Done en bas), on les parcourt dans l'ordre naturel.
        int index = 1;
        for (ColumnInput ci : columnInputs) {
            String colName = ci.nameField.getText().trim();

            // On prend toutes les colonnes, même si le nom n'est pas changé (car vous avez des valeurs par défaut)
            // Si le champ est vide, on l'ignore, sinon on l'ajoute.
            if (!colName.isEmpty()) {
                Column col = new Column(colName, colorToHex(ci.colorPicker.getValue()), index++);
                orderedColumns.put(col, new ArrayList<>());
            }
        }

        if (orderedColumns.isEmpty()) {
            showAlert("No columns", "Please define at least one column.");
            return;
        }

        newKanban.setTaskColumn(orderedColumns);

        // 3. Sauvegarde
        // GSON va écrire le JSON en suivant l'ordre de la LinkedHashMap (To do en premier)
        KanbanCallsDataImplementation.saveKanbanAsJson(newKanban);

        if (mainCore != null) {
            mainCore.addOrReplaceKanban(newKanban);

            try {
                currentUser.addKanban(newKanban);
                var provider = mainCore.getDataClientProvider();
                if (provider != null) {
                    provider.getToMainImpl().saveUser();
                    System.out.println("Utilisateur sauvegardé avec le nouveau Kanban ID.");
                }
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(CreateKanbanController.class.getName())
                        .log(java.util.logging.Level.SEVERE, "Erreur sauvegarde user", e);
            }

            if (mainCore.getCommPort() != null) {
                mainCore.getCommPort().sendNewKanban(newKanban);
            }
        }

        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();

        if (mainCore != null) {
            mainCore.showHomeView();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private static class ColumnInput {
        TextField nameField;
        ColorPicker colorPicker;

        ColumnInput(TextField nameField, ColorPicker colorPicker) {
            this.nameField = nameField;
            this.colorPicker = colorPicker;
        }
    }
}