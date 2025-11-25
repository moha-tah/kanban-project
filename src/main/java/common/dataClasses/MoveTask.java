package common.dataClasses;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MoveTask extends Modification {
    private UUID taskId;
    private UUID targetColumn;
    
    // Constructeur
    public MoveTask(UUID taskId, UUID targetColumn) {
        super();
        this.taskId = taskId;
        this.targetColumn = targetColumn;
    }
    
    // Constructeur avec ID
    public MoveTask(UUID id, UUID taskId, UUID targetColumn) {
        super(id);
        this.taskId = taskId;
        this.targetColumn = targetColumn;
    }
    
    // Getters
    public UUID getTaskId() {
        return taskId;
    }
    
    public UUID getTargetColumn() {
        return targetColumn;
    }
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    
    public void setTargetColumn(UUID targetColumn) {
        this.targetColumn = targetColumn;
    }
    
    @Override
    public boolean execute() {
        LightKanban myLightKanban = getTargetKanban();
        Kanban myKanban = getTheKanban(myLightKanban);
        HashMap<Column, List<Task>> taskColumn = myKanban.getTaskColumn();
        // Trouver la tâche et sa colonne source
        Task movingTask = null;
        Column sourceColumn = null;
        for (Column col : taskColumn.keySet()) {
            for (Task t : taskColumn.get(col)) {
                if (t.getId().equals(taskId)) {
                    movingTask = t;
                    sourceColumn = col;
                    break;
                }
            }
            if (movingTask != null) break;
        }
        if (movingTask == null || sourceColumn == null) {
            return false; // Pas de modification si la tâche ou la colonne n'est pas trouvée
        }

        // Trouver la colonne de destination
        Column destinationColumn = null;
        for (Column col : taskColumn.keySet()) {
            if (col.getId().equals(targetColumn)) {
                destinationColumn = col;
                break;
            }
        }
        if (destinationColumn == null) {
            return false; // Destination column not found
        }
        User taskUser = getTheUser()
        
        myKanban.moveTask( taskUser ,movingTask, sourceColumn, destinationColumn);
        return true;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler le déplacement de tâche
        // À implémenter selon les règles métier
        return taskId != null && targetColumn != null;
    }
    
    @Override
    public String toString() {
        return "MoveTask{" +
                "id=" + getId() +
                ", taskId=" + taskId +
                ", targetColumn=" + targetColumn +
                '}';
    }
}
