package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class DeleteTask extends Modification {
    private UUID taskId;
    private Task previousTask = null;
    
    // Constructeur
    public DeleteTask(UUID taskId) {
        super();
        this.taskId = taskId;
    }
    
    // Constructeur avec kanban cible
    public DeleteTask(UUID taskId, LightKanban targetKanban) {
        super();
        this.taskId = taskId;
        this.setLightTargetKanban(targetKanban);
    }
    
    // Constructeur avec ID
    public DeleteTask(UUID id, UUID taskId) {
        super(id);
        this.taskId = taskId;
    }
    
    // Getters
    public UUID getTaskId() {
        return taskId;
    }
    public Task getPreviousTask() {
        return previousTask;
    }
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    public void setPreviousTask(Task previousTask) {
        this.previousTask = previousTask;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        //enlever la tâche de la liste de tâches 
        List<Task> tasks = targetKanban.getTasks();
        this.previousTask = tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        
        // Retirer la tâche de toutes les colonnes dans taskColumn AVANT de modifier la liste tasks
        java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
        for (java.util.Map.Entry<Column, List<Task>> entry : taskColumn.entrySet()) {
            List<Task> columnTasks = entry.getValue();
            if (columnTasks != null) {
                columnTasks.removeIf(task -> task.getId().equals(taskId));
                taskColumn.put(entry.getKey(), columnTasks);
            }
        }
        targetKanban.setTaskColumn(taskColumn);
        
        // Supprimer la tâche de la liste
        tasks.removeIf(task -> task.getId().equals(taskId));
        
        // Mettre à jour la liste des tâches
        // setTasks() ne pourra pas réajouter la tâche car elle n'est plus dans taskColumn
        targetKanban.setTasks(tasks);

        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        if (previousTask == null) {
            throw new IllegalStateException("Cannot undo DeleteTask: previousTask is null (task may not have existed at deletion time).");
        }
        CreateTask undoModification = new CreateTask(previousTask, targetKanban.getColumnFromTask(previousTask.getId()).getId());
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "DeleteTask{" +
                "id=" + getId() +
                ", taskId=" + taskId +
                '}';
    }
}
