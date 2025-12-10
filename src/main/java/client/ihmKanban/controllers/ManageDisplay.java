package client.ihmKanban.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.Kanban;
import common.dataClasses.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
        kanbanCorps.LOGGER.info("[Kanban] Refresh kanban : ");
    }

    private Parent buildKanbanView(Kanban kanban) throws IOException {
    URL fxmlUrl = MainApp.class.getResource("/kanbanView.fxml");
    kanbanCorps.LOGGER.info("DEBUG FXML kanbanView");

    if (fxmlUrl == null) {
        throw new IllegalStateException("kanbanView.fxml introuvable dans le classpath !");
    }

    FXMLLoader loader = new FXMLLoader(fxmlUrl);
    Parent kanbanView = loader.load();

    // Colonnes
    List<Column> cols = kanban.getAllColumns();

    // Tasks & CreateTask
    List<CreateTask> taskCreations = new ArrayList<>();
    for (Column col : cols) {
        for (Task t : kanban.getTasksFromColumn(col)) {
            taskCreations.add(new CreateTask(t, col.getId()));
        }
    }

    // Controller du kanban
    KanbanViewController controller = loader.getController();
    controller.setCore(corps);
    controller.initBoard(kanban, cols, taskCreations, this);

    return kanbanView;
}

    public void openKanbanScreen(Kanban kanban, HomeViewController homeController) {
    try {
        setHomeViewController(homeController);
        Parent kanbanView = buildKanbanView(kanban);
        homeController.getKanbanArea().setContent(kanbanView);

    } catch (IOException | IllegalStateException e) {
        kanbanCorps.LOGGER.log(Level.INFO, 
            "Erreur lors de l'ouverture de l'écran Kanban : {0}", e.getMessage());
    }
}


    public void openKanbanScreenFromProfile(
        Kanban kanban, 
        client.ihmMain.controllers.ProfileViewController profileController) {
    
    try {
        setHomeViewController(null);
        Parent kanbanView = buildKanbanView(kanban);
        //profileController.getKanbanArea().setContent(kanbanView);

    } catch (IOException | IllegalStateException e) {
        kanbanCorps.LOGGER.log(Level.INFO, 
            "Erreur lors de l'ouverture de l'écran Kanban : {0}", e.getMessage());
    }
}
}