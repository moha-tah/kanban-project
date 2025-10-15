package common.src.dataClasses;
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
        // Logique pour exécuter le déplacement de tâche
        // À implémenter selon les règles métier
        return taskId != null && targetColumn != null;
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
