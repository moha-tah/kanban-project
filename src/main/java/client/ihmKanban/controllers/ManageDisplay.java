package client.ihmKanban.controllers;

import client.MainApp;
import common.dataClasses.LightKanban;
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

    public ManageDisplay() {}

    public void openKanbanScreen(LightKanban lk,
                                 List<Column> cols,
                                 List<CreateTask> taskCreations) throws Exception {

        URL fxmlUrl = MainApp.class.getResource("/display-kanban.fxml");
        System.out.println("DEBUG FXML URL = " + fxmlUrl);

        if (fxmlUrl == null) {
            throw new IllegalStateException("display-kanban.fxml introuvable dans le classpath !");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        DisplayKanbanController controller = loader.getController();
        controller.initBoard(lk, cols, taskCreations);

        Stage stage = new Stage();
        stage.setTitle("Kanban - " + lk.getTitle());
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    // pour tester
    public void openDemoKanban() throws Exception {
        LightKanban lk = new LightKanban("Mon Kanban de Test");

        List<Column> cols = new ArrayList<>();
        Column todo   = new Column("TO DO",   "#5D8BF4", 1);
        Column doing  = new Column("DOING",   "#f06a10", 2);
        Column done   = new Column("DONE",    "#0ec20e", 3);
        cols.add(todo);
        cols.add(doing);
        cols.add(done);

        List<CreateTask> tasks = new ArrayList<>();
        tasks.add(new CreateTask(
                new Task("Créer maquette", "Faire les écrans principaux",
                        LocalDate.now(), LocalDate.now().plusDays(3)),
                todo.getId()));

        tasks.add(new CreateTask(
                new Task("Implémenter backend", "Coder les endpoints",
                        LocalDate.now(), LocalDate.now().plusDays(5)),
                doing.getId()));

        tasks.add(new CreateTask(
                new Task("Réunion finale", "Revue du projet",
                        LocalDate.now(), LocalDate.now().plusDays(7)),
                done.getId()));

        openKanbanScreen(lk, cols, tasks);
    }
}
