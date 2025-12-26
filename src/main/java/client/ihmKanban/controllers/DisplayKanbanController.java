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
import common.dataClasses.AddUserToTask;
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

public class DisplayKanbanController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DisplayKanbanController.class.getName());
    private static final String WHITE_TEXT = "-fx-text-fill: white;";
    private static kanbanCorps corps;

    public DisplayKanbanController() {}

    public static void setCore(kanbanCorps kcorps) {
        corps = kcorps;
    }

    private MainCore core;

    @FXML private Label kanbanTitleLabel;
    @FXML private HBox columnsContainer;
    @FXML private Button addColumnButton;

    private final Map<UUID, List<LightUser>> taskUsers = new HashMap<>();
    private LightKanban kanban;
    private List<Column> columns;
    private List<CreateTask> taskCreations;
    private ManageDisplay manageDisplay;
    private Popup currentPopup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        core = MainApp.getCore();
    }

    public void initBoard(LightKanban kanban,
                          List<Column> columns,
                          List<CreateTask> taskCreations, ManageDisplay manageDisplay) {
        this.kanban = kanban;
        this.columns = columns;
        this.taskCreations = taskCreations;
        this.manageDisplay = manageDisplay;
        updateTaskUsers();
        renderKanban();
    }
    
    private void updateTaskUsers() {
        // Mettre à jour taskUsers avec les utilisateurs affectés de chaque tâche
        if (corps != null && corps.getCurrentKanban() != null) {
            common.dataClasses.Kanban fullKanban = corps.getCurrentKanban();
            taskUsers.clear();
            for (Task task : fullKanban.getTasks()) {
                if (task.getAffectedUsers() != null && !task.getAffectedUsers().isEmpty()) {
                    taskUsers.put(task.getId(), task.getAffectedUsers());
                }
            }
        }
    }

    @FXML
    private void handleBack() {
        corps.getCommPort().closingKanban(kanban, corps.getMe());
        corps.getMainPort().goHomeView();
    }

    private void renderKanban() {
        if (kanban == null || columns == null) return;

        kanbanTitleLabel.setText(kanban.getTitle());
        columnsContainer.getChildren().clear();
        columns.sort(Comparator.comparingInt(Column::getNumber));

        for (Column col : columns) {
            columnsContainer.getChildren().add(createColumnNode(col));
        }
    }

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

    private void closeCurrentPopup() {
        if (currentPopup != null && currentPopup.isShowing()) currentPopup.hide();
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

    private void showPopupNearNodeWithBounds(Popup popup, Bounds bounds, javafx.stage.Window window) {
        closeCurrentPopup();
        currentPopup = popup;
        popup.show(window, bounds.getMaxX() + 4, bounds.getMinY());
    }

    @FXML
    private void onAddColumnButtonClick() {
        if (addColumnButton != null) showAddColumnPopup(addColumnButton);
    }

    // ----------------------- Refactoring général -----------------------

    private void addPopupButton(VBox box, String text, String bgColor, String textColor, Runnable action) {
        Button btn = createMenuButton(text, bgColor, textColor);
        btn.setOnAction(e -> {
            action.run();
            // Ne pas fermer le popup ici, laisser showPopupNearNode() le faire
        });
        box.getChildren().add(btn);
    }

    // ----------------------- POPUPS -----------------------

    private void onColumnMenuClick(Column col, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        addPopupButton(box, "ADD TASK", "#ff8c1a", "black", () -> showAddTaskPopup(col, anchorNode));
        addPopupButton(box, "EDIT COLUMN", "#c0c0ff", "black", () -> showEditColumnPopup(col, anchorNode));
        addPopupButton(box, "DELETE COLUMN", "#ff6666", "black", () -> {
            LOGGER.log(Level.INFO, "DELETE COLUMN : {0}", col.getTitle());
            corps.getCommPort().sendRequestModification(corps.getMe(), new DeleteColumn(col.getId(), kanban));
        });

        showPopupNearNode(popup, anchorNode);
    }

    private void onTaskMenuClick(Task task, Node anchorNode) {
        Popup popup = createBasePopup();
        VBox box = (VBox) popup.getContent().get(0);

        addPopupButton(box, "ADD USER", "#ff8c1a", "black", () -> showAddUserToTaskPopup(task, anchorNode));
        addPopupButton(box, "SEE USERS", "#c0c0ff", "black", () -> showTaskUsersPopup(task, anchorNode));
        addPopupButton(box, "EDIT TASK", "#d0d0d0", "black", () -> showEditTaskPopup(task, anchorNode));
        addPopupButton(box, "DELETE TASK", "#ff6666", "black", () -> {
            LOGGER.log(Level.INFO, "DELETE TASK : {0}", task.getTitle());
            corps.getCommPort().sendRequestModification(corps.getMe(), new DeleteTask(task.getId(), kanban));
        });
        showPopupNearNode(popup, anchorNode);
    }

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
                corps.getCommPort().sendRequestModification(corps.getMe(), new MoveTask(task.getId(), col.getId(), kanban));
            });
        }
        showPopupNearNode(popup, statusBtn);
    }

    // ----------------------- Ajout / édition colonnes et tâches -----------------------

    private void showAddColumnPopup(Node anchorNode) {
        showGenericColumnPopup(anchorNode, "ADD COLUMN", null, (title, color) -> {
            int colNum = corps.getDataPort().getLocalKanban().getColumns().size();
            Column col = new Column(title, color, colNum);
            corps.getCommPort().sendRequestModification(corps.getMe(), new CreateColumn(col, kanban));
            LOGGER.log(Level.INFO, "Nouvelle colonne créée : {0}", title);
        });
    }

    private void showEditColumnPopup(Column col, Node anchorNode) {
        showGenericColumnPopup(anchorNode, "EDIT COLUMN", col, (title, color) -> {
            col.setTitle(title);
            col.setColor(color);
            corps.getCommPort().sendRequestModification(corps.getMe(), new ModifyColumn(col, kanban));
            renderKanban();
            LOGGER.log(Level.INFO, "Colonne modifiée : {0}", title);
        });
    }

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

    // ----------------------- Tâches -----------------------

    private void showAddTaskPopup(Column col, Node anchorNode) {
        // Capturer les bounds et la fenêtre avant de fermer le popup du menu
        Bounds bounds = anchorNode.localToScreen(anchorNode.getBoundsInLocal());
        javafx.stage.Window window = anchorNode.getScene().getWindow();
        
        showGenericTaskPopup("ADD TASK", null, col, bounds, window, (taskTitle, taskDesc) -> {
            Task tache = new Task(taskTitle, taskDesc);
            // Mise à jour optimiste : ajouter la tâche localement pour affichage immédiat
            if (taskCreations == null) {
                taskCreations = new java.util.ArrayList<>();
            }
            CreateTask createTask = new CreateTask(tache, col.getId());
            taskCreations.add(createTask);
            renderKanban(); // Afficher immédiatement la nouvelle tâche
            // Envoyer la modification au serveur
            corps.getCommPort().sendRequestModification(corps.getMe(), new CreateTask(tache, col.getId(), kanban));
            LOGGER.log(Level.INFO, "Nouvelle tâche créée : {0}", taskTitle);
        });
    }

    private void showEditTaskPopup(Task task, Node anchorNode) {
        showGenericTaskPopup("EDIT TASK", task, null, anchorNode, (taskTitle, taskDesc) -> {
            task.setTitle(taskTitle);
            task.setDescription(taskDesc);
            corps.getCommPort().sendRequestModification(corps.getMe(), new ModifyTask(task, kanban));
            renderKanban();
        });
    }
    
    private void showGenericTaskPopup(String popupTitle, Task taskToEdit, Column col, Node anchorNode,
                                      BiConsumer<String, String> onSubmit) {
        showGenericTaskPopup(popupTitle, taskToEdit, col, 
            anchorNode.localToScreen(anchorNode.getBoundsInLocal()), 
            anchorNode.getScene().getWindow(), onSubmit);
    }
    
    private void showGenericTaskPopup(String popupTitle, Task taskToEdit, Column col, Bounds bounds, javafx.stage.Window window,
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
        showPopupNearNodeWithBounds(popup, bounds, window);
    }

    // ----------------------- Utilisateurs -----------------------

    private void showAddUserToTaskPopup(Task task, Node anchorNode) {
        showUsersPopup(task, anchorNode, true);
    }

    private void showTaskUsersPopup(Task task, Node anchorNode) {
        showUsersPopup(task, anchorNode, false);
    }

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
                            corps.getCommPort().sendRequestModification(corps.getMe(), new AddUserToTask(task.getId(), user.getId(), kanban));
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
