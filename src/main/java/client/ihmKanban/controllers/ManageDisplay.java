package client.ihmKanban.controllers;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import common.dataClasses.Kanban;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageDisplay {

    private final kanbanCorps corps;

    public ManageDisplay(kanbanCorps corps) {
        this.corps = corps;
    }

    public void openKanbanScreen(Kanban kanban) {
        try {
            // Charger le "shell" kanban complet : menu + users + board
            URL fxmlUrl = MainApp.class.getResource("/kanbanView.fxml");
            corps.LOGGER.info("DEBUG FXML kanbanView");

            if (fxmlUrl == null) {
                throw new IllegalStateException("kanbanView.fxml introuvable dans le classpath !");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Récupérer les colonnes
            List<Column> cols = kanban.getAllColumns();

            // Construire la liste des CreateTask
            List<CreateTask> taskCreations = new ArrayList<>();
            for (Column col : cols) {
                List<Task> tasks = kanban.getTasksFromColumn(col);
                for (Task t : tasks) {
                    taskCreations.add(new CreateTask(t, col.getId()));
                }
            }

            // Récupérer le contrôleur principal kanbanView.fxml
            KanbanViewController controller = loader.getController();
            controller.initBoard(kanban, cols, taskCreations);

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Kanban - " + kanban.getTitle());
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();

        } catch (Exception e) {
            corps.LOGGER.info("Erreur lors de l'ouverture de l'écran Kanban : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
