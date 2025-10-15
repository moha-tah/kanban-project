import java.util.List;

// Placeholders pour les types utilisés dans les interfaces

// DataCallsKanban
public interface DataCallsKanban {
    void displayKanban(Kanban kanban);
}

// MainCallsKanban
public interface MainCallsKanban {
    void openCreateFrom();
    void displaySnapshotList();
}

// CommCallsKanban
public interface CommCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
}

