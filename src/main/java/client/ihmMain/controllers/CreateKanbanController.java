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

        // Get the current user as creator
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

        // 1. Instanciation du Kanban with creator
        Kanban newKanban = new Kanban(UUID.randomUUID(), title, visibility, currentUser);

        // 2. Configuration des colonnes
        int index = 1;
        for (ColumnInput ci : columnInputs) {
            String colName = ci.nameField.getText().trim();
            if (!colName.isEmpty()) {
                Column col = new Column(colName, colorToHex(ci.colorPicker.getValue()), index++);
                newKanban.getTaskColumn().put(col, new ArrayList<>());
            }
        }

        if (newKanban.getTaskColumn().isEmpty()) {
            showAlert("No columns", "Please define at least one column.");
            return;
        }

        // 3. Sauvegarde physique du fichier Kanban (JSON)
        // Cela crée le fichier UUID.json dans data/kanbans
        KanbanCallsDataImplementation.saveKanbanAsJson(newKanban);

        if (mainCore != null) {
            // A. Mise à jour de l'affichage immédiat
            mainCore.addOrReplaceKanban(newKanban);

            // B. Liaison avec l'utilisateur et Sauvegarde du User
            try {
                // Add kanban to the user's kanban list in memory
                currentUser.addKanban(newKanban);

                // Save the updated user to disk
                var provider = mainCore.getDataClientProvider();
                if (provider != null) {
                    provider.getToMainImpl().saveUser();
                    System.out.println("Utilisateur sauvegardé avec le nouveau Kanban ID.");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
                java.util.logging.Logger.getLogger(CreateKanbanController.class.getName())
                        .log(java.util.logging.Level.SEVERE, "Erreur traitement createKanban", e);
            }

            // C. Notification réseau
            if (mainCore.getCommPort() != null) {
                mainCore.getCommPort().sendNewKanban(newKanban);
            }
        }

        // 4. Fermeture et rafraîchissement
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