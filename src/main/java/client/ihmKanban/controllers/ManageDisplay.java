package client.ihmKanban.controllers;

import java.io.IOException;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.Kanban;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ManageDisplay {

    private final kanbanCorps corps; 

    public ManageDisplay(kanbanCorps corps) {
        this.corps = corps;

    }

    // Référence au contrôleur de la vue principale qui appartient à IHM Main
    private HomeViewController homeViewController;

    public void setHomeViewController(HomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

    public HomeViewController getHomeViewController() {
        return homeViewController;
    }

    public void refreshKanban(Kanban kanban) {

        this.openKanbanScreen(kanban, homeViewController);
        corps.LOGGER.info("[Kanban] Refresh kanban : ");
    }

    public void openKanbanScreen(Kanban kanban, HomeViewController homeController) {
        try {
            // Charger le "shell" kanban complet : board
            URL fxmlUrl = MainApp.class.getResource("/kanbanView.fxml");
            kanbanCorps.LOGGER.info("DEBUG FXML kanbanView");

            setHomeViewController(homeController);

            if (fxmlUrl == null) {
                throw new IllegalStateException("kanbanView.fxml introuvable dans le classpath !");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            //Parent root = loader.load();
            Parent kanbanView = loader.load();

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
            controller.setCore(corps);
            controller.initBoard(kanban, cols, taskCreations, this);
            

            // Afficher la fenêtre
            homeController.getKanbanArea().setContent(kanbanView);

        } catch (IOException | IllegalStateException e) {
            corps.LOGGER.log(Level.INFO, "Erreur lors de l''ouverture de l''\u00e9cran Kanban : {0}", e.getMessage());
        }
    }


    public void openKanbanScreenFromProfile(Kanban kanban, client.ihmMain.controllers.ProfileViewController profileController) {
        try {
            // Charger le "shell" kanban complet : board
            URL fxmlUrl = MainApp.class.getResource("/kanbanView.fxml");
            kanbanCorps.LOGGER.info("DEBUG FXML kanbanView");
            
            setHomeViewController(null);

            if (fxmlUrl == null) {
                throw new IllegalStateException("kanbanView.fxml introuvable dans le classpath !");
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent kanbanView = loader.load();

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
            controller.setCore(corps);
            controller.initBoard(kanban, cols, taskCreations, this);
            
            // Afficher la fenêtre
            profileController.getKanbanArea().setContent(kanbanView);

        } catch (IOException | IllegalStateException e) {
            corps.LOGGER.log(Level.INFO, "Erreur lors de l''ouverture de l''\u00e9cran Kanban : {0}", e.getMessage());
        }
    }
}