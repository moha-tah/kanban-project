package common.src.dataClasses;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class Kanban extends LightKanban {
    private HashMap<Column, List<Task>> taskColumn;
    
    // Constructeur
    public Kanban(String title) {
        super(title);
        this.taskColumn = new HashMap<>();
    }
    
    // Constructeur avec ID
    public Kanban(UUID id, String title) {
        super(id, title);
        this.taskColumn = new HashMap<>();
    }
    
    // Getters
    public HashMap<Column, List<Task>> getTaskColumn() {
        return taskColumn;
    }
    
    // Setters
    public void setTaskColumn(HashMap<Column, List<Task>> taskColumn) {
        this.taskColumn = taskColumn;
    }
    
    // Méthodes métier
    public boolean canBeModifiedBy(LightUser user) {
        // Logique pour vérifier si l'utilisateur peut modifier le kanban
        // À implémenter selon les règles métier
        return true; // Placeholder
    }
    
    public boolean modifyKanban(LightUser user, String newTitle) {
        if (canBeModifiedBy(user)) {
            setTitle(newTitle);
            return true;
        }
        return false;
    }
    
    public boolean addTaskToColumn(LightUser user, Task task, Column column) {
        if (canBeModifiedBy(user)) {
            taskColumn.computeIfAbsent(column, k -> new ArrayList<>()).add(task);
            return true;
        }
        return false;
    }
    
    public boolean moveTask(LightUser user, Task task, Column fromColumn, Column toColumn) {
        if (canBeModifiedBy(user)) {
            List<Task> fromTasks = taskColumn.get(fromColumn);
            List<Task> toTasks = taskColumn.get(toColumn);
            
            if (fromTasks != null && fromTasks.contains(task)) {
                fromTasks.remove(task);
                if (toTasks != null) {
                    toTasks.add(task);
                } else {
                    taskColumn.put(toColumn, new ArrayList<>());
                    taskColumn.get(toColumn).add(task);
                }
                return true;
            }
        }
        return false;
    }
    
    // Méthode utilitaire pour obtenir toutes les tâches d'une colonne
    public List<Task> getTasksFromColumn(Column column) {
        return taskColumn.getOrDefault(column, new ArrayList<>());
    }
    
    // Méthode utilitaire pour obtenir toutes les colonnes
    public List<Column> getAllColumns() {
        return new ArrayList<>(taskColumn.keySet());
    }
}
