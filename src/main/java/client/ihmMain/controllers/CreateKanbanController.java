package client.ihmMain.controllers;

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
    @FXML private Button addColumnButton;
    @FXML private VBox columnsContainer;
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

        // Couleurs par défaut
        colorPicker1.setValue(Color.web("#4D96FF"));  // bleu
        colorPicker2.setValue(Color.web("#FFA500"));  // orange
        colorPicker3.setValue(Color.web("#FF69B4"));  // rose
        colorPicker4.setValue(Color.web("#32CD32"));  // vert

        // Ajouter les colonnes FXML au listage columnInputs
        columnInputs.add(new ColumnInput(
                (TextField) ((HBox) columnsContainer.getChildren().get(0)).getChildren().get(0),
                colorPicker1
        ));
        columnInputs.add(new ColumnInput(
                (TextField) ((HBox) columnsContainer.getChildren().get(1)).getChildren().get(0),
                colorPicker2
        ));
        columnInputs.add(new ColumnInput(
                (TextField) ((HBox) columnsContainer.getChildren().get(2)).getChildren().get(0),
                colorPicker3
        ));
        columnInputs.add(new ColumnInput(
                (TextField) ((HBox) columnsContainer.getChildren().get(3)).getChildren().get(0),
                colorPicker4
        ));
    }

    @FXML
    private void handleAddColumn() {
        addColumnInput();
    }

    private void addColumnInput() {
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
        String description = descriptionField.getText().trim();
        String visibility = visibilityChoice.getValue();

        if (title.isEmpty() || visibility == null) {
            showAlert("Missing fields", "Please fill in all required fields.");
            return;
        }

        // Créer un Kanban complet (pas un LightKanban)
        Kanban newKanban = new Kanban(UUID.randomUUID(), title);

        // Ajouter les colonnes définies par l’utilisateur
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

        // Ajout au MainCore
        if (mainCore != null) {
            mainCore.addOrReplaceKanban(newKanban);

            if (mainCore.getCommPort() != null)
                mainCore.getCommPort().notifyEditions(newKanban);

            System.out.println("✅ Kanban created: " + title);
            System.out.println("🧩 Columns: " + newKanban.getTaskColumn().keySet());
        }

        // Ferme la fenêtre
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    // Utils internes

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static class ColumnInput {
        TextField nameField;
        ColorPicker colorPicker;

        ColumnInput(TextField nameField, ColorPicker colorPicker) {
            this.nameField = nameField;
            this.colorPicker = colorPicker;
        }
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}