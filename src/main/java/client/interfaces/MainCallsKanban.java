package client.interfaces;

import java.util.List;
import java.util.UUID;
import common.dataClasses.LightUser;
import common.dataClasses.Kanban;

// MainCallsKanban
public interface MainCallsKanban {
    void displaySnapshotList();
    void openCreateForm(Kanban kanban, javafx.scene.control.ScrollPane kanbanArea);

}