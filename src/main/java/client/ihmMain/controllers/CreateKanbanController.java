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

/**
 * Contrôleur du formulaire de création de kanban.
 * 
 * Ce contrôleur gère un formulaire en 3 étapes pour créer un nouveau kanban :
 * 1) Informations de base (nom, description, visibilité)
 * 2) Configuration des colonnes (nom et couleur)
 * 3) Finalisation et création
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MainCore
 * @see HomeViewController
 */
public class CreateKanbanController {

    /**
     * Cœur de l'application principale.
     */
    private MainCore mainCore;

    /**
     * Conteneur de l'étape 1 (informations de base).
     */
    @FXML private VBox step1Box;
    
    /**
     * Conteneur de l'étape 2 (colonnes).
     */
    @FXML private VBox step2Box;
    
    /**
     * Conteneur de l'étape 3 (finalisation).
     */
    @FXML private VBox step3Box;
    
    /**
     * Label affichant l'étape actuelle.
     */
    @FXML private Label stepLabel;
    
    /**
     * Barre de progression indiquant l'avancement.
     */
    @FXML private ProgressBar progressBar;
    
    /**
     * Bouton pour revenir à l'étape précédente.
     */
    @FXML private Button btnBack;
    
    /**
     * Bouton pour passer à l'étape suivante ou créer le kanban.
     */
    @FXML private Button btnNext;

    /**
     * Champ de saisie du nom du projet.
     */
    @FXML private TextField projectNameField;
    
    /**
     * Zone de texte pour la description du projet.
     */
    @FXML private TextArea descriptionField;
    
    /**
     * Conteneur pour les champs de colonnes.
     */
    @FXML private VBox columnsContainer;
    
    /**
     * Choix de la visibilité (Private/Public).
     */
    @FXML private ChoiceBox<String> visibilityChoice;

    /**
     * Sélecteurs de couleur pour les colonnes (injectés depuis le FXML).
     */
    @FXML private ColorPicker colorPicker1;
    @FXML private ColorPicker colorPicker2;
    @FXML private ColorPicker colorPicker3;
    @FXML private ColorPicker colorPicker4;

    /**
     * Liste des entrées de colonnes (nom + couleur).
     */
    private final List<ColumnInput> columnInputs = new ArrayList<>();
    
    /**
     * Étape actuelle du formulaire (1, 2 ou 3).
     */
    private int currentStep = 1;

    /**
     * Définit le cœur de l'application principale.
     * 
     * @param mainCore Le cœur de l'application (ne doit pas être null)
     */
    public void setMainCore(MainCore mainCore) {
        this.mainCore = mainCore;
    }

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode configure les valeurs par défaut pour la visibilité,
     * les couleurs des colonnes, et mappe les éléments FXML aux objets logiques.
     */
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

    /**
     * Gère le passage à l'étape suivante ou la finalisation.
     * 
     * Cette méthode valide l'étape actuelle, puis passe à l'étape suivante
     * ou finalise la création si on est à l'étape 3.
     */
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

    /**
     * Gère le retour à l'étape précédente.
     */
    @FXML
    private void handleBack() {
        if (currentStep > 1) {
            currentStep--;
            updateUI();
        }
    }

    /**
     * Met à jour l'interface utilisateur selon l'étape actuelle.
     * 
     * Cette méthode gère la visibilité des conteneurs d'étapes, met à jour
     * le label et la barre de progression, et ajuste les boutons.
     */
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

    /**
     * Valide les données de l'étape actuelle.
     * 
     * @return true si l'étape est valide, false sinon
     */
    private boolean validateCurrentStep() {
        if (currentStep == 1 && projectNameField.getText().trim().isEmpty()) {
            showAlert("Name Required", "Please enter a project name.");
            return false;
        }
        // Step 2 (Columns) is technically always valid since we have defaults,
        // but you could check if list is empty.
        return true;
    }

    /**
     * Finalise la création du kanban.
     * 
     * Cette méthode crée l'objet Kanban avec toutes les données saisies,
     * sauvegarde le kanban localement, l'ajoute à l'utilisateur, l'envoie
     * au serveur, puis ferme la fenêtre et retourne à la page d'accueil.
     */
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
        Kanban newKanban = new Kanban(UUID.randomUUID(), title, visibility, currentUser);

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

    /**
     * Gère l'ajout d'une nouvelle colonne dynamiquement.
     * 
     * Cette méthode crée un nouveau champ de saisie avec un sélecteur de couleur
     * et l'ajoute au conteneur et à la liste des entrées.
     */
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

    /**
     * Ferme la fenêtre de création et retourne à la page d'accueil.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnNext.getScene().getWindow();
        stage.close();
        if (mainCore != null) mainCore.showHomeView();
    }

    /**
     * Affiche une alerte à l'utilisateur.
     * 
     * @param title Le titre de l'alerte
     * @param msg Le message de l'alerte
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Convertit une couleur JavaFX en code hexadécimal.
     * 
     * @param color La couleur à convertir (ne doit pas être null)
     * @return Le code couleur hexadécimal (format #RRGGBB)
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Classe interne représentant une entrée de colonne (nom + couleur).
     */
    private static class ColumnInput {
        /**
         * Champ de saisie du nom de la colonne.
         */
        TextField nameField;
        
        /**
         * Sélecteur de couleur de la colonne.
         */
        ColorPicker colorPicker;
        
        /**
         * Constructeur d'une entrée de colonne.
         * 
         * @param nameField Le champ de saisie du nom (ne doit pas être null)
         * @param colorPicker Le sélecteur de couleur (ne doit pas être null)
         */
        ColumnInput(TextField nameField, ColorPicker colorPicker) {
            this.nameField = nameField;
            this.colorPicker = colorPicker;
        }
    }
}