package client.ihmKanban.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.UsersController;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.LightKanban;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class KanbanViewController implements Initializable {

    // contrôleur de users.fxml injecté grâce au fx:include fx:id="usersInclude"
    @FXML
    private UsersController usersIncludeController;

    // contrôleur de displayKanban.fxml injecté grâce à fx:id="displayKanbanInclude"
    @FXML
    private DisplayKanbanController displayKanbanIncludeController;

    private MainCore core;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        core = MainApp.getCore();

        if (core != null && usersIncludeController != null) {
            usersIncludeController.setCore(core);
            usersIncludeController.refreshUsers();
        }
    }

    public void initBoard(LightKanban kanban,
                          List<Column> columns,
                          List<CreateTask> taskCreations, ManageDisplay manageDisplay) {

        if (displayKanbanIncludeController != null) {
            displayKanbanIncludeController.initBoard(kanban, columns, taskCreations,manageDisplay );
        }
    }
}
