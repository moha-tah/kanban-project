package client.ihmKanban.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.UserCardController;
import common.dataClasses.Column;
import common.dataClasses.CreateColumn;
import common.dataClasses.CreateTask;
import common.dataClasses.DeleteColumn;
import common.dataClasses.DeleteTask;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.ModifyColumn;
import common.dataClasses.ModifyTask;
import common.dataClasses.MoveTask;
import common.dataClasses.Task;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * Contrôleur de l'affichage détaillé d'un kanban.
 * 
 * Ce contrôleur gère l'affichage visuel d'un kanban avec ses colonnes et tâches.
 * Il permet de créer, modifier et supprimer des colonnes et tâches via des popups,
 * de déplacer des tâches entre colonnes, et de gérer les utilisateurs assignés aux tâches.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Initializable
 * @see KanbanViewController
 * @see ManageDisplay
 */
public class DisplayKanbanController implements Initializable {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(DisplayKanbanController.class.getName());
    
    /**
     * Style CSS pour le texte blanc.
     */
    private static final String WHITE_TEXT = "-fx-text-fill: white;";
    
    /**
     * Cœur de l'application Kanban (statique pour accès global).
     */
    private static kanbanCorps corps;

    /**
     * Constructeur par défaut du contrôleur.
     */
    public DisplayKanbanController() {}

    /**
     * Définit le cœur de l'application Kanban (méthode statique).
     * 
     * @param kcorps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public static void setCore(kanbanCorps kcorps) {
        corps = kcorps;
    }

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;

    /**
     * Label affichant le titre du kanban.
     */
    @FXML private Label kanbanTitleLabel;
    
    /**
     * Conteneur horizontal pour les colonnes du kanban.
     */
    @FXML private HBox columnsContainer;
    
    /**
     * Bouton pour ajouter une nouvelle colonne.
     */
    @FXML private Button addColumnButton;

    /**
     * Map associant les IDs de tâches à leurs listes d'utilisateurs assignés.
     */
    private final Map<UUID, List<LightUser>> taskUsers = new HashMap<>();
    
    /**
     * Le kanban actuellement affiché.
     */
    private LightKanban kanban;
    
    /**
     * Liste des colonnes du kanban.
     */
    private List<Column> columns;
    
    /**
     * Liste des tâches à créer lors de l'initialisation.
     */
    private List<CreateTask> taskCreations;
    
    /**
     * Gestionnaire d'affichage pour les opérations de rafraîchissement.
     */
    private ManageDisplay manageDisplay;
    
