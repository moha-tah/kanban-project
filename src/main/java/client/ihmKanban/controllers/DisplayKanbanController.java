package client.ihmKanban.controllers;

import common.dataClasses.LightKanban;
import common.dataClasses.Column;
import common.dataClasses.Task;
import common.dataClasses.CreateTask;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class DisplayKanbanController implements Initializable {

    @FXML
    private Label kanbanTitleLabel;      // label en haut : titre du kanban

    @FXML
    private HBox columnsContainer;       // contient toutes les colonnes

    // Données du modèle
    private LightKanban kanban;
    private List<Column> columns;
    private List<CreateTask> taskCreations;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // on ne fait rien au chargement : on attend initBoard(...)
    }

    /**
     * Appelée par ManageDisplay pour injecter les objets.
     */
    public void initBoard(LightKanban kanban,
                          List<Column> columns,
                          List<CreateTask> taskCreations) {

        this.kanban = kanban;
        this.columns = columns;
        this.taskCreations = taskCreations;

        renderKanban();
    }

    // --------------------------------------------------------------------
    // Construction de l'IHM à partir des objets Java
    // --------------------------------------------------------------------
    private void renderKanban() {
        if (kanban == null || columns == null) return;

        kanbanTitleLabel.setText(kanban.getTitle());

        columnsContainer.getChildren().clear();

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
        menuLabel.setOnMouseClicked(e -> onColumnMenuClick(col, columnBox));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

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
        menuBtn.setOnAction(e -> onTaskMenuClick(task, card));
        topRow.getChildren().add(menuBtn);

        Label title = new Label(task.getTitle());
        title.setStyle("-fx-font-weight: bold;");

        Label desc = new Label(task.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");

        Button statusBtn = new Button("STATUS");
        statusBtn.setStyle("-fx-background-color: #ffa500; -fx-text-fill: white; -fx-font-size: 10;");
        statusBtn.setOnAction(e -> onStatusClick(task, statusBtn));

        card.getChildren().addAll(topRow, title, desc, statusBtn);
        return card;
    }

    // ---- Handlers basiques (tu pourras les relier à tes popups) ----
    /* 
    private void onColumnMenuClick(Column col, VBox columnNode) {
        System.out.println("Menu colonne : " + col.getTitle());
    }

    private void onTaskMenuClick(Task task, VBox cardNode) {
        System.out.println("Menu tâche : " + task.getTitle());
    }

    private void onStatusClick(Task task, Button statusBtn) {
        System.out.println("Changer état de : " + task.getTitle());
    } */
}
