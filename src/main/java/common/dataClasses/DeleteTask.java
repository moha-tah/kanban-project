package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class DeleteTask extends Modification {
    private UUID taskId;
    
    // Constructeur
    public DeleteTask(UUID taskId) {
        super();
        this.taskId = taskId;
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
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        //enlever la tâche de la liste de tâches 
        List<Task> tasks = targetKanban.getTasks();
        tasks.removeIf(task -> task.getId().equals(taskId));
        targetKanban.setTasks(tasks);

        //récupérer la colonne de la tâche
        Column col = targetKanban.getColumnFromTask(taskId);

        //mettre à jour le hashmap en enlevant la tâche pour sa colonne
        targetKanban.modifyHashmap(tasks, col);

        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la suppression de tâche
        // À implémenter selon les règles métier
        return taskId != null;
    }
    
    @Override
    public String toString() {
        return "DeleteTask{" +
                "id=" + getId() +
                ", taskId=" + taskId +
                '}';
    }
}
