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

import java.time.LocalDate;
import java.util.ArrayList;
import java.net.URL;
import java.util.List;

public class ManageDisplay {

        private final kanbanCorps corps; 

    public ManageDisplay(kanbanCorps corps) {
        this.corps = corps;
    }


    public void openKanbanScreen(Kanban kanban) {

    try {
        // Charger FXML
        URL fxmlUrl = MainApp.class.getResource("/displayKanban.fxml");
        System.out.println("DEBUG FXML URL = " + fxmlUrl);

        if (fxmlUrl == null) {
            throw new IllegalStateException("displayKanban.fxml introuvable dans le classpath !");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Extraction automatique des colonnes
        List<Column> cols = kanban.getAllColumns();

        // Conversion HashMap<Column, List<Task>> → List<CreateTask>
        List<CreateTask> taskCreations = new ArrayList<>();
        for (Column col : cols) {
            List<Task> tasks = kanban.getTasksFromColumn(col);
            for (Task t : tasks) {
                taskCreations.add(new CreateTask(t, col.getId()));
            }
        }

        // Initialisation du contrôleur
        DisplayKanbanController controller = loader.getController();
        controller.initBoard(kanban, cols, taskCreations);

        // Ouverture de la fenêtre
        Stage stage = new Stage();
        stage.setTitle("Kanban - " + kanban.getTitle());
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Erreur lors de l'ouverture de l'écran Kanban : " + e.getMessage());
    }
}
}