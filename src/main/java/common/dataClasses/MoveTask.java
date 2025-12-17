package common.dataClasses;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MoveTask extends Modification {
    private UUID taskId;
    private UUID targetColumn;
    private UUID previousColumn = null;
    
    // Constructeur
    public MoveTask(UUID taskId, UUID targetColumn) {
        super();
        this.taskId = taskId;
        this.targetColumn = targetColumn;
    }
    
    // Constructeur avec kanban cible
    public MoveTask(UUID taskId, UUID targetColumn, LightKanban targetKanban) {
        super();
        this.taskId = taskId;
        this.targetColumn = targetColumn;
        this.setLightTargetKanban(targetKanban);
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

    public UUID getPreviousColumn() {
        return previousColumn;
    }
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    
    public void setTargetColumn(UUID targetColumn) {
        this.targetColumn = targetColumn;
    }

    public void setPreviousColumn(UUID previousColumn) {
        this.previousColumn = previousColumn;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
        this.previousColumn = taskColumn.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(task -> task.getId().equals(taskId)))
                .map(entry -> entry.getKey().getId())
                .findFirst()
                .orElse(null);
        Task taskToMove = null;
        //Supprimer la valeur de l'ancienne colonne
        for (Column col: taskColumn.keySet()) {
            List<Task> taskList = taskColumn.get(col);
            taskToMove = taskList.stream()
                    .filter(task -> task.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
            if (taskToMove != null) {
                taskList.removeIf(task -> task.getId().equals(taskId));
                taskColumn.put(col, taskList);
                break;
            }
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
    public Kanban undo(Kanban targetKanban) {
        MoveTask undoModification = new MoveTask(taskId, previousColumn);
        return undoModification.execute(targetKanban);
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
