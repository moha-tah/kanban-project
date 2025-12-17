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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class CreateKanbanController {

    private MainCore mainCore;

    // Composants de navigation
    @FXML private VBox step1Box;
    @FXML private VBox step2Box;
    @FXML private VBox step3Box;
    @FXML private Label stepLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    // Champs de données
    @FXML private TextField projectNameField;
    @FXML private TextArea descriptionField;
    @FXML private VBox columnsContainer;
    @FXML private ChoiceBox<String> visibilityChoice;

    // ColorPickers (Injectés depuis le FXML)
    @FXML private ColorPicker colorPicker1;
    @FXML private ColorPicker colorPicker2;
    @FXML private ColorPicker colorPicker3;
    @FXML private ColorPicker colorPicker4;

    private final List<ColumnInput> columnInputs = new ArrayList<>();
    private int currentStep = 1; // 1, 2 ou 3

    public void setMainCore(MainCore mainCore) {
        this.mainCore = mainCore;
    }

    @FXML
    private void initialize() {
        // Init Visibility
        visibilityChoice.getItems().addAll("Private", "Public");
        visibilityChoice.setValue("Private");

        // Init Colors
        colorPicker1.setValue(Color.web("#4D96FF"));
        colorPicker2.setValue(Color.web("#FFA500"));
        colorPicker3.setValue(Color.web("#FF69B4"));
        colorPicker4.setValue(Color.web("#32CD32"));

        // Init Column List (Mapping FXML children to Logic)
        // Note: Ces éléments doivent correspondre à l'ordre dans le FXML
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(0)).getChildren().get(0), colorPicker1));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(1)).getChildren().get(0), colorPicker2));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(2)).getChildren().get(0), colorPicker3));
        columnInputs.add(new ColumnInput((TextField) ((HBox) columnsContainer.getChildren().get(3)).getChildren().get(0), colorPicker4));

        updateUI();
    }

    // --- Navigation Logic ---

    @FXML
    private void handleNext() {
        if (validateCurrentStep()) {
            if (currentStep < 3) {
                currentStep++;
                updateUI();
            } else {
                // Step 3 -> Finish
                finalizeCreation();
            }
        }
    }

    @FXML
    private void handleBack() {
        if (currentStep > 1) {
            currentStep--;
            updateUI();
        }
    }

    private void updateUI() {
        // 1. Gérer la visibilité des VBox
        step1Box.setVisible(currentStep == 1);
        step2Box.setVisible(currentStep == 2);
        step3Box.setVisible(currentStep == 3);

        // 2. Mettre à jour header et boutons
        stepLabel.setText("Step " + currentStep + " of 3");
        progressBar.setProgress(currentStep / 3.0);

        // Gestion bouton Back
        btnBack.setVisible(currentStep > 1);

        // Gestion bouton Next/Create
        if (currentStep == 3) {
            btnNext.setText("Create Kanban");
            btnNext.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 30;");
        } else {
            btnNext.setText("Next");
            btnNext.setStyle("-fx-background-color: linear-gradient(to right, #6A11CB, #2575FC); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 30;");
        }
    }

    private boolean validateCurrentStep() {
        if (currentStep == 1 && projectNameField.getText().trim().isEmpty()) {
            showAlert("Name Required", "Please enter a project name.");
            return false;
        }
        // Step 2 (Columns) is technically always valid since we have defaults,
        // but you could check if list is empty.
        return true;
    }

    // --- Business Logic (Création) ---

    private void finalizeCreation() {
        String title = projectNameField.getText().trim();
        String visibility = visibilityChoice.getValue();

        // Récupération User
        User currentUser = null;
        if (mainCore != null) {
            var provider = mainCore.getDataClientProvider();
            if (provider != null) currentUser = provider.getMyModel().getLocalUser();
        }

        if (currentUser == null) {
            showAlert("Error", "User not logged in.");
            return;
        }

        // 1. Création Objet
        List<Access> accessList = new ArrayList<>();
        accessList.add(new Access(currentUser, Role.MODIFIER));
        Kanban newKanban = new Kanban(UUID.randomUUID(), title, accessList, visibility, currentUser);

        // 2. Colonnes (LinkedHashMap pour l'ordre)
        LinkedHashMap<Column, List<Task>> orderedColumns = new LinkedHashMap<>();
        int index = 1;
        for (ColumnInput ci : columnInputs) {
            String colName = ci.nameField.getText().trim();
            if (!colName.isEmpty()) {
                Column col = new Column(colName, colorToHex(ci.colorPicker.getValue()), index++);
                orderedColumns.put(col, new ArrayList<>());
            }
        }

        if (orderedColumns.isEmpty()) {
            showAlert("Columns Missing", "Please define at least one column.");
            return;
        }
        newKanban.setTaskColumn(orderedColumns);

        // 3. Sauvegarde
        KanbanCallsDataImplementation.saveKanbanAsJson(newKanban);

        if (mainCore != null) {
            mainCore.addOrReplaceKanban(newKanban);
            try {
                currentUser.addKanban(newKanban);
                mainCore.getDataClientProvider().getToMainImpl().saveUser();
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(CreateKanbanController.class.getName())
                        .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", e);
            }

            if (mainCore.getCommPort() != null) {
                mainCore.getCommPort().sendNewKanban(newKanban);
            }
        }

        closeWindow();
    }

    // --- Helpers ---

    @FXML
    private void handleAddColumn() {
        HBox newColumn = new HBox(10);
        newColumn.setAlignment(Pos.CENTER_LEFT);

        TextField columnName = new TextField();
        columnName.setPromptText("Column name");
        columnName.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-padding: 6;");
        HBox.setHgrow(columnName, javafx.scene.layout.Priority.ALWAYS);

        ColorPicker colorPicker = new ColorPicker(Color.web("#FF6B6B"));
        colorPicker.setStyle("-fx-color-label-visible: false;");

        newColumn.getChildren().addAll(columnName, colorPicker);
        columnsContainer.getChildren().add(newColumn);

        columnInputs.add(new ColumnInput(columnName, colorPicker));
    }

    private void closeWindow() {
        Stage stage = (Stage) btnNext.getScene().getWindow();
        stage.close();
        if (mainCore != null) mainCore.showHomeView();
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