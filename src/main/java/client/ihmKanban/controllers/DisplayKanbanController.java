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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

public class DisplayKanbanController {

    @FXML private Label kanbanTitle;
    @FXML private HBox columnsContainer;

    @FXML
    public void initialize(){ 
    }

    private Kanban kanban;

    /** Appelé par ton corps lorsqu'on charge le Kanban */
    public void setKanban(Kanban kanban) {
        this.kanban = kanban;
        kanbanTitle.setText(kanban.getTitle());
        loadColumns();
    }

    /** Génère les colonnes dynamiquement */
    private void loadColumns() {
        columnsContainer.getChildren().clear();

        for (Column col : kanban.getAllColumns()) {
            VBox columnBox = createColumnBox(col);
            columnsContainer.getChildren().add(columnBox);
        }
    }

    /** Créé une colonne stylée */
    private VBox createColumnBox(Column col) {
        VBox column = new VBox(15);
        column.setPrefWidth(260);
        column.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-background-radius: 15;");
        column.setFillWidth(true);

        Label title = new Label(col.getTitle());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox tasksBox = new VBox(10);

        for (Task t : kanban.getTasksFromColumn(col)) {
            tasksBox.getChildren().add(createTaskCard(t));
        }

        column.getChildren().addAll(title, tasksBox);

        return column;
    }

    /** Carte de tâche */
    private HBox createTaskCard(Task task) {
        HBox card = new HBox();
        card.setStyle("""
            -fx-background-color: #f2f2f2;
            -fx-padding: 12;
            -fx-background-radius: 12;
            -fx-border-color: #cccccc;
            -fx-border-radius: 12;
        """);

        Label name = new Label(task.getTitle());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        card.getChildren().add(name);

        return card;
    }
}