package client.ihmKanban.controllers;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.UserCardController;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.CreateColumn;
import common.dataClasses.DeleteColumn;
import common.dataClasses.DeleteTask;
import common.dataClasses.ModifyColumn;
import common.dataClasses.ModifyTask;
import common.dataClasses.Modification;

public class DisplayKanbanController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DisplayKanbanController.class.getName());
    
    // Color constants
    private static final String ORANGE_COLOR = "#ff8c1a";
    private static final String RED_COLOR = "#ff6666";

    public DisplayKanbanController() {
        // Constructeur public requis par JavaFX FXML
    }

    private kanbanCorps corps;

    public void setCore(kanbanCorps Kcorps) {
        this.corps = Kcorps;
    }

    // Core principal (pour récupérer les users connectés / snapshot)
    private MainCore core;

    @FXML
    private Label kanbanTitleLabel;      // label en haut : titre du kanban

    @FXML
    private HBox columnsContainer;       // contient toutes les colonnes

    @FXML
    private Button addColumnButton;      // bouton + à droite

    // Données du modèle
    private LightKanban kanban;
    private List<Column> columns;
    private List<CreateTask> taskCreations;
    private ManageDisplay manageDisplay;

    // Popup courant (pour le fermer quand on en ouvre un autre)
    private Popup currentPopup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // récupération du MainCore pour accéder aux users connectés
        core = MainApp.getCore();
        // on ne fait rien d'autre au chargement : on attend initBoard(...)
    }

    /**
     * Appelée par ManageDisplay pour injecter les objets.
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

    @FXML
    private void handleBack() {
        corps.getMainPort().goHomeView();
    }

    // --------------------------------------------------------------------
    // Construction de l'IHM à partir des objets Java
    // --------------------------------------------------------------------
    private void renderKanban() {
        if (kanban == null || columns == null) return;

        kanbanTitleLabel.setText(kanban.getTitle());

        columnsContainer.getChildren().clear();

        columns.sort(Comparator.comparingInt(Column::getNumber));

        for (Column col : columns) {
            VBox columnNode = createColumnNode(col);
            columnsContainer.getChildren().add(columnNode);
        }
    }

    private VBox createColumnNode(Column col) {
        VBox columnBox = new VBox(10);
        columnBox.setPadding(new Insets(10));
        columnBox.setPrefWidth(260);

        String bgColor = col.getColor() != null ? col.getColor() : "#5D8BF4";
        columnBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");

        // ----- header colonne -----
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);

        Label titleLabel = new Label(col.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label menuLabel = new Label("⋮");
        menuLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
        // On passe la colonne ET le label (ancre pour afficher le popup à côté)
        menuLabel.setOnMouseClicked(e -> onColumnMenuClick(col, menuLabel));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, menuLabel);

        // ----- tâches de cette colonne -----
        VBox tasksBox = new VBox(8);
        UUID colId = col.getId();

        if (taskCreations != null) {
            for (CreateTask ct : taskCreations) {
                if (ct.getTargetColumn().equals(colId)) {
                    Task t = ct.getNewTask();
                    VBox taskCard = createTaskCard(t);
                    tasksBox.getChildren().add(taskCard);
                }
            }
        }

        columnBox.getChildren().addAll(header, tasksBox);
        return columnBox;
    }

    private VBox createTaskCard(Task task) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 6;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);
        Button menuBtn = new Button("⋮");
        // On passe la tâche et le bouton (ancre pour le popup)
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

    // --------------------------------------------------------------------
    // POPUPS
    // --------------------------------------------------------------------

    private void closeCurrentPopup() {
        if (currentPopup != null && currentPopup.isShowing()) {
            currentPopup.hide();
        }
        currentPopup = null;
    }

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

    private Popup createBasePopup() {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setAutoFix(true);

        // Conteneur de base
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

    private void showPopupNearNode(Popup popup, Node anchor) {
        closeCurrentPopup();
        currentPopup = popup;

        Bounds b = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor.getScene().getWindow(), b.getMaxX() + 4, b.getMinY());
    }

    // --------------------------------------------------------------------
    // Bouton + pour créer une colonne
    // --------------------------------------------------------------------

    @FXML
    private void onAddColumnButtonClick() {
        if (addColumnButton != null) {
            showAddColumnPopup(addColumnButton);
        }
    }

    /**
     * Popup ADD COLUMN : COLUMN NAME + COLOR + bouton ADD
     */
    private void showAddColumnPopup(Node anchorNode) {
        
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("ADD COLUMN");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // COLUMN NAME
        Label nameLabel = new Label("COLUMN NAME");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter column name");

        // COLOR
        Label colorLabel = new Label("COLOR");
        colorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        ComboBox<String> colorCombo = new ComboBox<>();
        colorCombo.setItems(FXCollections.observableArrayList(
                "BLUE", "GREEN", "ORANGE", "RED", "PURPLE"
        ));
        colorCombo.getSelectionModel().select("BLUE"); // par défaut

        Button addBtn = new Button("ADD");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle(
                "-fx-background-color: #3cbc4c;" +  // vert
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 6 12 6 12;"
        );

        addBtn.setOnAction(e -> {
            String colTitle = nameField.getText() != null ? nameField.getText().trim() : "";
            String colorName = colorCombo.getSelectionModel().getSelectedItem();

            if (colTitle.isEmpty()) {
                System.out.println("COLUMN NAME vide, aucune colonne créée.");
                return;
            }

            // Conversion nom couleur -> code couleur
            String colorCode = "#5D8BF4"; // défaut BLUE
            if (colorName != null) {
                switch (colorName) {
                    case "BLUE" -> colorCode = "#5D8BF4";
                    case "GREEN" -> colorCode = "#4CAF50";
                    case "ORANGE" -> colorCode = ORANGE_COLOR;
                    case "RED" -> colorCode = RED_COLOR;
                    case "PURPLE" -> colorCode = "#8a2be2";
                    default -> {
                    }
                }
            }

            System.out.println("ADD COLUMN '" + colTitle + "' with color " + colorCode);


            Column col = new Column(colTitle, colorCode);
            CreateColumn modify = new CreateColumn(col);
            corps.getDataPort().getModified((Modification) modify, kanban.getId());
            LOGGER.info("création d'un nouvelle colonne envoyé à data");

            // renderKanban();

            popup.hide();
        });

        box.getChildren().addAll(
                title,
                nameLabel, nameField,
                colorLabel, colorCombo,
                addBtn
        );

        showPopupNearNode(popup, anchorNode);
    }

    // --------------------------------------------------------------------
    // Handlers colonnes / tâches / statut
    // --------------------------------------------------------------------

    /** Menu colonne : ADD TASK / EDIT COLUMN / DELETE COLUMN */
    private void onColumnMenuClick(Column col, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        Button addTask = createMenuButton("ADD TASK", ORANGE_COLOR, "black");
        Button editCol  = createMenuButton("EDIT COLUMN", "#c0c0ff", "black");
        Button deleteCol = createMenuButton("DELETE COLUMN", RED_COLOR, "black");

        addTask.setOnAction(e -> {
            popup.hide();
            showAddTaskPopup(col, anchorNode);
        });

        editCol.setOnAction(e -> {
            popup.hide();
            showEditColumnPopup(col, anchorNode);
        });

        deleteCol.setOnAction(e -> {
            System.out.println("DELETE COLUMN : " + col.getTitle());


            DeleteColumn delete = new DeleteColumn(col.getId());
            corps.getDataPort().getModified((Modification) delete, kanban.getId());
            LOGGER.info("suppression d'une colonne envoyée à data");

            popup.hide();
        });

        box.getChildren().addAll(addTask, editCol, deleteCol);
        showPopupNearNode(popup, anchorNode);
    }

    /** Popup ADD TASK : titre + description + bouton vert ADD */
    private void showAddTaskPopup(Column col, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("ADD TASK");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label nameLabel = new Label("TASK TITLE");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Task title");

        Label descLabel = new Label("DESCRIPTION");
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Enter description...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);

        Button addBtn = new Button("ADD");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle(
                "-fx-background-color: #3cbc4c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 6 12 6 12;"
        );

        addBtn.setOnAction(e -> {
            String taskTitle = nameField.getText() != null ? nameField.getText().trim() : "";
            String taskDesc  = descArea.getText() != null ? descArea.getText().trim() : "";

            if (taskTitle.isEmpty()) {
                System.out.println("Le titre de la tâche est vide, rien créé.");
                return;
            }

            System.out.println("ADD TASK '" + taskTitle + "' dans colonne : " + col.getTitle());


            Task tache = new Task(taskTitle, taskDesc);
            CreateTask modify = new CreateTask(tache, col.getId());
            corps.getDataPort().getModified((Modification) modify, kanban.getId());
            LOGGER.info("Nouvelle Task envoyée à data");

            popup.hide();
        });

        box.getChildren().addAll(
                title,
                nameLabel, nameField,
                descLabel, descArea,
                addBtn
        );

        showPopupNearNode(popup, anchorNode);
    }

    /** Popup EDIT COLUMN : titre + couleur (ComboBox) + bouton EDIT */
    private void showEditColumnPopup(Column col, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("EDIT COLUMN");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label titleLabel = new Label("TITLE");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField titleField = new TextField(col.getTitle());
        titleField.setPromptText("Column title");

        Label colorLabel = new Label("COLOR");
        colorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        ComboBox<String> colorCombo = new ComboBox<>();
        colorCombo.setItems(FXCollections.observableArrayList(
                "BLUE", "GREEN", "ORANGE", "RED", "PURPLE"
        ));
        colorCombo.setEditable(false);

        String currentColor = (col.getColor() != null) ? col.getColor().toUpperCase() : "";
        switch (currentColor) {
            case "#5D8BF4", "BLUE" -> colorCombo.getSelectionModel().select("BLUE");
            case "GREEN" -> colorCombo.getSelectionModel().select("GREEN");
            case "ORANGE" -> colorCombo.getSelectionModel().select("ORANGE");
            case "RED" -> colorCombo.getSelectionModel().select("RED");
            case "PURPLE" -> colorCombo.getSelectionModel().select("PURPLE");
            default -> {
            }
        }

        Button editBtn = new Button("EDIT");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle(
                "-fx-background-color: #3cbc4c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 6 12 6 12;"
        );

        editBtn.setOnAction(e -> {
            String newTitle = titleField.getText() != null ? titleField.getText().trim() : "";
            String newColorName = colorCombo.getSelectionModel().getSelectedItem();

            if (newTitle.isEmpty()) {
                System.out.println("Titre de colonne vide : on ne modifie pas.");
                return;
            }

            String newColorCode = col.getColor();
            if (newColorName != null) {
                switch (newColorName) {
                    case "BLUE" -> newColorCode = "#5D8BF4";
                    case "GREEN" -> newColorCode = "#4CAF50";
                    case "ORANGE" -> newColorCode = ORANGE_COLOR;
                    case "RED" -> newColorCode = RED_COLOR;
                    case "PURPLE" -> newColorCode = "#8a2be2";
                    default -> {
                    }
                }
            }

            System.out.println("EDIT COLUMN '" + col.getTitle() + "' -> '" +
                    newTitle + "', color=" + newColorCode);

            try {
                col.setTitle(newTitle);
            } catch (Exception ex) {
                LOGGER.warning("Impossible d'appeler col.setTitle(...) : adapte ce code à ta classe Column.");
            }
            try {
                col.setColor(newColorCode);
            } catch (Exception ex) {
                LOGGER.warning("Impossible d'appeler col.setColor(...) : adapte ce code à ta classe Column.");
            }


            ModifyColumn modify = new ModifyColumn(col);
            corps.getDataPort().getModified((Modification) modify, kanban.getId());
            LOGGER.info("création d'un nouvelle colonne envoyé à data");


            renderKanban();
            popup.hide();
        });

        box.getChildren().addAll(
                title,
                titleLabel, titleField,
                colorLabel, colorCombo,
                editBtn
        );

        showPopupNearNode(popup, anchorNode);
    }

    /** Menu tâche : ADD A USER / SEE USERS / EDIT / DELETE / COPY */
    private void onTaskMenuClick(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        Button addUser = createMenuButton("ADD A USER", ORANGE_COLOR, "black");
        Button seeUsers = createMenuButton("SEE USERS", "#c0c0ff", "black");
        Button editTask = createMenuButton("EDIT TASK", "#d0d0d0", "black");
        Button deleteTask = createMenuButton("DELETE TASK", RED_COLOR, "black");
        Button copyTask = createMenuButton("COPY TASK", "#bbbbff", "black");

        addUser.setOnAction(e -> {
            popup.hide();
            showAddUserToTaskPopup(task, anchorNode);
        });

        seeUsers.setOnAction(e -> {
            popup.hide();
            showTaskUsersPopup(task, anchorNode);
        });

        editTask.setOnAction(e -> {
            popup.hide();
            showEditTaskPopup(task, anchorNode);
        });

        deleteTask.setOnAction(e -> {
            System.out.println("DELETE TASK : " + task.getTitle());

            DeleteTask delete = new DeleteTask(task.getId());
            corps.getDataPort().getModified((Modification) delete, kanban.getId());
            LOGGER.info("supprimer une tache envoyée à data");

            popup.hide();
        });

        copyTask.setOnAction(e -> {
            System.out.println("COPY TASK : " + task.getTitle());

            // TODO : corps.copyTask(task);
            popup.hide();
        });

        box.getChildren().addAll(addUser, seeUsers, editTask, deleteTask, copyTask);
        showPopupNearNode(popup, anchorNode);
    }

    /** Popup EDIT TASK */
    private void showEditTaskPopup(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("EDIT TASK");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label nameLabel = new Label("NAME");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextField nameField = new TextField(task.getTitle());
        nameField.setPromptText("Task title");

        Label descLabel = new Label("DESCRIPTION");
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        TextArea descArea = new TextArea(task.getDescription());
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setPromptText("Enter description...");

        Button editBtn = new Button("EDIT");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle(
                "-fx-background-color: #3cbc4c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 6 12 6 12;"
        );

        editBtn.setOnAction(e -> {
            String newTitle = nameField.getText() != null ? nameField.getText().trim() : "";
            String newDesc  = descArea.getText() != null ? descArea.getText().trim() : "";

            if (newTitle.isEmpty()) {
                System.out.println("Titre vide : on ne modifie pas la tâche.");
                return;
            }

            System.out.println("EDIT TASK '" + task.getTitle() + "' -> '" + newTitle + "'");

            try {
                task.setTitle(newTitle);
            } catch (Exception ex) {
                LOGGER.warning("Impossible d'appeler task.setTitle(...) : adapte ce code à ta classe Task.");
            }
            try {
                task.setDescription(newDesc);
            } catch (Exception ex) {
                LOGGER.warning("Impossible d'appeler task.setDescription(...) : adapte ce code à ta classe Task.");
            }
            LocalDate start = LocalDate.now();    
            LocalDate end = LocalDate.now().plusDays(7);

            ModifyTask modify = new ModifyTask(task);
            corps.getDataPort().getModified((Modification)modify, kanban.getId());
            LOGGER.info("Taskmodifié envoyé à data");


            renderKanban();
            popup.hide();
        });

        box.getChildren().addAll(
                title,
                nameLabel, nameField,
                descLabel, descArea,
                editBtn
        );

        showPopupNearNode(popup, anchorNode);
    }

    /** Popup ADD A USER (tous les users connectés) */
    private void showAddUserToTaskPopup(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("CONNECTED USERS");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        box.getChildren().add(title);

        List<LightUser> users = null;
        if (core != null) {
            users = core.getUsersSnapshot();
        } else {
            LOGGER.warning("MainCore est null dans DisplayKanbanController, impossible de récupérer les users.");
        }

        if (users == null || users.isEmpty()) {
            Label empty = new Label("No connected users.");
            empty.setStyle("-fx-text-fill: white;");
            box.getChildren().add(empty);
        } else {
            for (LightUser user : users) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
                    Node userCard = loader.load();
                    UserCardController controller = loader.getController();
                    controller.setUserData(user.getUsername(), user.getAvatar());

                    Button addBtn = new Button("ADD");
                    addBtn.setStyle(
                            "-fx-background-color: " + ORANGE_COLOR + ";" +
                                    "-fx-text-fill: black;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 20;" +
                                    "-fx-padding: 2 10 2 10;"
                    );

                    addBtn.setOnAction(ev -> {
                        System.out.println("ADD user " + user.getUsername() +
                                " to task " + task.getTitle());
                        // TODO : corps.assignUserToTask(task, user);
                        popup.hide();
                    });

                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(userCard, Priority.ALWAYS);

                    row.getChildren().addAll(userCard, addBtn);
                    box.getChildren().add(row);

                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE,
                            "Erreur lors du chargement de user_card.fxml pour le popup ADD USER", ex);
                }
            }
        }

        showPopupNearNode(popup, anchorNode);
    }

    /** Popup SEE USERS : users de la tâche avec boutons View / Delete */
    private void showTaskUsersPopup(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);
        box.setSpacing(10);

        Label title = new Label("TASK USERS");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        box.getChildren().add(title);

        List<LightUser> users;
        if (core != null) {
            // TODO : remplacer par "users de la tâche"
            users = core.getUsersSnapshot();
        } else {
            LOGGER.warning("MainCore est null dans DisplayKanbanController, impossible de récupérer les users.");
            users = Collections.emptyList();
        }

        if (users.isEmpty()) {
            Label empty = new Label("No users on this task.");
            empty.setStyle("-fx-text-fill: white;");
            box.getChildren().add(empty);
        } else {
            for (LightUser user : users) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
                    Node userCard = loader.load();
                    UserCardController controller = loader.getController();
                    controller.setUserData(user.getUsername(), user.getAvatar());

                    Button viewBtn = new Button("View");
                    viewBtn.setStyle(
                            "-fx-background-color: " + ORANGE_COLOR + ";" +
                                    "-fx-text-fill: black;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 20;" +
                                    "-fx-padding: 2 10 2 10;"
                    );

                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setStyle(
                            "-fx-background-color: " + RED_COLOR + ";" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 20;" +
                                    "-fx-padding: 2 10 2 10;"
                    );

                    viewBtn.setOnAction(ev -> {
                        System.out.println("VIEW user " + user.getUsername() +
                                " on task " + task.getTitle());
                        // TODO : ouvrir une fiche détaillée
                    });

                    deleteBtn.setOnAction(ev -> {
                        System.out.println("DELETE user " + user.getUsername() +
                                " from task " + task.getTitle());
                        // TODO : corps.unassignUserFromTask(task, user);
                        popup.hide();
                    });

                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(userCard, Priority.ALWAYS);

                    row.getChildren().addAll(userCard, viewBtn, deleteBtn);
                    box.getChildren().add(row);

                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE,
                            "Erreur lors du chargement de user_card.fxml pour le popup SEE USERS", ex);
                }
            }
        }

        showPopupNearNode(popup, anchorNode);
    }

    /** Menu statut : TO DO / DOING / TO REVIEW / DONE */
    private void onStatusClick(Task task, Button statusBtn) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        Button toDo = createMenuButton("TO DO", "#5D8BF4", "white");
        Button doing = createMenuButton("DOING", "#ffb347", "black");
        Button toReview = createMenuButton("TO REVIEW", RED_COLOR, "white");
        Button done = createMenuButton("DONE", "#66cc66", "black");

        toDo.setOnAction(e -> {
            System.out.println("Status TO DO pour : " + task.getTitle());
            statusBtn.setText("TO DO ▼");
            // TODO : corps.updateStatus(task, ...);
            popup.hide();
        });

        doing.setOnAction(e -> {
            System.out.println("Status DOING pour : " + task.getTitle());
            statusBtn.setText("DOING ▼");
            popup.hide();
        });

        toReview.setOnAction(e -> {
            System.out.println("Status TO REVIEW pour : " + task.getTitle());
            statusBtn.setText("TO REVIEW ▼");
            popup.hide();
        });

        done.setOnAction(e -> {
            System.out.println("Status DONE pour : " + task.getTitle());
            statusBtn.setText("DONE ▼");
            popup.hide();
        });

        box.getChildren().addAll(toDo, doing, toReview, done);
        showPopupNearNode(popup, statusBtn);
    }
}
