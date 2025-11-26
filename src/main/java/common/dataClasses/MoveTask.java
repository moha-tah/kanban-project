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
    public Kanban execute(Kanban targetKanban) {
        HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
        Task taskToMove = null;
        //Supprimer la valeur de l'ancienne colonne
        for (Column col: taskColumn.keySet()) {
            List<Task> taskList = taskColumn.get(col);
            taskToMove = taskList.stream()
                    .filter(task -> task.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
            taskList.removeIf(task -> task.getId().equals(taskId));
            taskColumn.put(col, taskList);
        }
        //Ajouter la valeur à la nouvelle colonne
        for (Column col: taskColumn.keySet()){
            if(col.getId().equals(targetColumn)){
                List<Task> taskList = taskColumn.get(col);
                if (taskToMove != null) {
                    taskList.add(taskToMove);
                }
                taskColumn.put(col, taskList);
                break;
            }
        }
        targetKanban.setTaskColumn(taskColumn);   
        return targetKanban;
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