    /**
     * Popup actuellement affichée (une seule à la fois).
     */
    private Popup currentPopup;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * @param url L'URL du fichier FXML (non utilisé)
     * @param resourceBundle Le ResourceBundle (non utilisé)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        core = MainApp.getCore();
    }

    /**
     * Initialise le tableau kanban avec les données fournies.
     * 
     * Cette méthode stocke les données et déclenche le rendu du kanban.
     * 
     * @param kanban La version légère du kanban à afficher (ne doit pas être null)
     * @param columns La liste des colonnes du kanban (ne doit pas être null)
     * @param taskCreations La liste des tâches à créer (ne doit pas être null)
     * @param manageDisplay Le gestionnaire d'affichage (ne doit pas être null)
     */
    public void initBoard(LightKanban kanban,
                          List<Column> columns,
                          List<CreateTask> taskCreations, ManageDisplay manageDisplay) {
        this.kanban = kanban;
        this.columns = columns;
        this.taskCreations = taskCreations;
        this.manageDisplay = manageDisplay;
        renderKanban();
    }

    /**
     * Gère l'action de retour à la vue principale.
     * 
     * Cette méthode ferme le kanban côté serveur et retourne à la vue d'accueil.
     */
    @FXML
    private void handleBack() {
        corps.getCommPort().closingKanban(kanban, corps.getMe());
        corps.getMainPort().goHomeView();
    }

    /**
     * Rend le kanban dans l'interface utilisateur.
     * 
     * Cette méthode affiche le titre du kanban et crée les nœuds visuels
     * pour chaque colonne avec ses tâches. Les colonnes sont triées par numéro.
     */
    private void renderKanban() {
        if (kanban == null || columns == null) return;

        kanbanTitleLabel.setText(kanban.getTitle());
        columnsContainer.getChildren().clear();
        columns.sort(Comparator.comparingInt(Column::getNumber));

        for (Column col : columns) {
            columnsContainer.getChildren().add(createColumnNode(col));
        }
    }

    /**
     * Crée un nœud visuel pour une colonne du kanban.
     * 
     * Cette méthode crée un VBox stylisé avec le titre de la colonne,
     * un menu d'actions, et toutes les tâches de cette colonne.
     * 
     * @param col La colonne pour laquelle créer le nœud (ne doit pas être null)
     * @return Un VBox représentant visuellement la colonne
     */
    private VBox createColumnNode(Column col) {
        VBox columnBox = new VBox(10);
        columnBox.setPadding(new Insets(10));
        columnBox.setPrefWidth(260);
        String bgColor = col.getColor() != null ? col.getColor() : "#5D8BF4";
        columnBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);

        Label titleLabel = new Label(col.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label menuLabel = new Label("⋮");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
        menuLabel.setOnMouseClicked(e -> onColumnMenuClick(col, menuLabel));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, spacer, menuLabel);

        VBox tasksBox = new VBox(8);
        UUID colId = col.getId();
        if (taskCreations != null) {
            for (CreateTask ct : taskCreations) {
                if (ct.getTargetColumn().equals(colId)) {
                    tasksBox.getChildren().add(createTaskCard(ct.getNewTask()));
                }
            }
        }

        columnBox.getChildren().addAll(header, tasksBox);
        return columnBox;
    }

    /**
     * Crée une carte visuelle pour une tâche.
     * 
     * Cette méthode crée un VBox stylisé avec le titre, la description,
     * un bouton de menu d'actions, et un bouton de changement de statut.
     * 
     * @param task La tâche pour laquelle créer la carte (ne doit pas être null)
     * @return Un VBox représentant visuellement la tâche
     */
    private VBox createTaskCard(Task task) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 6;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);
        Button menuBtn = new Button("⋮");
        menuBtn.setOnAction(e -> onTaskMenuClick(task, menuBtn));
        topRow.getChildren().add(menuBtn);

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-weight: bold;");
        Label desc = new Label(task.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");

        Button statusBtn = new Button("STATUS ▼");
        statusBtn.setStyle("-fx-background-color: #ffa500; -fx-text-fill: white; -fx-font-size: 10;");
        statusBtn.setOnAction(e -> onStatusClick(task, statusBtn));

        card.getChildren().addAll(topRow, title, desc, statusBtn);
        return card;
    }

    /**
     * Ferme le popup actuellement affiché s'il existe.
     */
    private void closeCurrentPopup() {
        if (currentPopup != null && currentPopup.isShowing()) currentPopup.hide();
        currentPopup = null;
    }

    /**
     * Crée un bouton de menu stylisé.
     * 
     * @param text Le texte du bouton (ne doit pas être null)
     * @param bgColor La couleur de fond (ne doit pas être null)
     * @param textColor La couleur du texte (ne doit pas être null)
     * @return Un bouton stylisé avec les paramètres fournis
     */
    private Button createMenuButton(String text, String bgColor, String textColor) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 4 12 4 12;"
        );
        return b;
    }

    /**
     * Crée un popup de base avec le style par défaut.
     * 
     * Le popup est configuré pour se fermer automatiquement et se positionner
     * automatiquement. Il a un style avec dégradé violet/rose.
     * 
     * @return Un popup configuré avec le style de base
     */
    private Popup createBasePopup() {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        VBox box = new VBox(6);
        box.setPadding(new Insets(8));
        box.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #8a2be2, #f080ff);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #00000040;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, #00000055, 10, 0.2, 0, 2);"
        );
        popup.getContent().add(box);
        return popup;
    }

    /**
     * Affiche un popup près d'un nœud d'ancrage.
     * 
     * Cette méthode ferme tout popup existant, stocke le nouveau popup,
     * et l'affiche à côté du nœud d'ancrage.
     * 
     * @param popup Le popup à afficher (ne doit pas être null)
     * @param anchor Le nœud près duquel afficher le popup (ne doit pas être null)
     */
    private void showPopupNearNode(Popup popup, Node anchor) {
        closeCurrentPopup();
        currentPopup = popup;
        Bounds b = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor.getScene().getWindow(), b.getMaxX() + 4, b.getMinY());
    }

    /**
     * Gère le clic sur le bouton d'ajout de colonne.
     * 
     * Affiche un popup pour créer une nouvelle colonne.
     */
    @FXML
    private void onAddColumnButtonClick() {
        if (addColumnButton != null) showAddColumnPopup(addColumnButton);
    }

    /**
     * Ajoute un bouton d'action dans un popup.
     * 
     * Le bouton exécute l'action fournie puis ferme le popup.
     * 
     * @param box Le conteneur VBox du popup (ne doit pas être null)
     * @param text Le texte du bouton (ne doit pas être null)
     * @param bgColor La couleur de fond du bouton (ne doit pas être null)
     * @param textColor La couleur du texte du bouton (ne doit pas être null)
     * @param action L'action à exécuter lors du clic (ne doit pas être null)
     */
    private void addPopupButton(VBox box, String text, String bgColor, String textColor, Runnable action) {
        Button btn = createMenuButton(text, bgColor, textColor);
        btn.setOnAction(e -> {
            action.run();
            closeCurrentPopup();
        });
        box.getChildren().add(btn);
    }

    /**
     * Gère le clic sur le menu d'une colonne.
     * 
     * Affiche un popup avec les actions disponibles pour la colonne :
     * ajouter une tâche, éditer la colonne, ou supprimer la colonne.
     * 
     * @param col La colonne concernée (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void onColumnMenuClick(Column col, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        addPopupButton(box, "ADD TASK", "#ff8c1a", "black", () -> showAddTaskPopup(col, anchorNode));
        addPopupButton(box, "EDIT COLUMN", "#c0c0ff", "black", () -> showEditColumnPopup(col, anchorNode));
        addPopupButton(box, "DELETE COLUMN", "#ff6666", "black", () -> {
            LOGGER.log(Level.INFO, "DELETE COLUMN : {0}", col.getTitle());
            corps.getCommPort().sendRequestModification(corps.getMe(), new DeleteColumn(col.getId()));
        });

        showPopupNearNode(popup, anchorNode);
    }

    /**
     * Gère le clic sur le menu d'une tâche.
     * 
     * Affiche un popup avec les actions disponibles pour la tâche :
     * ajouter un utilisateur, voir les utilisateurs, éditer la tâche, ou supprimer la tâche.
     * 
     * @param task La tâche concernée (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void onTaskMenuClick(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        addPopupButton(box, "ADD USER", "#ff8c1a", "black", () -> showAddUserToTaskPopup(task, anchorNode));
        addPopupButton(box, "SEE USERS", "#c0c0ff", "black", () -> showTaskUsersPopup(task, anchorNode));
        addPopupButton(box, "EDIT TASK", "#d0d0d0", "black", () -> showEditTaskPopup(task, anchorNode));
        addPopupButton(box, "DELETE TASK", "#ff6666", "black", () -> {
            LOGGER.log(Level.INFO, "DELETE TASK : {0}", task.getTitle());
            corps.getCommPort().sendRequestModification(corps.getMe(), new DeleteTask(task.getId()));
        });

        showPopupNearNode(popup, anchorNode);
    }

    /**
     * Gère le clic sur le bouton de statut d'une tâche.
     * 
     * Affiche un popup listant toutes les colonnes disponibles pour permettre
     * de déplacer la tâche vers une autre colonne.
     * 
     * @param task La tâche concernée (ne doit pas être null)
     * @param statusBtn Le bouton de statut cliqué (ne doit pas être null)
     */
    private void onStatusClick(Task task, Button statusBtn) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        if (columns == null || columns.isEmpty()) {
            Label empty = new Label("No columns available.");
            empty.setStyle(WHITE_TEXT);
            box.getChildren().add(empty);
            showPopupNearNode(popup, statusBtn);
            return;
        }

        for (Column col : columns) {
            addPopupButton(box, col.getTitle(), col.getColor() != null ? col.getColor() : "#5D8BF4", "white", () -> {
                LOGGER.log(Level.INFO, "Change status of task ''{0}'' to column ''{1}''", new Object[]{task.getTitle(), col.getTitle()});
                statusBtn.setText(col.getTitle() + " ▼");
                corps.getCommPort().sendRequestModification(corps.getMe(), new MoveTask(task.getId(), col.getId()));
            });
        }

        showPopupNearNode(popup, statusBtn);
    }

    /**
     * Affiche un popup pour ajouter une nouvelle colonne.
     * 
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showAddColumnPopup(Node anchorNode) {
        showGenericColumnPopup(anchorNode, "ADD COLUMN", null, (title, color) -> {
            Column col = new Column(title, color);
            corps.getCommPort().sendRequestModification(corps.getMe(), new CreateColumn(col));
            LOGGER.log(Level.INFO, "Nouvelle colonne cr\u00e9\u00e9e : {0}", title);
        });
    }

    /**
     * Affiche un popup pour éditer une colonne existante.
     * 
     * @param col La colonne à éditer (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showEditColumnPopup(Column col, Node anchorNode) {
        showGenericColumnPopup(anchorNode, "EDIT COLUMN", col, (title, color) -> {
            col.setTitle(title);
            col.setColor(color);
            corps.getCommPort().sendRequestModification(corps.getMe(), new ModifyColumn(col));
            renderKanban();
            LOGGER.log(Level.INFO, "Colonne modifi\u00e9e : {0}", title);
        });
    }

    /**
     * Affiche un popup générique pour créer ou éditer une colonne.
     * 
     * Ce popup permet de saisir le nom et la couleur de la colonne.
     * 
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     * @param popupTitle Le titre du popup (ne doit pas être null)
     * @param colToEdit La colonne à éditer (null pour création)
     * @param onSubmit Le callback appelé lors de la soumission avec (titre, couleur)
     */
    private void showGenericColumnPopup(Node anchorNode, String popupTitle, Column colToEdit,
                                        BiConsumer<String, String> onSubmit) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label(popupTitle);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label nameLabel = new Label("COLUMN NAME");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        TextField nameField = new TextField();
        if (colToEdit != null) nameField.setText(colToEdit.getTitle());

        Label colorLabel = new Label("COLOR");
        colorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        ComboBox<String> colorCombo = new ComboBox<>();
        colorCombo.setItems(FXCollections.observableArrayList("BLUE", "GREEN", "ORANGE", "RED", "PURPLE"));
        colorCombo.getSelectionModel().select(colToEdit != null ? mapColorToName(colToEdit.getColor()) : "BLUE");

        Button addBtn = new Button(colToEdit != null ? "EDIT" : "ADD");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("-fx-background-color: #3cbc4c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12 6 12;");

        addBtn.setOnAction(e -> {
            String colTitle = nameField.getText().trim();
            String colorCode = mapNameToColor(colorCombo.getSelectionModel().getSelectedItem());
            if (!colTitle.isEmpty()) onSubmit.accept(colTitle, colorCode);
            popup.hide();
        });

        box.getChildren().addAll(title, nameLabel, nameField, colorLabel, colorCombo, addBtn);
        showPopupNearNode(popup, anchorNode);
    }

    /**
     * Convertit un code couleur hexadécimal en nom de couleur.
     * 
     * @param color Le code couleur hexadécimal (peut être null)
     * @return Le nom de la couleur correspondante, ou "BLUE" par défaut
     */
    private String mapColorToName(String color) {
        if (color == null) return "BLUE";
        return switch (color.toUpperCase()) {
            case "#5D8BF4", "BLUE" -> "BLUE";
            case "#4CAF50", "GREEN" -> "GREEN";
            case "#FF8C1A", "ORANGE" -> "ORANGE";
            case "#FF6666", "RED" -> "RED";
            case "#8A2BE2", "PURPLE" -> "PURPLE";
            default -> "BLUE";
        };
    }

    /**
     * Convertit un nom de couleur en code couleur hexadécimal.
     * 
     * @param name Le nom de la couleur (peut être null)
     * @return Le code couleur hexadécimal correspondant, ou "#5D8BF4" (bleu) par défaut
     */
    private String mapNameToColor(String name) {
        if (name == null) return "#5D8BF4";
        return switch (name.toUpperCase()) {
            case "BLUE" -> "#5D8BF4";
            case "GREEN" -> "#4CAF50";
            case "ORANGE" -> "#ff8c1a";
            case "RED" -> "#ff6666";
            case "PURPLE" -> "#8a2be2";
            default -> "#5D8BF4";
        };
    }

    /**
     * Affiche un popup pour ajouter une nouvelle tâche à une colonne.
     * 
     * @param col La colonne dans laquelle ajouter la tâche (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showAddTaskPopup(Column col, Node anchorNode) {
        showGenericTaskPopup("ADD TASK", null, col, anchorNode, (taskTitle, taskDesc) -> {
            Task tache = new Task(taskTitle, taskDesc);
            corps.getCommPort().sendRequestModification(corps.getMe(), new CreateTask(tache, col.getId()));
            LOGGER.log(Level.INFO, "Nouvelle t\u00e2che cr\u00e9\u00e9e : {0}", taskTitle);
        });
    }

    /**
     * Affiche un popup pour éditer une tâche existante.
     * 
     * @param task La tâche à éditer (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showEditTaskPopup(Task task, Node anchorNode) {
        showGenericTaskPopup("EDIT TASK", task, null, anchorNode, (taskTitle, taskDesc) -> {
            task.setTitle(taskTitle);
            task.setDescription(taskDesc);
            corps.getCommPort().sendRequestModification(corps.getMe(), new ModifyTask(task));
            renderKanban();
            LOGGER.log(Level.INFO, "T\u00e2che modifi\u00e9e : {0}", taskTitle);
        });
    }

    /**
     * Affiche un popup générique pour créer ou éditer une tâche.
     * 
     * Ce popup permet de saisir le titre et la description de la tâche.
     * 
     * @param popupTitle Le titre du popup (ne doit pas être null)
     * @param taskToEdit La tâche à éditer (null pour création)
     * @param col La colonne cible pour une nouvelle tâche (peut être null si édition)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     * @param onSubmit Le callback appelé lors de la soumission avec (titre, description)
     */
    private void showGenericTaskPopup(String popupTitle, Task taskToEdit, Column col, Node anchorNode,
                                      BiConsumer<String, String> onSubmit) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label(popupTitle);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label nameLabel = new Label("TASK TITLE");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        TextField nameField = new TextField();
        if (taskToEdit != null) nameField.setText(taskToEdit.getTitle());

        Label descLabel = new Label("DESCRIPTION");
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        if (taskToEdit != null) descArea.setText(taskToEdit.getDescription());

        Button addBtn = new Button(taskToEdit != null ? "EDIT" : "ADD");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("-fx-background-color: #3cbc4c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 12 6 12;");
        addBtn.setOnAction(e -> {
            String taskTitle = nameField.getText().trim();
            String taskDesc = descArea.getText().trim();
            if (!taskTitle.isEmpty()) onSubmit.accept(taskTitle, taskDesc);
            popup.hide();
        });

        box.getChildren().addAll(title, nameLabel, nameField, descLabel, descArea, addBtn);
        showPopupNearNode(popup, anchorNode);
    }

    /**
     * Affiche un popup pour ajouter un utilisateur à une tâche.
     * 
     * @param task La tâche concernée (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showAddUserToTaskPopup(Task task, Node anchorNode) {
        showUsersPopup(task, anchorNode, true);
    }

    /**
     * Affiche un popup listant les utilisateurs assignés à une tâche.
     * 
     * @param task La tâche concernée (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     */
    private void showTaskUsersPopup(Task task, Node anchorNode) {
        showUsersPopup(task, anchorNode, false);
    }

    /**
     * Affiche un popup générique pour gérer les utilisateurs d'une tâche.
     * 
     * En mode ajout, affiche la liste des utilisateurs connectés avec un bouton
     * pour les ajouter. En mode affichage, affiche uniquement les utilisateurs
     * déjà assignés à la tâche.
     * 
     * @param task La tâche concernée (ne doit pas être null)
     * @param anchorNode Le nœud d'ancrage pour positionner le popup (ne doit pas être null)
     * @param isAddMode true pour le mode ajout, false pour le mode affichage
     */
    private void showUsersPopup(Task task, Node anchorNode, boolean isAddMode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label(isAddMode ? "CONNECTED USERS" : "TASK USERS");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        box.getChildren().add(title);

        List<LightUser> users = isAddMode ? core.getUsersSnapshot() : taskUsers.getOrDefault(task.getId(), Collections.emptyList());
        if (users == null || users.isEmpty()) {
            Label empty = new Label(isAddMode ? "No connected users." : "No users on this task.");
            empty.setStyle(WHITE_TEXT);
            box.getChildren().add(empty);
        } else {
            for (LightUser user : users) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
                    Node userCard = loader.load();
                    UserCardController controller = loader.getController();
                    controller.setUserData(user.getUsername(), user.getAvatar());

                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.getChildren().add(userCard);

                    if (isAddMode) {
                        Button addBtn = new Button("ADD");
                        addBtn.setOnAction(e -> {
                            //corps.getCommPort().sendRequestModification(corps.getMe(), new AddUserToTask(task.getId(), user.getId()));
                            popup.hide();
                        });
                        row.getChildren().add(addBtn);
                    }

                    box.getChildren().add(row);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Cannot load user_card.fxml", e);
                }
            }
        }

        showPopupNearNode(popup, anchorNode);
    }

}
